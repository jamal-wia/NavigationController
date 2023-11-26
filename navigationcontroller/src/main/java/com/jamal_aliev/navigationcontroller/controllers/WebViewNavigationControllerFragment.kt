package com.jamal_aliev.navigationcontroller.controllers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.NavigationControllerContract
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import me.aartikov.alligator.AndroidNavigator
import me.aartikov.alligator.Screen
import me.aartikov.alligator.animations.AnimationData
import java.io.Serializable

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class WebViewNavigationControllerFragmentScreen(
    val url: String
) : Screen, Serializable {
    override fun hashCode(): Int = url.hashCode()
    override fun equals(other: Any?): Boolean {
        return other is WebViewNavigationControllerFragmentScreen
                && other.url == this.url
    }
}

/**
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 */
open class WebViewNavigationControllerFragment : Fragment,
    NavigationControllerContract {

    constructor() : super()
    constructor(@LayoutRes idRes: Int) : super(idRes)

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val screenResolver get() = navigator.screenResolver
    private val screen: WebViewNavigationControllerFragmentScreen by lazy {
        screenResolver.getScreen(this)
    }

    override fun canGoBack(): Boolean {
        return (requireView() as WebView).run { canGoBack() }
    }

    override fun onNavigationUp(animationData: AnimationData?) {
        (requireView() as WebView).goBack()
    }

    private var containerId: Int = CONTAINER_ID_INIT_VALUE
    override fun getContainerId(): Int = requireView().id

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        containerId = savedInstanceState?.getInt(CONTAINER_ID_KEY, CONTAINER_ID_INIT_VALUE)
            ?.takeIf { it != CONTAINER_ID_INIT_VALUE }
            ?: View.generateViewId()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return WebView(requireContext())
            .apply {
                id = containerId
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view is WebView) {
            view.loadUrl(screen.url)
        }
    }

    companion object {
        private const val CONTAINER_ID_KEY = "container_id"
        private const val CONTAINER_ID_INIT_VALUE = -1
    }
}