package com.jamal_aliev.navigationcontroller.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.NavigationContextChanger
import com.jamal_aliev.navigationcontroller.core.controller.NavigationController

fun Fragment.requireNavigationContextChanger(): NavigationContextChanger {
    return (requireActivity() as? NavigationContextChanger)
        ?: requireActivity().supportFragmentManager.fragments
            .first { it is NavigationContextChanger } as NavigationContextChanger
}

inline fun <reified T : NavigationController> Fragment.requireNavigationController(): T {
    return findFragmentBefore(this) { it is T } as T
}

fun Fragment.findFragmentAfter(
    fragments: List<Fragment>,
    predicate: (Fragment) -> Boolean
): Fragment? {
    fragments.findLast(predicate)
        ?.let { return it }

    for (item in fragments) {
        return findFragmentAfter(
            fragments = item.childFragmentManager.fragments,
            predicate = predicate
        )
    }

    return null
}


fun Fragment.findFragmentBefore(
    fragment: Fragment,
    predicate: (Fragment) -> Boolean
): Fragment? {
    var currentFragment: Fragment? = fragment
    while (currentFragment != null) {
        if (predicate(currentFragment)) {
            return currentFragment
        }
        currentFragment = currentFragment.parentFragment
    }
    return null
}


internal fun Fragment.requireAppCompatActivity() = requireActivity() as AppCompatActivity

