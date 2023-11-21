package com.jamal_aliev.fncontroller.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jamal_aliev.fncontroller.core.NavigationContextChanger

internal fun Fragment.requireNavigationContextChanger(): NavigationContextChanger {
    return (requireActivity() as? NavigationContextChanger)
        ?: requireActivity().supportFragmentManager.fragments
            .first { it is NavigationContextChanger } as NavigationContextChanger
}

internal fun Fragment.requireAppCompatActivity() =
    requireActivity() as AppCompatActivity