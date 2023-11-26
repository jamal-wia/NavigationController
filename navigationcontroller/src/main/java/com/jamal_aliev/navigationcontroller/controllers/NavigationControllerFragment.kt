package com.jamal_aliev.navigationcontroller.controllers

import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.NavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.animation.AppearFadeAnimationData
import com.jamal_aliev.navigationcontroller.core.animation.DisabledAnimationData
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import com.jamal_aliev.navigationcontroller.util.requireNavigationContextChanger
import me.aartikov.alligator.*
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.SimpleTransitionAnimation
import me.aartikov.alligator.animations.TransitionAnimation
import me.aartikov.alligator.animations.providers.TransitionAnimationProvider
import java.io.Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class NavigationControllerFragmentScreen(
    val screens: List<Screen> = mutableListOf() // Нельзя использовать emptyList()
) : Screen, Serializable {
    override fun hashCode() = screens.hashCode()
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return other is NavigationControllerFragmentScreen
                && (other.screens === this.screens
                || other.screens == this.screens)
    }
}

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class NavigationControllerFragment : Fragment(R.layout.container),
    NavigationControllerContract,
    NavigationContextProvider,
    TransitionAnimationProvider {

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val fragmentNavigator get() = navigator.navigationContext?.fragmentNavigator
    private val screenResolver get() = navigator.screenResolver

    override fun getContainerId(): Int {
        return R.id.container
    }

    override fun canGoBack(): Boolean {
        return fragmentNavigator?.canGoBack() == true
    }

    override fun onNavigationUp(animationData: AnimationData?) {
        requireNavigationContextChanger().setNavigationContext(this)
        fragmentNavigator?.goBack(null, null)
    }

    private var navigationContext: NavigationContext? = null
    override fun getNavigationContext(): NavigationContext {
        if (navigationContext == null) {
            navigationContext =
                NavigationContext.Builder(requireAppCompatActivity(), navigatorFactory)
                    .fragmentNavigation(childFragmentManager, getContainerId())
                    .transitionAnimationProvider(this)
                    .transitionListener { transitionType, destinationType,
                                          screenClassFrom, screenClassTo ->
                        if (transitionType == TransitionType.BACK) {
                            requireNavigationContextChanger()
                                .defaultNavigationContext()
                        }
                    }
                    .build()
        }
        return navigationContext!!
    }

    private var animationPool =
        hashMapOf<Pair<Class<out Screen>, Class<out Screen>>, AnimationData>()

    private var forwardAnim: SimpleTransitionAnimation? = null
        get() {
            if (field == null) {
                field = SimpleTransitionAnimation(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                )
            }
            return field
        }

    private var backAnim: SimpleTransitionAnimation? = null
        get() {
            if (field == null) {
                field = SimpleTransitionAnimation(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
                )
            }
            return field
        }

    private var appearFadeAnimation: SimpleTransitionAnimation? = null
        get() {
            if (field == null) {
                field = SimpleTransitionAnimation(
                    R.anim.appear,
                    R.anim.fade
                )
            }
            return field
        }

    override fun getAnimation(
        transitionType: TransitionType,
        destinationType: DestinationType,
        screenClassFrom: Class<out Screen>,
        screenClassTo: Class<out Screen>,
        animationData: AnimationData?
    ): TransitionAnimation {
        var resultAnimationData = animationData

        if (animationData == null) {
            val savedAnimationData = animationPool[screenClassFrom to screenClassTo]
                ?: animationPool[screenClassTo to screenClassFrom]
            if (savedAnimationData != null) resultAnimationData = savedAnimationData
        } else {
            animationPool[screenClassFrom to screenClassTo] = animationData
        }

        val ltr = ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_LTR
        val rtl = ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_RTL

        return when {
            resultAnimationData is DisabledAnimationData -> TransitionAnimation.DEFAULT

            resultAnimationData is AppearFadeAnimationData -> appearFadeAnimation!!

            transitionType == TransitionType.FORWARD && ltr-> forwardAnim!!

            transitionType == TransitionType.FORWARD && rtl -> backAnim!!

            transitionType == TransitionType.BACK && ltr -> {
                animationPool.remove(screenClassFrom to screenClassTo)
                animationPool.remove(screenClassTo to screenClassFrom)
                backAnim!!
            }

            transitionType == TransitionType.BACK && rtl -> {
                animationPool.remove(screenClassFrom to screenClassTo)
                animationPool.remove(screenClassTo to screenClassFrom)
                forwardAnim!!
            }

            else -> TransitionAnimation.DEFAULT
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val screen = screenResolver.getScreen<NavigationControllerFragmentScreen>(this)
            requireNavigationContextChanger().setNavigationContext(this)
            for ((index, item) in screen.screens.withIndex()) {
                if (index == 0) navigator.reset(item)
                else navigator.goForward(item)
            }
        } else if (animationPool.isEmpty()) {
            val serializableValue = savedInstanceState.getSerializable(ANIMATION_POOL_KEY)
            animationPool = if (serializableValue != null && serializableValue is HashMap<*, *>) {
                @Suppress("UNCHECKED_CAST")
                serializableValue as HashMap<Pair<Class<out Screen>, Class<out Screen>>, AnimationData>
            } else {
                HashMap()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ANIMATION_POOL_KEY, animationPool)
        super.onSaveInstanceState(outState)
    }

    private companion object {
        private const val ANIMATION_POOL_KEY = "animation_pool"
        private const val CONTAINER_ID_INIT_VALUE = -1
        private const val CONTAINER_ID_KEY = "container_id"
    }
}