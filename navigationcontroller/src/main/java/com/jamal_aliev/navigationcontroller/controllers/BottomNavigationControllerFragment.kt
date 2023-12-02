package com.jamal_aliev.navigationcontroller.controllers

import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireNavigationContextChanger
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.ScreenResolver
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.exceptions.ScreenNotFoundException

open class BottomNavigationControllerFragmentScreen(
    @MenuRes open val menuId: Int,
    open val rootScreen: SwitchScreen,
    open val screen1: SwitchScreen? = null,
    open val screen2: SwitchScreen? = null,
    open val screen3: SwitchScreen? = null,
    open val screen4: SwitchScreen? = null,
    open val screen5: SwitchScreen? = null,
) : SwitchNavigationControllerFragmentScreen() {

    init {
        check(
            screen1 == rootScreen
                    || screen2 == rootScreen
                    || screen3 == rootScreen
                    || screen4 == rootScreen
                    || screen5 == rootScreen
        )
    }

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

    private val args: BottomNavigationControllerFragmentScreen
            by lazy { screenResolver.getScreen(this) }
    private val menuId: Int by lazy { args.menuId }
    private val rootScreen by lazy { args.rootScreen }
    private val screen1 by lazy { args.screen1 }
    private val screen2 by lazy { args.screen2 }
    private val screen3 by lazy { args.screen3 }
    private val screen4 by lazy { args.screen4 }
    private val screen5 by lazy { args.screen5 }
    private val tabScreens by lazy {
        hashMapOf<Int, SwitchScreen>()
            .apply {
                screen1?.let { put(it.id, it) }
                screen2?.let { put(it.id, it) }
                screen3?.let { put(it.id, it) }
                screen4?.let { put(it.id, it) }
                screen5?.let { put(it.id, it) }
            }
    }

    override fun canGoBack() = super.canGoBack() || currentScreen.id != rootScreen.id

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        return if (super.canGoBack()) super.onNavigationUp(animationData)
        else if (currentScreen.id != rootScreen.id) {
            backStack.removeLast()
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(rootScreen, animationData) == Unit
        } else false
    }

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            navigator.switchTo(rootScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = view.findViewById(R.id.bottom_navigation)
        bottomNavigation.apply {
            menu.clear()
            inflateMenu(menuId)
            setOnItemSelectedListener { onScreenSelected(it.itemId).run { true } }
        }
    }

    override fun onSwitchScreen(screenFrom: SwitchScreen?, screenTo: SwitchScreen) {
        super.onSwitchScreen(screenFrom, screenTo)
        bottomNavigation.menu.findItem(screenTo.id).isChecked = true
    }

    /**
     * Выполняет переключение экрана
     * */
    open fun onScreenSelected(screenId: Int) {
        val wantToSwitch = tabScreens.getValue(screenId)
        if (wantToSwitch.id != currentScreen.id) {
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(wantToSwitch)
        } else if (currentScreen is LineNavigationControllerFragmentScreen) {
            try {
                val rootNavControllerScreen = currentScreen
                        as LineNavigationControllerFragmentScreen
                val rootScreen = rootNavControllerScreen.screens.first()

                requireNavigationContextChanger()
                    .setNavigationContextAfter(this) { true }
                navigator.goBackTo(rootScreen)
            } catch (e: ScreenNotFoundException) {
                // goBackTo может фантомно выдать ошибку
            } catch (e: Exception) {
                requireNavigationContextChanger().setNavigationContext(this)
                navigator.switchTo(rootScreen)
            }
        }
    }
}