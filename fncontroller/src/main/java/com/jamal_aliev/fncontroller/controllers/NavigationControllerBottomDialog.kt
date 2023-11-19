package com.jamal_aliev.fncontroller.controllers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamal_aliev.fncontroller.R
import com.jamal_aliev.fncontroller.core.NavigationControllerContract
import com.jamal_aliev.fncontroller.core.animation.AppearFadeAnimationData
import com.jamal_aliev.fncontroller.core.animation.DisabledAnimationData
import com.jamal_aliev.fncontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.fncontroller.navigator.FNNavigatorHolder
import com.jamal_aliev.fncontroller.util.requireAppCompatActivity
import com.jamal_aliev.fncontroller.util.requireNavigationContextChanger
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
open class NavigationControllerBottomDialogScreen(
    val screens: List<Screen> = mutableListOf()
) : Screen, Serializable {
    override fun hashCode() = screens.hashCode()
    override fun equals(other: Any?): Boolean {
        return if (other !is NavigationControllerBottomDialogScreen) false
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

    private val navigator: AndroidNavigator get() = FNNavigatorHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val fragmentNavigator get() = navigator.navigationContext?.fragmentNavigator
    private val screenResolver get() = navigator.screenResolver

    private var containerId: Int = CONTAINER_ID_INIT_VALUE
    override fun getContainerId(): Int {
        if (containerId == CONTAINER_ID_INIT_VALUE)
            throw IllegalStateException("containerId is incorrect")
        return containerId
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        containerId = savedInstanceState?.getInt(CONTAINER_ID_KEY, CONTAINER_ID_INIT_VALUE)
            ?.takeIf { it != CONTAINER_ID_INIT_VALUE }
            ?: View.generateViewId()

        if (savedInstanceState == null) {
            val screen = screenResolver.getScreen<NavigationControllerBottomDialogScreen>(this)
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return FragmentContainerView(requireContext())
            .apply {
                id = containerId
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(CONTAINER_ID_KEY, containerId)
        outState.putSerializable(ANIMATION_POOL_KEY, animationPool)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        (activity as? DialogInterface.OnDismissListener)
            ?.onDismiss(dialog)
        super.onDestroy()
    }

    private companion object {
        private const val ANIMATION_POOL_KEY = "animation_pool"
        private const val CONTAINER_ID_INIT_VALUE = -1
        private const val CONTAINER_ID_KEY = "container_id"
    }
}