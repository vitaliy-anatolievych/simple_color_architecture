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
import com.study.simplecolorsarchitecture.views.screens.utils.onTryAgain
import com.study.simplecolorsarchitecture.views.screens.utils.renderSimpleResult
import org.intellij.lang.annotations.Identifier

class ChangeColorFragment : BaseFragment(), HasScreenTitle {

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

        viewModel.viewState.observe(viewLifecycleOwner) { result ->
            renderSimpleResult(
                root = binding.root,
                result = result
            ) { viewState ->
                adapter.items = viewState.colorsList
                with(binding) {
                    saveButton.visibility =
                        if (viewState.showSaveButton) View.VISIBLE else View.INVISIBLE
                    cancelButton.visibility =
                        if (viewState.showCancelButton) View.VISIBLE else View.INVISIBLE
                    changeProgressBar.visibility =
                        if (viewState.showProgressBar) View.VISIBLE else View.GONE
                }


            }
        }
        viewModel.screenTitle.observe(viewLifecycleOwner) {
            // if screen title is changed -> need to notify activity about updates
            notifyScreenUpdates()
        }

        onTryAgain(root = binding.root) {
            viewModel.tryAgain()
        }

        return binding.root
    }

    private fun setupLayoutManager(binding: FragmentChangeColorBinding, adapter: ColorsAdapter) {
        // waiting for list width
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val width = binding.root.width
                val itemWidth = resources.getDimensionPixelSize(R.dimen.item_width)
                val columns = width / itemWidth
                binding.colorsRecyclerView.adapter = adapter
                binding.colorsRecyclerView.layoutManager =
                    GridLayoutManager(requireContext(), columns)
            }
        })
    }
}