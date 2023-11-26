package com.jamal_aliev.navigationcontroller.core

import androidx.fragment.app.Fragment

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface NavigationContextChanger {
    fun setNavigationContext(fragment: Fragment? = null)
    fun setFirstNavigationContext()
    fun setLastNavigationContext(lastFragment: Fragment? = null)
    fun setFirstTabNavigationContext()
    fun setLastTabNavigationContext(lastFragment: Fragment? = null)
    fun defaultNavigationContext(afterFragment: Fragment? = null)
    fun resetNavigationContext()
}
