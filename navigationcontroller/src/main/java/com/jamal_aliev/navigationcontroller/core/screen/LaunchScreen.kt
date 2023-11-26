package com.jamal_aliev.navigationcontroller.core.screen

import me.aartikov.alligator.Screen

/**
 * LaunchScreen - представляет из сбея экран, который был открыт из вне (например по ссылке или пушу)
 * @author Jamal Aliev (aliev.djamal.2000@gmail.com)
 * */
interface LaunchScreen : Screen {
    object Unknown : LaunchScreen
}