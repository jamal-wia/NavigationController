package com.jamal_aliev.fncontroller

import android.app.Application
import com.jamal_aliev.fncontroller.navigator.FNNavigatorHolder
import me.aartikov.alligator.AndroidNavigator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        FNNavigatorHolder.createNavigator(AppRegistryNavigationFactory())
        navigator = FNNavigatorHolder.requireNavigator()
    }

    companion object {
        lateinit var navigator: AndroidNavigator
    }
}