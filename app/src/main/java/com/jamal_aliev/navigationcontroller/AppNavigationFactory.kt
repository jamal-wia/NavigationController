package com.jamal_aliev.navigationcontroller

import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.NavigationControllerFactory

class AppRegistryNavigationControllerFactory : NavigationControllerFactory() {
    init {
        registerFragment(Screens.ColorFragmentScreen::class.java, ColorFragment::class.java)

        registerFragment(
            Screens.Tab1NavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab2NavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab3NavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab4NavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.Tab5NavigationController::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            Screens.AppTabNavigationControllerScreen::class.java,
            AppTabNavigationControllerFragment::class.java
        )
    }
}