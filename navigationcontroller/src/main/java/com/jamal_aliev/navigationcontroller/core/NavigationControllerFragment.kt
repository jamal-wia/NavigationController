package com.jamal_aliev.navigationcontroller.core

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.controller.NavigationController
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.findFragmentAfter
import com.jamal_aliev.navigationcontroller.util.findFragmentBefore
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import kotlinx.coroutines.delay
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.animations.AnimationData
import java.io.Serializable

class NavigationControllerFragment : Fragment(R.layout.container),
    NavigationController,
    NavigationContextChanger,
    NavigationContextProvider,
    DialogInterface.OnDismissListener,
    OnNavigationUpProvider {

    var rootScreenArg: Screen? = null
        get() {
            return if (field != null) field
            else {
                (arguments?.getSerializable(ROOT_SCREEN_ARG_KEY)
                    ?: arguments?.getParcelable(ROOT_SCREEN_ARG_KEY)) as? Screen
            }
        }

    private val showRootScreen by lazy {
        arguments?.getBoolean(
            SHOW_ROOT_SCREEN_ARG_KEY,
            true
        ) ?: true
    }

    private val navigator get() = NavigationControllerHolder.requireNavigator()
    private val navigationFactory get() = navigator.navigationFactory

    private val navigationContext by lazy {
        NavigationContext.Builder(requireAppCompatActivity(), navigationFactory)
            .fragmentNavigation(childFragmentManager, R.id.container)
            .build()
    }

    override fun provideNavigationContext(): NavigationContext = navigationContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            if (showRootScreen) rootScreenArg?.let { navigator.reset(it) }
        } else {
            val rootScreen = (savedInstanceState.getSerializable(ROOT_SCREEN_ARG_KEY)
                ?: savedInstanceState.getParcelable(ROOT_SCREEN_ARG_KEY)) as? Screen
            if (rootScreen != null) rootScreenArg = rootScreen
        }

        if (rootScreenArg == null) {
            rootScreenArg = (arguments?.getSerializable(ROOT_SCREEN_ARG_KEY)
                ?: arguments?.getParcelable(ROOT_SCREEN_ARG_KEY)) as? Screen
        }

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

    override fun onSaveInstanceState(outState: Bundle) {
        when (val rootScreen = rootScreenArg) {
            is Serializable -> outState.putSerializable(ROOT_SCREEN_ARG_KEY, rootScreen)
            is Parcelable -> outState.putParcelable(ROOT_SCREEN_ARG_KEY, rootScreen)
        }

        super.onSaveInstanceState(outState)
    }

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
                    && it != this
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
                    && it != this
                    && predicate(it)
        } as? NavigationContextProvider

        return navigationContextProvider?.let {
            navigator.bind(it.provideNavigationContext())
        } != null
    }

    override fun canGoBack(): Boolean = false

    override fun onNavigationUp(animationData: AnimationData?): Boolean {

        fun resetToRootScreen(): Boolean {
            return if (rootScreenArg != null) {
                setNavigationContext(this)
                navigator.reset(rootScreenArg!!) == Unit
            } else false
        }

        val lastNavigationFragment =
            findFragmentAfter(
                fragments = requireActivity().supportFragmentManager.fragments,
                predicate = fun(fragment): Boolean {
                    return fragment is OnNavigationUpProvider
                            && fragment != this
                            && fragment.childFragmentManager
                        .fragments.all { it !is OnNavigationUpProvider }
                }
            ) ?: return resetToRootScreen()
        val canGoBackNavigationFragment =
            findFragmentBefore(
                fragment = lastNavigationFragment,
                predicate = fun(fragment): Boolean {
                    return (fragment as? OnNavigationUpProvider)
                        ?.canGoBack() == true
                }
            ) ?: return resetToRootScreen()


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
                            && fragment != this
                            && fragment.childFragmentManager
                        .fragments.all { it !is OnNavigationUpProvider }
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

    private companion object {
        private const val ROOT_SCREEN_ARG_KEY =
            "com.jamal_aliev.navigationcontroller.ROOT_SCREEN_ARG_KEY"

        private const val SHOW_ROOT_SCREEN_ARG_KEY =
            "com.jamal_aliev.navigationcontroller.SHOW_ROOT_SCREEN_ARG_KEY"
    }

    class Builder() {

        private var rootScreen: Screen? = null
        private var showRootScreen: Boolean = true

        fun setRootScreen(screen: Screen, show: Boolean = true): Builder {
            check(screen is Serializable || screen is Parcelable)
            rootScreen = screen
            showRootScreen = show
            return this
        }

        fun show(fragmentManager: FragmentManager, containerId: Int) {
            fragmentManager.beginTransaction()
                .add(
                    containerId,
                    NavigationControllerFragment()
                        .apply {
                            arguments = bundleOf(
                                ROOT_SCREEN_ARG_KEY to rootScreenArg,
                                SHOW_ROOT_SCREEN_ARG_KEY to showRootScreen
                            )
                        }
                )
                .commitNow()
        }
    }
}
