package com.jamal_aliev.fncontroller.core

import com.jamal_aliev.fncontroller.core.provider.ContainerProvider
import com.jamal_aliev.fncontroller.core.provider.NavigationScreenSwitcherProvider
import com.jamal_aliev.fncontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.fncontroller.core.screen.SwitchScreen

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface SwitchNavigationControllerContract :
    ContainerProvider,
    OnNavigationUpProvider,
    NavigationScreenSwitcherProvider {
    fun onSwitchScreen(
        screenFrom: SwitchScreen?,
        screenTo: SwitchScreen
    ) {
    }
}