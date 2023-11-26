package com.jamal_aliev.navigationcontroller.core

import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerBottomDialog
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerBottomDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerDialog
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.NavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.TabNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.TabNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragmentScreen
import me.aartikov.alligator.navigationfactories.RegistryNavigationFactory

open class NavigationControllerFactory : RegistryNavigationFactory() {
    init {
        registerDialogFragment(
            NavigationControllerBottomDialogScreen::class.java,
            NavigationControllerBottomDialog::class.java
        )
        registerDialogFragment(
            NavigationControllerDialogScreen::class.java,
            NavigationControllerDialog::class.java
        )
        registerFragment(
            NavigationControllerFragmentScreen::class.java,
            NavigationControllerFragment::class.java
        )
        registerFragment(
            SwitchNavigationControllerFragmentScreen::class.java,
            SwitchNavigationControllerFragment::class.java
        )
        registerFragment(
            TabNavigationControllerFragmentScreen::class.java,
            TabNavigationControllerFragment::class.java
        )
        registerFragment(
            WebViewNavigationControllerFragmentScreen::class.java,
            WebViewNavigationControllerFragment::class.java
        )
    }
}