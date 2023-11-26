package com.jamal_aliev.navigationcontroller

import android.app.Application
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import me.aartikov.alligator.AndroidNavigator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        NavigationControllerHolder.createNavigator(AppRegistryNavigationControllerFactory())
        navigator = NavigationControllerHolder.requireNavigator()
    }

    companion object {
        lateinit var navigator: AndroidNavigator
    }
}