package com.jamal_aliev.navigationcontroller.core

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import kotlinx.coroutines.delay
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.animations.AnimationData

class NavigationContextChangerFragment : Fragment(R.layout.container),
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
        return searchFirstNavigationContextProvider(fragments, predicate)
            ?.let { navigator.bind(it.provideNavigationContext()) } != null
    }

    override fun setNavigationContextBefore(
        fragment: Fragment,
        predicate: (Fragment) -> Boolean
    ): Boolean {
        return searchBeforeNavigationContextProvider(fragment, predicate)
            ?.let { navigator.bind(it.provideNavigationContext()) } != null
    }

    override fun canGoBack(): Boolean = false

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        val lastNavigationFragment =
            searchLastNavigationContextProvider(
                requireActivity().supportFragmentManager.fragments
            ) { true } ?: return false
        val canGoBackNavigationFragment = getCanGoBackLastFragment(
            lastNavigationFragment as Fragment
        )
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
            searchLastNavigationContextProvider(
                requireActivity().supportFragmentManager.fragments
            ) { true } ?: return navigator.goBack()
        val canGoBackNavigationFragment = getCanGoBackLastFragment(
            lastNavigationFragment as Fragment
        )
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
        val actualNavigationContext = searchLastNavigationContextProvider(
            fragments = requireActivity().supportFragmentManager.fragments
        ) { true }?.provideNavigationContext()
            ?: navigationContext
        navigator.bind(actualNavigationContext)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        navigator.unbind(requireAppCompatActivity())
        lifecycleScope.launchWhenResumed {
            delay(timeMillis = 1L) // Перед bindNavigationContext необходима задержка иначе сработает некорректно
            val actualNavigationContext = searchLastNavigationContextProvider(
                fragments = requireActivity().supportFragmentManager.fragments
            ) { true }?.provideNavigationContext()
                ?: navigationContext
            navigator.bind(actualNavigationContext)
        }
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind(requireAppCompatActivity())
    }

    private fun searchFirstNavigationContextProvider(
        fragments: List<Fragment>,
        predicate: (Fragment) -> Boolean
    ): NavigationContextProvider? {
        fragments.findLast {
            it is NavigationContextProvider
                    && predicate(it)
        }?.let { fragment ->
            return (fragment.takeIf { it != this } as? NavigationContextProvider)
                ?: searchFirstNavigationContextProvider(
                    fragments = fragment.childFragmentManager.fragments,
                    predicate = predicate
                )
        }

        for (item in fragments) {
            return searchFirstNavigationContextProvider(
                fragments = item.childFragmentManager.fragments,
                predicate = predicate
            )
        }

        return null
    }

    private fun searchLastNavigationContextProvider(
        fragments: List<Fragment>,
        predicate: (Fragment) -> Boolean
    ): NavigationContextProvider? {
        val navigationContextProviderFragment =
            fragments.findLast {
                it is NavigationContextProvider
                        && predicate(it)
            } ?: return null
        if (navigationContextProviderFragment == this) {
            searchLastNavigationContextProvider(
                fragments = navigationContextProviderFragment.childFragmentManager.fragments,
                predicate = predicate
            )
        }

        navigationContextProviderFragment.childFragmentManager.fragments
            .find {
                it is NavigationContextProvider
                        && predicate(it)
            }
            ?.let {
                return searchLastNavigationContextProvider(
                    fragments = navigationContextProviderFragment.childFragmentManager.fragments,
                    predicate = predicate
                )
            }

        return navigationContextProviderFragment as NavigationContextProvider
    }

    private fun searchBeforeNavigationContextProvider(
        fragment: Fragment,
        predicate: (Fragment) -> Boolean
    ): NavigationContextProvider? {
        val navigationContextProvider = (fragment.takeIf {
            it is NavigationContextProvider
                    && predicate(it)
        } ?: fragment.parentFragment?.let {
            searchBeforeNavigationContextProvider(
                fragment = it,
                predicate = predicate
            )
        }) as? NavigationContextProvider
        return navigationContextProvider.takeIf { it != this }
    }

    private fun getCanGoBackLastFragment(lastNavigationFragment: Fragment): Fragment? {
        when {
            lastNavigationFragment !is OnNavigationUpProvider
                    && lastNavigationFragment.parentFragment == null -> {
                return null
            }

            lastNavigationFragment !is OnNavigationUpProvider
                    && lastNavigationFragment.parentFragment != null -> {
                getCanGoBackLastFragment(lastNavigationFragment.requireParentFragment())
            }
        }

        return when {
            lastNavigationFragment is OnNavigationUpProvider
                    && lastNavigationFragment.canGoBack() -> {
                lastNavigationFragment
            }

            lastNavigationFragment is OnNavigationUpProvider
                    && !lastNavigationFragment.canGoBack()
                    && lastNavigationFragment.parentFragment != null -> {
                getCanGoBackLastFragment(lastNavigationFragment.requireParentFragment())
            }

            else -> null
        }
    }

    companion object {
        fun show(fragmentManager: FragmentManager, containerId: Int) {
            fragmentManager.beginTransaction()
                .add(containerId, NavigationContextChangerFragment())
                .commitNow()
        }
    }
}
