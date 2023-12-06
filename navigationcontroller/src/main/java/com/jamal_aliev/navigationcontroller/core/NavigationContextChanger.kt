package com.jamal_aliev.navigationcontroller.core

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.controller.LineNavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.controller.SwitchNavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface NavigationContextChanger {
    fun setNavigationContext(navigationContextProvider: NavigationContextProvider): Boolean

    /**
     * @param fragment a fragment is a fragment after which the search will be. If fragment is null then search will be from activity
     * */
    fun setNavigationContextAfter(fragment: Fragment?, predicate: (Fragment) -> Boolean): Boolean

    fun setNavigationContextBefore(fragment: Fragment, predicate: (Fragment) -> Boolean): Boolean

    object AnyNavigationContext : (Fragment) -> Boolean {
        override fun invoke(p1: Fragment): Boolean {
            return p1 is NavigationContextProvider
        }
    }

    object LineNavigationContext : (Fragment) -> Boolean {
        override fun invoke(p1: Fragment): Boolean {
            return p1 is LineNavigationControllerContract
        }
    }

    object SwitchNavigationContext : (Fragment) -> Boolean {
        override fun invoke(p1: Fragment): Boolean {
            return p1 is SwitchNavigationControllerContract
        }
    }

    object DialogNavigationContext : (Fragment) -> Boolean {
        override fun invoke(p1: Fragment): Boolean {
            return p1 is DialogFragment && AnyNavigationContext(p1)
        }
    }
}

