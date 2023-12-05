package com.jamal_aliev.navigationcontroller

import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.NavigationControllerFactory

class AppRegistryNavigationControllerFactory : NavigationControllerFactory() {
    init {
        registerFragment(
            Screens.ColorFragmentScreen::class.java,
            ColorFragment::class.java
        )
        registerFragment(
            Screens.Tab1LineNavigationController::class.java,
            LineNavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab2LineNavigationController::class.java,
            LineNavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab3LineNavigationController::class.java,
            LineNavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab4LineNavigationController::class.java,
            LineNavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab5LineNavigationController::class.java,
            LineNavigationControllerFragment::class.java
        )
    }
}