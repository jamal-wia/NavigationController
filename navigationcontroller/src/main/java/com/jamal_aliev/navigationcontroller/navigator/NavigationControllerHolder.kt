package com.jamal_aliev.navigationcontroller.navigator

import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.navigationfactories.NavigationFactory

object NavigationControllerHolder {

    private var navigator: AndroidNavigator? = null
    fun requireNavigator() = requireNotNull(navigator) { "Navigator is null" }

    fun createNavigator(navigationFactory: NavigationFactory) {
        navigator = AndroidNavigator(navigationFactory)
    }
}