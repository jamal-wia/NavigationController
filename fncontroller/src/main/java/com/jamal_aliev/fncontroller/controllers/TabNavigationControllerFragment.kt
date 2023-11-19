package com.jamal_aliev.fncontroller.controllers

import androidx.annotation.MenuRes
import com.jamal_aliev.fncontroller.core.screen.SwitchScreen
import com.jamal_aliev.fncontroller.navigator.FNNavigatorHolder
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.ScreenResolver

open class TabNavigationControllerScreen(
    @MenuRes val menuId: Int,
    override val screens: List<SwitchScreen> = ArrayList()
) : SwitchNavigationControllerScreen(screens) {

    override fun hashCode(): Int = menuId

    override fun equals(other: Any?): Boolean {
        return this.menuId == (other as? TabNavigationControllerScreen)?.menuId
    }
}

abstract class TabNavigationControllerFragment : SwitchNavigationControllerFragment() {

    private val navigator: AndroidNavigator get() = FNNavigatorHolder.requireNavigator()
    private val screenResolver: ScreenResolver get() = navigator.screenResolver

    private val args: TabNavigationControllerScreen by lazy { screenResolver.getScreen(this) }
    protected val menuId: Int by lazy { args.menuId }

}