package com.jamal_aliev.navigationcontroller.core.animation

import com.jamal_aliev.navigationcontroller.R
import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType
import me.aartikov.alligator.animations.AnimationData
import me.aartikov.alligator.animations.SimpleTransitionAnimation
import me.aartikov.alligator.animations.TransitionAnimation
import me.aartikov.alligator.animations.providers.TransitionAnimationProvider
import java.io.Serializable

class LineNavigationAnimationProvider : TransitionAnimationProvider, Serializable {

    var ltr: Boolean = true
    val rtl get() = !ltr

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
}