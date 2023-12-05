package com.jamal_aliev.navigationcontroller.core

import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType

data class TransactionData(
    val transitionType: TransitionType,
    val destinationType: DestinationType,
    val screenClassFrom: Class<out Screen>?,
    val screenClassTo: Class<out Screen>?
)