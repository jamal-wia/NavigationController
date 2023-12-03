package com.jamal_aliev.navigationcontroller.controllers

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.R
import com.jamal_aliev.navigationcontroller.core.controller.NavigationController
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider
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
    NavigationController,
    OnNavigationUpProvider {

    constructor() : super(R.layout.fragment_webview)
    constructor(@LayoutRes idRes: Int) : super(idRes)

    private val navigator: AndroidNavigator get() = NavigationControllerHolder.requireNavigator()
    private val screenResolver get() = navigator.screenResolver
    private val screen: WebViewNavigationControllerFragmentScreen by lazy {
        screenResolver.getScreen(this)
    }

    override fun canGoBack(): Boolean {
        return (requireView() as WebView).run { canGoBack() }
    }

    override fun onNavigationUp(animationData: AnimationData?): Boolean {
        return (requireView() as WebView).goBack() == Unit
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (view is WebView) {
            view.loadUrl(screen.url)
        }
    }
}