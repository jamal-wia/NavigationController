package com.jamal_aliev.navigationcontroller

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import com.jamal_aliev.navigationcontroller.controllers.LineNavigationControllerFragmentScreen
import com.jamal_aliev.navigationcontroller.core.AndroidNavigationContextChangerFragment
import com.jamal_aliev.navigationcontroller.core.provider.OnNavigationUpProvider

class MainActivity : AppCompatActivity() {

    private lateinit var rootContainer: FragmentContainerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rootContainer = findViewById(R.id.root_navigation_changer)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(rootContainer.id, AndroidNavigationContextChangerFragment())
                .commitNow()

            App.navigator.reset(LineNavigationControllerFragmentScreen())
            App.navigator.reset(Screens.AppTabNavigationControllerScreen)
        }
    }

    override fun onNavigateUp(): Boolean {
        return (supportFragmentManager.fragments.first { it is OnNavigationUpProvider }
                as OnNavigationUpProvider)
            .onNavigationUp() == Unit
    }

}