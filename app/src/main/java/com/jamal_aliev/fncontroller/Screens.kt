package com.jamal_aliev.fncontroller

import com.jamal_aliev.fncontroller.controllers.NavigationControllerFragmentScreen
import com.jamal_aliev.fncontroller.controllers.TabNavigationControllerFragmentScreen
import com.jamal_aliev.fncontroller.core.screen.SwitchScreen
import me.aartikov.alligator.Screen
import java.io.Serializable

object Screens {

    data class ColorFragmentScreen(val color: Long) : Screen, Serializable

    object Tab1NavigationController : NavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x000000))
    ), SwitchScreen {
        override val id: Int = R.id.item1
    }

    object Tab2NavigationController : NavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0xFF0000))
    ), SwitchScreen {
        override val id: Int = R.id.item2
    }

    object Tab3NavigationController : NavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x2FFF00))
    ), SwitchScreen {
        override val id: Int = R.id.item3
    }

    object Tab4NavigationController : NavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x0037FF))
    ), SwitchScreen {
        override val id: Int = R.id.item4
    }

    object Tab5NavigationController : NavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0xFFFFFF))
    ), SwitchScreen {
        override val id: Int = R.id.item5
    }

    object AppTabNavigationControllerScreen : TabNavigationControllerFragmentScreen(
        menuId = R.menu.main_navigation,
        screens = listOf(
            Tab1NavigationController,
            Tab2NavigationController,
            Tab3NavigationController,
            Tab4NavigationController,
            Tab5NavigationController
        )
    )
}