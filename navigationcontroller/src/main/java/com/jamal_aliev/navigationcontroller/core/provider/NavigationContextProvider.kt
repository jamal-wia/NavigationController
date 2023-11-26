package com.jamal_aliev.navigationcontroller.core.provider

import me.aartikov.alligator.NavigationContext

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
fun interface NavigationContextProvider {
    fun getNavigationContext(): NavigationContext
}
