package com.jamal_aliev.navigationcontroller.core.provider

import me.aartikov.alligator.animations.AnimationData

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface OnNavigationUpProvider {
    fun canGoBack(): Boolean = false
    fun onNavigationUp(animationData: AnimationData? = null)
}