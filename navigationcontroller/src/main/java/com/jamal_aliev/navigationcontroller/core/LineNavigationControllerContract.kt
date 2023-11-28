package com.jamal_aliev.navigationcontroller.core

import com.jamal_aliev.navigationcontroller.core.provider.ContainerProvider
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
interface LineNavigationControllerContract : NavigationController,
    ContainerProvider,
    OnNavigationUpProvider {
    fun onTransactionScreen(
        transitionType: TransitionType,
        destinationType: DestinationType,
        screenClassFrom: Class<out Screen>?,
        screenClassTo: Class<out Screen>?
    ) {
    }
}