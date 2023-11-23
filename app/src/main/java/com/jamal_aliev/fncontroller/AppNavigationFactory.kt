package com.jamal_aliev.fncontroller

import com.jamal_aliev.fncontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.fncontroller.core.FNRegistryNavigationFactory

class AppRegistryNavigationFactory : FNRegistryNavigationFactory() {
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