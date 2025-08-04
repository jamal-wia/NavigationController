package com.jamal_aliev.navigationcontroller.controllers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.TransactionData
import com.jamal_aliev.navigationcontroller.core.animation.LineNavigationAnimationProvider
import com.jamal_aliev.navigationcontroller.core.controller.LineNavigationControllerContract
import com.jamal_aliev.navigationcontroller.core.provider.NavigationContextProvider
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import com.jamal_aliev.navigationcontroller.util.requireAppCompatActivity
import com.jamal_aliev.navigationcontroller.util.requireNavigationContextChanger
import kotlinx.coroutines.flow.MutableSharedFlow
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.DestinationType
import me.aartikov.alligator.NavigationContext
import me.aartikov.alligator.Screen
import me.aartikov.alligator.TransitionType
import me.aartikov.alligator.animations.AnimationData
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
open class LineNavigationControllerBottomDialog : BottomSheetDialogFragment(R.layout.container),
    LineNavigationControllerContract,
    NavigationContextProvider {

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val navigatorFactory get() = navigator.navigationFactory
    private val fragmentNavigator get() = navigator.navigationContext?.fragmentNavigator
    private val screenResolver get() = navigator.screenResolver

    private lateinit var lineNavigationAnimationProvider: LineNavigationAnimationProvider

    private val navigationContext by lazy {
        NavigationContext.Builder(requireAppCompatActivity(), navigatorFactory)
            .fragmentNavigation(childFragmentManager, getContainerId())
            .transitionAnimationProvider(lineNavigationAnimationProvider)
            .transitionListener(::onTransactionScreen)
            .build()
    }

    override fun provideNavigationContext(): NavigationContext = navigationContext

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lineNavigationAnimationProvider = getOrCreateAnimationProvider(savedInstanceState)

        if (savedInstanceState == null) {
            val screen = screenResolver.getScreen<LineNavigationControllerBottomDialogScreen>(this)
            requireNavigationContextChanger().setNavigationContext(this)
            for ((index, item) in screen.screens.withIndex()) {
                if (index == 0) navigator.reset(item)
                else navigator.goForward(item)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lineNavigationAnimationProvider.ltr =
            ViewCompat.getLayoutDirection(requireView()) == ViewCompat.LAYOUT_DIRECTION_LTR
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ANIMATION_PROVIDER_KEY, lineNavigationAnimationProvider)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        transactionsListeners.clear()
        (requireNavigationContextChanger() as? DialogInterface.OnDismissListener)
            ?.onDismiss(dialog)
        super.onDestroy()
    }

    override fun getContainerId(): Int = R.id.container

    private fun getOrCreateAnimationProvider(
        savedInstanceState: Bundle?
    ): LineNavigationAnimationProvider {
        val lineNavigationAnimationProvider =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                savedInstanceState?.getSerializable(
                    ANIMATION_PROVIDER_KEY,
                    LineNavigationAnimationProvider::class.java
                )
            } else {
                savedInstanceState?.getSerializable(
                    ANIMATION_PROVIDER_KEY
                ) as? LineNavigationAnimationProvider
            } ?: LineNavigationAnimationProvider()
        return lineNavigationAnimationProvider
    }

    private val transactionsListeners = hashMapOf<Int, MutableSharedFlow<TransactionData>>()

    fun observeTransactionListener(
        lifecycle: Lifecycle,
        listenerId: Int,
        sharedFlow: MutableSharedFlow<TransactionData>
    ) {
        transactionsListeners[listenerId] = sharedFlow

        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    transactionsListeners.remove(listenerId)
                }
            }
        })
    }

    override fun onTransactionScreen(
        transitionType: TransitionType,
        destinationType: DestinationType,
        screenClassFrom: Class<out Screen>?,
        screenClassTo: Class<out Screen>?
    ) {
        transactionsListeners.forEach { (_, v) ->
            v.tryEmit(
                TransactionData(
                    transitionType, destinationType,
                    screenClassFrom, screenClassTo
                )
            )
        }
        if (transitionType == TransitionType.BACK) {
            requireNavigationContextChanger()
                .setNavigationContextAfter(this) { true }
        }
    }

    override fun canGoBack(): Boolean {
        requireNavigationContextChanger().setNavigationContext(this)
        return fragmentNavigator?.canGoBack() == true
    }

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        requireNavigationContextChanger().setNavigationContext(this)
        return fragmentNavigator?.goBack(null, animationData) == Unit
    }

    private companion object {
        private const val ANIMATION_PROVIDER_KEY = "animation_provider_key"
    }
}
