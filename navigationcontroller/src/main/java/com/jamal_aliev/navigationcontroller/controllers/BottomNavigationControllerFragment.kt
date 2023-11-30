package com.jamal_aliev.navigationcontroller.controllers

import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.ScreenResolver

open class BottomNavigationControllerFragmentScreen(
    @MenuRes open val menuId: Int,
    open val screen1: SwitchScreen? = null,
    open val screen2: SwitchScreen? = null,
    open val screen3: SwitchScreen? = null,
    open val screen4: SwitchScreen? = null,
    open val screen5: SwitchScreen? = null,
) : SwitchNavigationControllerFragmentScreen(
    screens = arrayListOf<SwitchScreen>()
        .apply {
            screen1?.let { add(it) }
            screen2?.let { add(it) }
            screen3?.let { add(it) }
            screen4?.let { add(it) }
            screen5?.let { add(it) }

            check(this.getOrNull(0) == screen1)
            check(this.getOrNull(1) == screen2)
            check(this.getOrNull(2) == screen3)
            check(this.getOrNull(3) == screen4)
            check(this.getOrNull(4) == screen5)
        }
) {

    override fun hashCode(): Int = menuId

    override fun equals(other: Any?): Boolean {
        return this.menuId == (other as? BottomNavigationControllerFragmentScreen)?.menuId
    }
}

open class BottomNavigationControllerFragment : SwitchNavigationControllerFragment(
    layoutRes = R.layout.fragment_bottom_navigation_controller
) {

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val screenResolver: ScreenResolver get() = navigator.screenResolver

    private val args: BottomNavigationControllerFragmentScreen by lazy {
        screenResolver.getScreen(this)
    }
    protected val menuId: Int by lazy { args.menuId }

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        bottomNavigation.apply {
            menu.clear()
            inflateMenu(args.menuId)
            setOnItemSelectedListener { onScreenSelected(it.itemId).run { true } }
        }
    }

    override fun onSwitchScreen(screenFrom: SwitchScreen?, screenTo: SwitchScreen) {
        super.onSwitchScreen(screenFrom, screenTo)
        bottomNavigation.menu.findItem(screenTo.id).isChecked = true
    }
}