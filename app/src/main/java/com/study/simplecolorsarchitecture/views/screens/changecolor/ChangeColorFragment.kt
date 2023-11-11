package com.study.simplecolorsarchitecture.views.screens.changecolor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.recyclerview.widget.GridLayoutManager
import com.study.core.views.BaseFragment
import com.study.core.views.BaseScreen
import com.study.core.views.screenViewModel
import com.study.simplecolorsarchitecture.R
import com.study.simplecolorsarchitecture.databinding.FragmentChangeColorBinding
import com.study.simplecolorsarchitecture.views.contracts.HasScreenTitle
import org.intellij.lang.annotations.Identifier

class ChangeColorFragment: BaseFragment(), HasScreenTitle {

    class Screen(@Identifier val id: Long) : BaseScreen

    override val viewModel by screenViewModel<ChangeColorViewModel>()

    override fun getScreenTitle(): String? = viewModel.screenTitle.value

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentChangeColorBinding.inflate(inflater, container, false)

        val adapter = ColorsAdapter(viewModel)
        setupLayoutManager(binding, adapter)

        binding.saveButton.setOnClickListener { viewModel.onSavePressed() }
        binding.cancelButton.setOnClickListener { viewModel.onCancelPressed() }

        viewModel.colorsList.observe(viewLifecycleOwner) {
            adapter.items = it
        }
        viewModel.screenTitle.observe(viewLifecycleOwner) {
            // if screen title is changed -> need to notify activity about updates
            notifyScreenUpdates()
        }

        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        // waiting for list width
        binding.colorsRecyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.colorsRecyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.colorsRecyclerView.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                val columns = width / itemWidth
                binding.colorsRecyclerView.adapter = adapter
                binding.colorsRecyclerView.layoutManager = GridLayoutManager(requireContext(), columns)
            }
        })
    }
}