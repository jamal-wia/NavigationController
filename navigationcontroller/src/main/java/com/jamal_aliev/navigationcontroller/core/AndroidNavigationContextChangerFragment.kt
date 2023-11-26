package com.jamal_aliev.navigationcontroller.core

import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerBottomDialog
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerDialog
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.provider.ContainerProvider
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.core.provider.NavigationScreenSwitcherProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import kotlinx.coroutines.delay
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.providers.TransitionAnimationProvider
import me.aartikov.alligator.exceptions.MissingScreenSwitcherException

class AndroidNavigationContextChangerFragment : Fragment(R.layout.container),
    DialogInterface.OnDismissListener,
    NavigationContextChanger,
    OnNavigationUpProvider {

    private val navigator = NavigationControllerHolder.requireNavigator()
    private val navigationFactory = navigator.navigationFactory

    private var isFirstStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isFirstStart = savedInstanceState == null
        if (isFirstStart) navigator.reset(LineNavigationControllerFragmentScreen())
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
        bindNavigationContext()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        navigator.unbind(requireAppCompatActivity())
        lifecycleScope.launchWhenResumed {
            delay(timeMillis = 1L) // Перед bindNavigationContext необходима задержка иначе сработает некорректно
            bindNavigationContext()
        }
    }

    override fun onPause() {
        super.onPause()
        navigator.unbind(requireAppCompatActivity())
    }

    override fun onNavigationUp(animationData: AnimationData?) {
        if (requireAppCompatActivity().onBackPressedDispatcher.hasEnabledCallbacks()) {
            requireAppCompatActivity().onBackPressedDispatcher.onBackPressed()
            return
        }
        val lastNavigationFragment = getAnyLastNavigationFragment(childFragmentManager.fragments)
        if (lastNavigationFragment == null) {
            resetNavigationContext()
            navigator.reset(LineNavigationControllerFragmentScreen())
            return
        }
        val canGoBackNavigationFragment = getCanGoBackLastFragment(lastNavigationFragment)
        return when {
            canGoBackNavigationFragment != lastNavigationFragment
                    && (lastNavigationFragment is NavigationControllerBottomDialog || lastNavigationFragment is NavigationControllerDialog) -> {
                navigator.goBack()
            }

            canGoBackNavigationFragment != null && canGoBackNavigationFragment is NavigationControllerContract -> {
                canGoBackNavigationFragment.onNavigationUp(animationData)
            }

            else -> {
                resetNavigationContext()
                navigator.reset(LineNavigationControllerFragmentScreen())
            }
        }
    }

    private fun onBackPressed() {
        val lastNavigationFragment = getAnyLastNavigationFragment(childFragmentManager.fragments)
        if (lastNavigationFragment == null) {
            navigator.goBack()
            return
        }
        when (val canGoBackNavigationFragment =
            getCanGoBackLastFragment(lastNavigationFragment)) {

            is OnNavigationUpProvider -> {
                canGoBackNavigationFragment.onNavigationUp()
            }

            else -> {
                navigator.goBack()
            }
        }
    }

    override fun setNavigationContext(fragment: Fragment?) {
        try {
            bindNavigationContext(fragment)
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    override fun setFirstNavigationContext() {
        try {
            bindNavigationContext(getFirstNavigationFragment(childFragmentManager.fragments))
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    override fun setLastNavigationContext(lastFragment: Fragment?) {
        try {
            bindNavigationContext(getLastNavigationFragment(lastFragment!!))
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    override fun setFirstTabNavigationContext() {
        try {
            bindNavigationContext(getFirstTabNavigationFragment(childFragmentManager.fragments))
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    override fun setLastTabNavigationContext(lastFragment: Fragment?) {
        try {
            bindNavigationContext(getLastTabNavigationFragment(lastFragment!!))
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    override fun defaultNavigationContext(afterFragment: Fragment?) {
        bindNavigationContext(afterFragment?.let {
            getAnyLastNavigationFragment(it.childFragmentManager.fragments)
        })
    }

    override fun resetNavigationContext() {
        try {
            bindNavigationContext(resetNavigationContext = true)
        } catch (e: MissingScreenSwitcherException) {
            defaultNavigationContext()
        }
    }

    private fun bindNavigationContext(
        navigationFragment: Fragment? = null,
        resetNavigationContext: Boolean = false,
    ) {
        if (navigationFragment is NavigationContextProvider) {
            return navigator.bind(navigationFragment.getNavigationContext())
        }
        val lastNavigationFragment =
            navigationFragment ?: getAnyLastNavigationFragment(childFragmentManager.fragments)
        if (lastNavigationFragment is NavigationContextProvider) {
            return navigator.bind(lastNavigationFragment.getNavigationContext())
        }
        val navigationContext =
            NavigationContext.Builder(requireAppCompatActivity(), navigationFactory)
                .fragmentNavigation(childFragmentManager, R.id.container)
                .transitionListener { _, _, _, _ ->
//                Возможно лишний код
//                if (getAnyLastNavigationFragment(supportFragmentManager.fragments) != null) {
//                    bindNavigationContext()
//                }
                }
        if (resetNavigationContext) {
            return navigator.bind(navigationContext.build())
        }
        if (lastNavigationFragment is WebViewNavigationControllerFragment) {
            val preLastNavigationFragment = getLastNavigationFragment(lastNavigationFragment)
            return bindNavigationContext(preLastNavigationFragment)
        }
        if (lastNavigationFragment is ContainerProvider) {
            navigationContext.fragmentNavigation(
                lastNavigationFragment.childFragmentManager, lastNavigationFragment.getContainerId()
            )
        }
        if (lastNavigationFragment is TransitionAnimationProvider) {
            navigationContext.transitionAnimationProvider(lastNavigationFragment)
        }
        when (lastNavigationFragment) {
            is NavigationScreenSwitcherProvider -> {
                navigationContext.screenSwitcher(lastNavigationFragment.getScreenSwitcher())
                    .screenSwitchingListener { screenFrom, screenTo ->
                        if (getAnyLastNavigationFragment(childFragmentManager.fragments) != null) {
                            bindNavigationContext()
                        }
                        lastNavigationFragment.onSwitchScreen(screenFrom, screenTo)
                    }
            }

            is NavigationControllerContract -> {
                navigationContext.transitionListener {
                        transitionType, destinationType,
                        screenClassFrom, screenClassTo,
                    ->
//                    Возможно лишний код
//                    if (getAnyLastNavigationFragment(supportFragmentManager.fragments) != null) {
//                        bindNavigationContext()
//                    }
                    lastNavigationFragment.onTransactionScreen(
                        transitionType = transitionType,
                        destinationType = destinationType,
                        screenClassFrom = screenClassFrom,
                        screenClassTo = screenClassTo
                    )
                }
            }
        }
        navigator.bind(navigationContext.build())
    }

    private fun getFirstNavigationFragment(fragments: List<Fragment>): Fragment? {
        val navigationFragment = fragments.find { it is NavigationControllerContract }
        if (navigationFragment != null) return navigationFragment
        for (item in fragments) {
            return getFirstNavigationFragment(item.childFragmentManager.fragments)
        }
        return null
    }

    private fun getLastNavigationFragment(lastFragment: Fragment): Fragment? {
        val parentFragment = lastFragment.parentFragment ?: return null
        return if (parentFragment is NavigationControllerContract) parentFragment
        else getLastNavigationFragment(parentFragment)
    }

    private fun getAnyLastNavigationFragment(fragments: List<Fragment>): Fragment? {
        val navigationFragment = fragments.findLast {
            it is NavigationControllerContract || it is SwitchNavigationControllerContract
        }

        val innerFragments = navigationFragment?.childFragmentManager?.fragments
        val haveInnerNavigation = innerFragments?.any {
            it is NavigationControllerContract || it is SwitchNavigationControllerContract
        } ?: false

        return if (haveInnerNavigation && navigationFragment != null) {
            getAnyLastNavigationFragment(navigationFragment.childFragmentManager.fragments)
        } else {
            navigationFragment
        }
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

    private fun getFirstTabNavigationFragment(fragments: List<Fragment>): Fragment? {
        val tabNavigationFragment = fragments.find { it is SwitchNavigationControllerContract }
        if (tabNavigationFragment != null) return tabNavigationFragment
        fragments.forEach { getFirstTabNavigationFragment(it.childFragmentManager.fragments) }
        return null
    }

    private fun getLastTabNavigationFragment(lastFragment: Fragment): Fragment? {
        val parentFragment = lastFragment.parentFragment ?: return null
        return if (parentFragment is SwitchNavigationControllerContract) parentFragment
        else getLastTabNavigationFragment(parentFragment)
    }

    private companion object {
        private const val CONTAINER_ID_INIT_VALUE = -1
        private const val CONTAINER_ID_KEY = "container_id"
    }
}