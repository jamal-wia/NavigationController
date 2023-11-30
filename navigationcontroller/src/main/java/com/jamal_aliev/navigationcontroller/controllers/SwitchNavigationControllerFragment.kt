package com.jamal_aliev.navigationcontroller.controllers

import android.os.Build
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.animation.AppearFadeAnimationData
import com.jamal_aliev.navigationcontroller.core.animation.DisabledAnimationData
import com.jamal_aliev.navigationcontroller.core.controller.SwitchNavigationControllerContract
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
import me.aartikov.alligator.navigationfactories.NavigationFactory
import me.aartikov.alligator.screenswitchers.FragmentScreenSwitcher
import me.aartikov.alligator.screenswitchers.ScreenSwitcher
import java.io.Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class SwitchNavigationControllerFragmentScreen(
    open val defaultAnimationData: AnimationData = AppearFadeAnimationData()
) : Screen, Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class SwitchNavigationControllerFragment : Fragment,
    SwitchNavigationControllerContract,
    NavigationContextProvider,
    FragmentScreenSwitcher.AnimationProvider {

    constructor() : super(R.layout.container)
    constructor(@LayoutRes layoutRes: Int) : super(layoutRes)

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val screenResolver: ScreenResolver get() = navigator.screenResolver
    private val navigationFactory: NavigationFactory get() = navigator.navigationFactory

    private val args: SwitchNavigationControllerFragmentScreen
            by lazy { screenResolver.getScreen(this) }
    private val defaultAnimationData by lazy { args.defaultAnimationData }

    protected val currentScreen get() = backStack.last()
    protected var backStack = ArrayList<SwitchScreen>()
        private set

    override fun provideScreenSwitcher(): ScreenSwitcher = screenSwitcher

    override fun provideNavigationContext(): NavigationContext = navigationContext

    override fun getContainerId(): Int = R.id.container

    private val screenSwitcher by lazy {
        FragmentScreenSwitcher(
            /* navigationFactory = */ navigationFactory,
            /* fragmentManager = */ childFragmentManager,
            /* containerId = */ getContainerId(),
            /* animationProvider = */ this
        )
    }

    private val navigationContext by lazy {
        NavigationContext.Builder(requireAppCompatActivity(), navigatorFactory)
            .screenSwitcher(provideScreenSwitcher())
            .screenSwitchingListener { screenFrom, screenTo ->
                onSwitchScreen(screenFrom as? SwitchScreen, screenTo as SwitchScreen)
            }
            .build()
    }

    override fun canGoBack(): Boolean = backStack.size > 1

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        backStack.removeLast()
        requireNavigationContextChanger().setNavigationContext(this)
        return navigator.switchTo(backStack.last(), animationData) == Unit
    }

    /**
     * Обрабатывает переключение экрана
     * */
    override fun onSwitchScreen(screenFrom: SwitchScreen?, screenTo: SwitchScreen) {
        val index = backStack.indexOfFirst { it.id == screenTo.id }
        if (index != -1) backStack.removeAt(index)
        backStack.add(screenTo)

        requireNavigationContextChanger()
            .setNavigationContextAfter(this) { true }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backStack = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState?.getSerializable(
                BACK_STACK_KEY, ArrayList::class.java
            ) as? ArrayList<SwitchScreen> ?: backStack
        } else {
            savedInstanceState?.getSerializable(BACK_STACK_KEY)
                    as? ArrayList<SwitchScreen> ?: backStack
        }

        if (savedInstanceState == null) {
            requireNavigationContextChanger().setNavigationContext(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(BACK_STACK_KEY, backStack)
        super.onSaveInstanceState(outState)
    }

    private val appearFadeAnimation by lazy {
        SimpleTransitionAnimation(
            /* enterAnimation = */ R.anim.appear,
            /* exitAnimation = */ R.anim.fade
        )
    }

    override fun getAnimation(
        screenFrom: Screen,
        screenTo: Screen,
        animationData: AnimationData?
    ): TransitionAnimation {
        var resultAnimationData = animationData
        if (resultAnimationData == null) resultAnimationData = defaultAnimationData
        return when (resultAnimationData) {
            is AppearFadeAnimationData -> appearFadeAnimation
            is DisabledAnimationData -> TransitionAnimation.DEFAULT
            else -> TransitionAnimation.DEFAULT
        }
    }

    private companion object {
        private const val BACK_STACK_KEY = "back_stack"
    }
}