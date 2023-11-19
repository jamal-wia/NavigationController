package com.jamal_aliev.fncontroller.core.provider

import me.aartikov.alligator.Screen
import me.aartikov.alligator.screenswitchers.ScreenSwitcher

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface NavigationScreenSwitcherProvider {
    fun getScreenSwitcher(): ScreenSwitcher
    fun onSwitchScreen(screenFrom: Screen?, screenTo: Screen) {}
}