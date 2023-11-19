package com.jamal_aliev.fncontroller.core.provider

import me.aartikov.alligator.animations.AnimationData

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface OnNavigationUpProvider {
    fun canGoBack(): Boolean = true
    fun onNavigationUp(animationData: AnimationData? = null)
}