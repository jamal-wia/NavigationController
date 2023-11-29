package com.jamal_aliev.navigationcontroller.core

import com.jamal_aliev.navigationcontroller.controllers.BottomNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.BottomNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerBottomDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerBottomDialog
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerDialog
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragmentScreen
import me.aartikov.alligator.navigationfactories.RegistryNavigationFactory

open class NavigationControllerFactory : RegistryNavigationFactory() {
    init {
        registerDialogFragment(
            LineNavigationControllerBottomDialogScreen::class.java,
            NavigationControllerBottomDialog::class.java
        )
        registerDialogFragment(
            LineNavigationControllerDialogScreen::class.java,
            NavigationControllerDialog::class.java
        )
        registerFragment(
            LineNavigationControllerFragmentScreen::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            SwitchNavigationControllerFragmentScreen::class.java,
            SwitchNavigationControllerFragment::class.java
        )
        registerFragment(
            BottomNavigationControllerFragmentScreen::class.java,
            BottomNavigationControllerFragment::class.java
        )
        registerFragment(
            WebViewNavigationControllerFragmentScreen::class.java,
            WebViewNavigationControllerFragment::class.java
        )
    }
}