package com.jamal_aliev.fncontroller

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorInt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.jamal_aliev.fncontroller.navigator.FNNavigatorHolder

class ColorFragment : Fragment(R.layout.fragment_color) {

    private val args: Screens.ColorFragmentScreen by lazy {
        FNNavigatorHolder.requireNavigator().screenResolver
            .getScreen(this)
    }

    private lateinit var rootContainer: ConstraintLayout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView(view)
    }

    private fun setupView(view: View) {
        rootContainer = view.findViewById(R.id.container)
        rootContainer.setBackgroundColor(longToColorInt(args.color))
    }


    @ColorInt
    fun longToColorInt(color: Long): Int {
        val red = ((color and 0xFF0000) shr 16).toInt()
        val green = ((color and 0x00FF00) shr 8).toInt()
        val blue = (color and 0x0000FF).toInt()
        return Color.rgb(red, green, blue)
    }
}