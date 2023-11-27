package com.jamal_aliev.navigationcontroller.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.NavigationContextChanger

fun Fragment.requireNavigationContextChanger(): NavigationContextChanger {
    return (requireActivity() as? NavigationContextChanger)
        ?: requireActivity().supportFragmentManager.fragments
            .first { it is NavigationContextChanger } as NavigationContextChanger
}

internal fun Fragment.requireAppCompatActivity() =
    requireActivity() as AppCompatActivity