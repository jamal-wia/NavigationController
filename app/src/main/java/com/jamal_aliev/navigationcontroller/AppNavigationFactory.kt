package com.jamal_aliev.navigationcontroller

import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.NavigationControllerFactory

class AppRegistryNavigationControllerFactory : NavigationControllerFactory() {
    init {
        registerFragment(Screens.ColorFragmentScreen::class.java, ColorFragment::class.java)

        registerFragment(
            Screens.Tab1LineNavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab2LineNavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab3LineNavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab4LineNavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab5LineNavigationController::class.java,
            NavigationControllerFragment::class.java
        )
    }
}