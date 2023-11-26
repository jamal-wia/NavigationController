package com.jamal_aliev.navigationcontroller.controllers

import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.SwitchNavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.core.screen.SwitchScreen
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import com.jamal_aliev.navigationcontroller.util.requireNavigationContextChanger
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.ScreenResolver
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.SimpleTransitionAnimation
import me.aartikov.alligator.animations.TransitionAnimation
import me.aartikov.alligator.exceptions.ScreenNotFoundException
import me.aartikov.alligator.navigationfactories.NavigationFactory
import me.aartikov.alligator.screenswitchers.FragmentScreenSwitcher
import me.aartikov.alligator.screenswitchers.ScreenSwitcher
import java.io.Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class SwitchNavigationControllerFragmentScreen(
    open val screens: List<SwitchScreen> = ArrayList()
) : Screen, Serializable {

    override fun hashCode() = screens.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is SwitchNavigationControllerFragmentScreen
                && (this.screens === other.screens || this.screens == other.screens)
    }
}

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
abstract class SwitchNavigationControllerFragment : Fragment,
    SwitchNavigationControllerContract,
    NavigationContextProvider,
    FragmentScreenSwitcher.AnimationProvider {

    constructor() : super()
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val screenResolver: ScreenResolver get() = navigator.screenResolver
    private val navigationFactory: NavigationFactory get() = navigator.navigationFactory

    private val args: SwitchNavigationControllerFragmentScreen by lazy {
        screenResolver.getScreen(this)
    }

    private val screenSwitcher by lazy {
        FragmentScreenSwitcher(
            /* navigationFactory = */ navigationFactory,
            /* fragmentManager = */ childFragmentManager,
            /* containerId = */ getContainerId(),
            /* animationProvider = */ this
        )
    }

    override fun getScreenSwitcher(): ScreenSwitcher {
        return screenSwitcher
    }

    private var navigationContext: NavigationContext? = null

    override fun getNavigationContext(): NavigationContext {
        if (navigationContext == null) {
            navigationContext =
                NavigationContext.Builder(requireAppCompatActivity(), navigatorFactory)
                    .fragmentNavigation(childFragmentManager, getContainerId())
                    .screenSwitcher(getScreenSwitcher())
                    .screenSwitchingListener { screenFrom, screenTo ->
                        onSwitchScreen(screenFrom as? SwitchScreen, screenTo as SwitchScreen)
                        requireNavigationContextChanger().defaultNavigationContext()
                    }
                    .build()
        }
        return navigationContext!!
    }

    override fun canGoBack(): Boolean {
        return idsBackStack.size > 1 || currentScreen?.id != mainScreen.id
    }

    override fun onNavigationUp(animationData: AnimationData?) {
        if (idsBackStack.size > 1) {
            idsBackStack.removeLast()
            val backScreen = screens.getValue(idsBackStack.last())
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(backScreen)
        } else if (currentScreen?.id != mainScreen.id) {
            idsBackStack.removeLast()
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(mainScreen)
        }
    }

    private val mainScreen by lazy { args.screens.first() }

    var currentScreen: SwitchScreen? = null // Нельзя использовать "= mainScreen"
        private set

    private val screens by lazy {
        hashMapOf<Int, SwitchScreen>()
            .apply {
                args.screens.forEach { put(it.id, it) }
            }
    }

    private var idsBackStack = ArrayList<Int>() // ids табов

    /**
     * Обрабатывает переключение экрана
     * */
    override fun onSwitchScreen(screenFrom: SwitchScreen?, screenTo: SwitchScreen) {
        val index = idsBackStack.indexOf(screenTo.id)
        if (index != -1) idsBackStack.removeAt(index)

        idsBackStack.add(screenTo.id)
        currentScreen = screenTo
    }

    /**
     * Выполняет переключение экрана
     * */
    open fun onScreenSelected(screenId: Int) {
        val wantToSwitch = screens.getValue(screenId)
        if (wantToSwitch.id != currentScreen?.id) {
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(wantToSwitch)
        } else if (currentScreen is NavigationControllerFragmentScreen) {
            try {
                val rootNavControllerScreen = currentScreen as NavigationControllerFragmentScreen
                val rootScreen = rootNavControllerScreen.screens.firstOrNull() ?: return
                requireNavigationContextChanger().defaultNavigationContext()
                navigator.goBackTo(rootScreen::class.java)
            } catch (e: ScreenNotFoundException) {
                // goBackTo может фантомно выдать ошибку
            } catch (e: Exception) {
                requireNavigationContextChanger().setNavigationContext(this)
                navigator.switchTo(mainScreen)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentScreen = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getSerializable(CURRENT_SCREEN_KEY, SwitchScreen::class.java)
                ?: mainScreen
        } else {
            savedInstanceState?.getSerializable(CURRENT_SCREEN_KEY).run {
                this ?: return@run mainScreen
                this as SwitchScreen
            }
        }

        idsBackStack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getSerializable(
                BACK_STACK_KEY, ArrayList::class.java
            ) as? ArrayList<Int> ?: idsBackStack
        } else {
            savedInstanceState?.getSerializable(BACK_STACK_KEY) as? ArrayList<Int>
                ?: idsBackStack
        }

        if (savedInstanceState == null) {
            requireNavigationContextChanger().setNavigationContext(this)
            navigator.switchTo(mainScreen)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(CURRENT_SCREEN_KEY, currentScreen)
        outState.putSerializable(BACK_STACK_KEY, idsBackStack)
        super.onSaveInstanceState(outState)
    }

    private val appearFadeAnimation by lazy {
        SimpleTransitionAnimation(
            /* enterAnimation = */ R.anim.appear,
            /* exitAnimation = */ R.anim.fade
        )
    }

    override fun getAnimation(
        screenFrom: Screen, screenTo: Screen, animationData: AnimationData?
    ): TransitionAnimation {
        return appearFadeAnimation
    }

    private companion object {
        private const val CURRENT_SCREEN_KEY = "current_screen"
        private const val BACK_STACK_KEY = "back_stack"
    }
}