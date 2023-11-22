package com.jamal_aliev.fncontroller

import com.jamal_aliev.fncontroller.core.FNRegistryNavigationFactory

class AppRegistryNavigationFactory : FNRegistryNavigationFactory() {
    init {
        registerFragment(Screens.ColorFragmentScreen::class.java, ColorFragment::class.java)
    }
}