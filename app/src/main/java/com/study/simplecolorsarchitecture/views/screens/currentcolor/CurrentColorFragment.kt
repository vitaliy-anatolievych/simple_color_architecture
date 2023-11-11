package com.study.simplecolorsarchitecture.views.screens.currentcolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.study.core.views.BaseFragment
import com.study.core.views.BaseScreen
import com.study.core.views.screenViewModel
import com.study.simplecolorsarchitecture.databinding.FragmentCurrentColorBinding


/**
 * A simple [Fragment] subclass.
 * Use the [CurrentColorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CurrentColorFragment : BaseFragment() {

    class Screen : BaseScreen

    override val viewModel by screenViewModel<CurrentColorViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding =
            FragmentCurrentColorBinding.inflate(inflater, container, false)

        with(binding) {

            viewModel.currentColor.observe(viewLifecycleOwner) {
                colorView.setBackgroundColor(it.value)
            }

            changeColorButton.setOnClickListener {
                viewModel.changeColor()
            }

        }

        return binding.root
    }
}