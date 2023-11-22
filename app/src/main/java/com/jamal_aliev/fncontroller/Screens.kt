package com.jamal_aliev.fncontroller

import me.aartikov.alligator.Screen
import java.io.Serializable

object Screens {

    data class ColorFragmentScreen(val color: Long) : Screen, Serializable

}