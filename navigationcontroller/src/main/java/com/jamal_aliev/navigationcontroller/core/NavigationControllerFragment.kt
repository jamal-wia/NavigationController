package com.jamal_aliev.navigationcontroller.core

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.controller.NavigationController
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import kotlinx.coroutines.delay
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.animations.AnimationData

class NavigationControllerFragment : Fragment(R.layout.container),
    NavigationController,
    NavigationContextChanger,
    NavigationContextProvider,
    DialogInterface.OnDismissListener,
    OnNavigationUpProvider {

    private val navigator get() = NavigationControllerHolder.requireNavigator()
    private val navigationFactory get() = navigator.navigationFactory

    private val navigationContext by lazy {
        NavigationContext.Builder(requireAppCompatActivity(), navigationFactory)
            .fragmentNavigation(childFragmentManager, R.id.container)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher
            .addCallback(
                owner = this,
                object : OnBackPressedCallback(enabled = true) {
                    override fun handleOnBackPressed() {
                        onBackPressed()
                    }
                }
            )
    }

    override fun onResume() {
        super.onResume()
        val success = setNavigationContextAfter(null, predicate = fun(_) = true)
        if (!success) setNavigationContext(this)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        navigator.unbind(requireAppCompatActivity())
        lifecycleScope.launchWhenResumed {
            delay(timeMillis = 1L) // Перед bindNavigationContext необходима задержка иначе сработает некорректно

            val success = setNavigationContextAfter(null, predicate = fun(_) = true)
            if (!success) setNavigationContext(this@NavigationControllerFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind(requireAppCompatActivity())
    }

    override fun provideNavigationContext(): NavigationContext = navigationContext

    override fun setNavigationContext(
        navigationContextProvider: NavigationContextProvider
    ): Boolean {
        navigator.bind(navigationContextProvider.provideNavigationContext())
        return true
    }

    override fun setNavigationContextAfter(
        fragment: Fragment?,
        predicate: (Fragment) -> Boolean
    ): Boolean {
        val fragments = fragment?.childFragmentManager?.fragments
            ?: requireActivity().supportFragmentManager.fragments

        val navigationContextProvider = findFragmentAfter(fragments) {
            it is NavigationContextProvider
                    && predicate(it)
        } as? NavigationContextProvider

        return navigationContextProvider?.let {
            navigator.bind(it.provideNavigationContext())
        } != null
    }

    override fun setNavigationContextBefore(
        fragment: Fragment,
        predicate: (Fragment) -> Boolean
    ): Boolean {
        val navigationContextProvider = findFragmentBefore(fragment) {
            it is NavigationContextProvider
                    && predicate(it)
        } as? NavigationContextProvider

        return navigationContextProvider?.let {
            navigator.bind(it.provideNavigationContext())
        } != null
    }

    override fun canGoBack(): Boolean = false

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        val lastNavigationFragment =
            findFragmentAfter(
                fragments = requireActivity().supportFragmentManager.fragments,
                predicate = fun(fragment): Boolean {
                    return fragment is OnNavigationUpProvider
                            && fragment.childFragmentManager
                        .fragments.any { it !is OnNavigationUpProvider }
                }
            ) ?: return false
        val canGoBackNavigationFragment =
            findFragmentBefore(
                fragment = lastNavigationFragment,
                predicate = fun(fragment): Boolean {
                    return (fragment as? OnNavigationUpProvider)
                        ?.canGoBack() == true
                }
            ) ?: return false


        return when {
            canGoBackNavigationFragment != lastNavigationFragment
                    && lastNavigationFragment is DialogFragment -> {
                navigator.goBack() == Unit
            }

            canGoBackNavigationFragment is OnNavigationUpProvider -> {
                canGoBackNavigationFragment.onNavigationUp(animationData)
            }

            else -> false
        }
    }

    private fun onBackPressed() {
        val lastNavigationFragment =
            findFragmentAfter(
                fragments = requireActivity().supportFragmentManager.fragments,
                predicate = fun(fragment): Boolean {
                    return fragment is OnNavigationUpProvider
                            && fragment.childFragmentManager
                        .fragments.any { it !is OnNavigationUpProvider }
                }
            ) ?: return navigator.goBack()
        val canGoBackNavigationFragment =
            findFragmentBefore(
                fragment = lastNavigationFragment,
                predicate = fun(fragment): Boolean {
                    return (fragment as? OnNavigationUpProvider)
                        ?.canGoBack() == true
                }
            ) ?: return navigator.goBack()

        when {
            canGoBackNavigationFragment != lastNavigationFragment
                    && lastNavigationFragment is DialogFragment -> {
                navigator.goBack()
            }

            canGoBackNavigationFragment is OnNavigationUpProvider -> {
                canGoBackNavigationFragment.onNavigationUp()
            }

            else -> {
                navigator.goBack()
            }
        }
    }

    private fun findFragmentAfter(
        fragments: List<Fragment>,
        predicate: (Fragment) -> Boolean
    ): Fragment? {
        fragments.findLast(predicate)
            ?.let { fragment ->
                return fragment.takeIf { it != this }
                    ?: findFragmentAfter(
                        fragments = fragment.childFragmentManager.fragments,
                        predicate = predicate
                    )
            }

        for (item in fragments) {
            return findFragmentAfter(
                fragments = item.childFragmentManager.fragments,
                predicate = predicate
            )
        }

        return null
    }

    private fun findFragmentBefore(
        fragment: Fragment,
        predicate: (Fragment) -> Boolean
    ): Fragment? {
        val result = fragment.takeIf(predicate)
            ?: fragment.parentFragment?.let { parent ->
                findFragmentBefore(
                    fragment = parent,
                    predicate = predicate
                )
            }
        return result.takeIf { it != this }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, containerId: Int) {
            fragmentManager.beginTransaction()
                .add(containerId, NavigationControllerFragment())
                .commitNow()
        }
    }
}
