package com.jamal_aliev.fncontroller.navigator

import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.navigationfactories.NavigationFactory

object FNNavigatorHolder {

    private var navigator: AndroidNavigator? = null
    fun requireNavigator() = requireNotNull(navigator) { "Navigator is null" }

    fun createNavigator(navigationFactory: NavigationFactory) {
        navigator = AndroidNavigator(navigationFactory)
    }
}