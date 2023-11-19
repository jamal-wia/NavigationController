package com.jamal_aliev.fncontroller.core.provider

import me.aartikov.alligator.NavigationContext

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
fun interface NavigationContextProvider {
    fun getNavigationContext(): NavigationContext
}
