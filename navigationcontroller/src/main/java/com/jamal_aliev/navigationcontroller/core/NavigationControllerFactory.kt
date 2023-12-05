package com.jamal_aliev.navigationcontroller.core

import com.jamal_aliev.navigationcontroller.controllers.BottomNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.BottomNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerBottomDialog
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerBottomDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerDialog
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerDialogScreen
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.SwitchNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.controllers.WebViewNavigationControllerFragmentScreen
import me.aartikov.alligator.navigationfactories.RegistryNavigationFactory

/**
 * Класс для установки связей между объектами screen и реальными андройдовскими представлениями
 * например - Activity, Fragment, DialogFragment
 * */
open class NavigationControllerFactory : RegistryNavigationFactory() {
    init {
        registerDialogFragment(
            LineNavigationControllerBottomDialogScreen::class.java,
            LineNavigationControllerBottomDialog::class.java
        )
        registerDialogFragment(
            LineNavigationControllerDialogScreen::class.java,
            LineNavigationControllerDialog::class.java
        )
        registerFragment(
            LineNavigationControllerFragmentScreen::class.java,
            LineNavigationControllerFragment::class.java
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