package com.jamal_aliev.navigationcontroller.core.controller

import com.jamal_aliev.navigationcontroller.core.provider.ContainerProvider
import com.jamal_aliev.navigationcontroller.core.provider.NavigationScreenSwitcherProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen

/**
 *
 * */
interface SwitchNavigationControllerContract :
    NavigationController,
    ContainerProvider,
    OnNavigationUpProvider,
    NavigationScreenSwitcherProvider {
    fun onSwitchScreen(
        screenFrom: SwitchScreen?,
        screenTo: SwitchScreen
    ) {
    }
}