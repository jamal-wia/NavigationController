package com.jamal_aliev.navigationcontroller.core

import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface NavigationContextChanger {
    fun setNavigationContext(navigationContextProvider: NavigationContextProvider)

    /**
     * @param fragment a fragment is a fragment after which the search will be. If fragment is null then search will be from activity
     * */
    fun setNavigationContextAfter(fragment: Fragment?, predicate: (Fragment) -> Boolean): Boolean
    fun setNavigationContextBefore(fragment: Fragment, predicate: (Fragment) -> Boolean): Boolean
}

