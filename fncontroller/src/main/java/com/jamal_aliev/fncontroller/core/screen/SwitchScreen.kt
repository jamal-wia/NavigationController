package com.jamal_aliev.fncontroller.core.screen

import me.aartikov.alligator.Screen
import java.io.Serializable

interface SwitchScreen : Screen, Serializable {
    val id: Int
}
