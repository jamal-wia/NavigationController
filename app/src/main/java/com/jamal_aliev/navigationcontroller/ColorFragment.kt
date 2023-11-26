package com.jamal_aliev.navigationcontroller

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.jamal_aliev.navigationcontroller.core.animation.ForwardBackAnimationData
import com.jamal_aliev.navigationcontroller.navigator.NavigationControllerHolder
import kotlin.random.Random

class ColorFragment : Fragment(R.layout.fragment_color) {

    private val navigator by lazy { NavigationControllerHolder.requireNavigator() }
    private val args: Screens.ColorFragmentScreen by lazy {
        NavigationControllerHolder.requireNavigator().screenResolver
            .getScreen(this)
    }

    private lateinit var rootContainer: ConstraintLayout
    private lateinit var nextButton: Button
    private lateinit var backButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(view: View) {
        rootContainer = view.findViewById(R.id.container)
        backButton = view.findViewById(R.id.back_button)
        nextButton = view.findViewById(R.id.next_button)

        rootContainer.setBackgroundColor(longToColorInt(args.color))

        nextButton.setOnClickListener {
            navigator.goForward(
                Screens.ColorFragmentScreen(randomColor()),
                ForwardBackAnimationData()
            )
        }
        backButton.setOnClickListener {
            requireActivity().onNavigateUp()
        }
    }

    private fun randomColor(): Long {
        return Random.nextLong(0x000000L, 0xFFFFFFL)
    }


    @ColorInt
    fun longToColorInt(color: Long): Int {
        val red = ((color and 0xFF0000) shr 16).toInt()
        val green = ((color and 0x00FF00) shr 8).toInt()
        val blue = (color and 0x0000FF).toInt()
        return Color.rgb(red, green, blue)
    }
}