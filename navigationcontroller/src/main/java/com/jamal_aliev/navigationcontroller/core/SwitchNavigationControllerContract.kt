package com.jamal_aliev.navigationcontroller.core

import com.jamal_aliev.navigationcontroller.core.provider.ContainerProvider
import com.jamal_aliev.navigationcontroller.core.provider.NavigationScreenSwitcherProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
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