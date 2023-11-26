package com.jamal_aliev.navigationcontroller

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jamal_aliev.navigationcontroller.controllers.TabNavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder

class AppTabNavigationControllerFragment : TabNavigationControllerFragment(
    layoutRes = R.layout.fragment_app_tab_navigation_controller
) {

    private val args: Screens.AppTabNavigationControllerScreen by lazy {
        NavigationControllerHolder.requireNavigator().screenResolver.getScreen(this)
    }

    override fun getContainerId(): Int = R.id.container

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(view: View) {
        bottomNavigationView = view.findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.apply {
            menu.clear()
            inflateMenu(args.menuId)
            setOnItemSelectedListener { onScreenSelected(it.itemId).run { true } }
        }
    }

    override fun onSwitchScreen(screenFrom: SwitchScreen?, screenTo: SwitchScreen) {
        super.onSwitchScreen(screenFrom, screenTo)
        bottomNavigationView.menu.findItem(screenTo.id).isChecked = true
    }
}