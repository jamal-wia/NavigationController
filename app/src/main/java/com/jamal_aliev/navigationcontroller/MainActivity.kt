package com.jamal_aliev.navigationcontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.core.NavigationContextChanger
import com.jamal_aliev.navigationcontroller.core.NavigationControllerFragment
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            NavigationControllerFragment.Builder()
                .setRootScreen(
                    LineNavigationControllerFragmentScreen(
                        listOf(Screens.AppBottomNavigationControllerScreen)
                    )
                )
                .show(supportFragmentManager, R.id.navigation_container)

        }
    }

    override fun onNavigateUp(): Boolean {
        return (supportFragmentManager.fragments.first { it is NavigationContextChanger }
                as OnNavigationUpProvider)
            .onNavigationUp()
    }

}