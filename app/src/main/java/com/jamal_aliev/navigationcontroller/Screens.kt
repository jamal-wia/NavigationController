package com.jamal_aliev.navigationcontroller

import com.jamal_aliev.navigationcontroller.controllers.BottomNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen
import me.aartikov.alligator.Screen
import java.io.Serializable

object Screens {

    data class ColorFragmentScreen(val color: Long) : Screen, Serializable

    object Tab1LineNavigationController : LineNavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x000000))
    ), SwitchScreen {
        override val id: Int = R.id.item1
    }

    object Tab2LineNavigationController : LineNavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0xFF0000))
    ), SwitchScreen {
        override val id: Int = R.id.item2
    }

    object Tab3LineNavigationController : LineNavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x2FFF00))
    ), SwitchScreen {
        override val id: Int = R.id.item3
    }

    object Tab4LineNavigationController : LineNavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0x0037FF))
    ), SwitchScreen {
        override val id: Int = R.id.item4
    }

    object Tab5LineNavigationController : LineNavigationControllerFragmentScreen(
        screens = listOf(ColorFragmentScreen(0xFFFFFF))
    ), SwitchScreen {
        override val id: Int = R.id.item5
    }

    val AppBottomNavigationControllerScreen = BottomNavigationControllerFragmentScreen(
        menuId = R.menu.main_navigation,
        screens = listOf(
            Tab1LineNavigationController,
            Tab2LineNavigationController,
            Tab3LineNavigationController,
            Tab4LineNavigationController,
            Tab5LineNavigationController
        )
    )
}