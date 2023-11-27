package com.jamal_aliev.navigationcontroller.controllers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.NavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.animation.AppearFadeAnimationData
import com.jamal_aliev.navigationcontroller.core.animation.ForwardBackAnimationData
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import com.jamal_aliev.navigationcontroller.util.requireNavigationContextChanger
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.SimpleTransitionAnimation
import me.aartikov.alligator.animations.TransitionAnimation
import me.aartikov.alligator.animations.providers.TransitionAnimationProvider
import java.io.Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class LineNavigationControllerBottomDialogScreen(
    val screens: List<Screen> = mutableListOf()
) : Screen, Serializable {
    override fun hashCode() = screens.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other !is LineNavigationControllerBottomDialogScreen) false
        else other.screens == this.screens
    }
}

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class NavigationControllerBottomDialog : BottomSheetDialogFragment(),
    NavigationControllerContract,
    NavigationContextProvider,
    TransitionAnimationProvider {

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val fragmentNavigator get() = navigator.navigationContext?.fragmentNavigator
    private val screenResolver get() = navigator.screenResolver

    override fun getContainerId(): Int = R.id.container

    override fun canGoBack(): Boolean = fragmentNavigator?.canGoBack() == true

    override fun onNavigationUp(animationData: AnimationData?) {
        requireNavigationContextChanger().setNavigationContext(this)
        fragmentNavigator?.goBack(null, null)
    }

    override fun provideNavigationContext(): NavigationContext = navigationContext

    private val navigationContext by lazy {
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

    private var animationPool = ArrayList<AnimationData>()

    private val rightSlideAnim by lazy {
        SimpleTransitionAnimation(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        )
    }

    private val leftSlideAnim by lazy {
        SimpleTransitionAnimation(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    private val appearFadeAnimation by lazy {
        SimpleTransitionAnimation(
            R.anim.appear,
            R.anim.fade
        )
    }

    override fun getAnimation(
        transitionType: TransitionType,
        destinationType: DestinationType,
        screenClassFrom: Class<out Screen>,
        screenClassTo: Class<out Screen>,
        animationData: AnimationData?
    ): TransitionAnimation {
        val ltr = ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_LTR
        val rtl = ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_RTL

        var resultAnimationData = animationData
        resultAnimationData ?: if (transitionType == TransitionType.BACK) {
            resultAnimationData = animationPool.lastOrNull()
        } else if (transitionType == TransitionType.REPLACE) {
            resultAnimationData = animationPool.lastOrNull()
        }

        return when (resultAnimationData) {

            is AppearFadeAnimationData -> {
                when (transitionType) {
                    TransitionType.FORWARD -> {
                        animationPool.add(resultAnimationData)
                    }

                    TransitionType.BACK -> {
                        animationPool.removeLastOrNull()
                    }

                    TransitionType.REPLACE -> {
                        // do nothing
                    }

                    TransitionType.RESET -> {
                        animationPool.clear()
                    }
                }
                appearFadeAnimation
            }

            is ForwardBackAnimationData -> {
                when {
                    transitionType == TransitionType.FORWARD
                            && ltr -> {
                        animationPool.add(resultAnimationData)
                        rightSlideAnim
                    }

                    transitionType == TransitionType.FORWARD
                            && rtl -> {
                        animationPool.add(resultAnimationData)
                        leftSlideAnim
                    }

                    transitionType == TransitionType.BACK
                            && ltr -> {
                        animationPool.removeLastOrNull()
                        leftSlideAnim
                    }

                    transitionType == TransitionType.BACK
                            && rtl -> {
                        animationPool.removeLastOrNull()
                        rightSlideAnim
                    }

                    transitionType == TransitionType.REPLACE
                            && ltr -> {
                        rightSlideAnim
                    }

                    transitionType == TransitionType.REPLACE
                            && rtl -> {
                        leftSlideAnim
                    }

                    transitionType == TransitionType.RESET
                            && ltr -> {
                        animationPool.clear()
                        leftSlideAnim
                    }

                    transitionType == TransitionType.RESET
                            && rtl -> {
                        animationPool.clear()
                        rightSlideAnim
                    }

                    else -> TransitionAnimation.DEFAULT
                }
            }

            else -> TransitionAnimation.DEFAULT
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            val screen = screenResolver.getScreen<LineNavigationControllerFragmentScreen>(this)
            requireNavigationContextChanger().setNavigationContext(this)
            for ((index, item) in screen.screens.withIndex()) {
                if (index == 0) navigator.reset(item)
                else navigator.goForward(item)
            }
        } else if (animationPool.isEmpty()) {
            val serializableValue = savedInstanceState.getSerializable(ANIMATION_POOL_KEY)
            animationPool = (serializableValue as? ArrayList<AnimationData>)
                ?: ArrayList()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ANIMATION_POOL_KEY, animationPool)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        (requireNavigationContextChanger() as? DialogInterface.OnDismissListener)
            ?.onDismiss(dialog)
        super.onDestroy()
    }

    private companion object {
        private const val ANIMATION_POOL_KEY = "animation_pool"
    }
}