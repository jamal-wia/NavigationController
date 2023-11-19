package com.jamal_aliev.fncontroller.core

import com.jamal_aliev.fncontroller.controllers.NavigationControllerBottomDialog
import com.jamal_aliev.fncontroller.controllers.NavigationControllerBottomDialogScreen
import com.jamal_aliev.fncontroller.controllers.NavigationControllerDialog
import com.jamal_aliev.fncontroller.controllers.NavigationControllerDialogScreen
import com.jamal_aliev.fncontroller.controllers.NavigationControllerFragment
import com.jamal_aliev.fncontroller.controllers.NavigationControllerFragmentScreen
import com.jamal_aliev.fncontroller.controllers.SwitchNavigationControllerFragment
import com.jamal_aliev.fncontroller.controllers.SwitchNavigationControllerFragmentScreen
import com.jamal_aliev.fncontroller.controllers.TabNavigationControllerFragment
import com.jamal_aliev.fncontroller.controllers.TabNavigationControllerFragmentScreen
import com.jamal_aliev.fncontroller.controllers.WebViewNavigationControllerFragment
import com.jamal_aliev.fncontroller.controllers.WebViewNavigationControllerFragmentScreen
import me.aartikov.alligator.navigationfactories.RegistryNavigationFactory

open class FNRegistryNavigationFactory : RegistryNavigationFactory() {
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