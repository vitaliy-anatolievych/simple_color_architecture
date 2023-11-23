package com.study.core.views

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.study.core.contracts.FragmentsHolder
import com.study.core.utils.ErrorResult
import com.study.core.utils.PendingResult
import com.study.core.utils.Result
import com.study.core.utils.SuccessResult

/**
 * Базовий клас для усіх фрагментів
 */
abstract class BaseFragment: Fragment() {

    /**
     * Обов'язкова [BaseViewModel] яка менеджить це фрагмент
     */
    abstract val viewModel: BaseViewModel

    /**
     * Цей метод викликається якщо потрібно щось перерендирити
     * наприклад [ToolBar]
     */
    fun notifyScreenUpdates() {
        (requireActivity() as FragmentsHolder).notifyScreenUpdates()
    }

    fun <T> renderResult(
        root: View,
        result: Result<T>,
        onPending: () -> Unit,
        onError: (Exception) -> Unit,
        onSuccess: (T) -> Unit
    ) {
        (root as ViewGroup).children.forEach { it.visibility = View.GONE }
        when(result) {
            is ErrorResult -> onError(result.exception)
            is PendingResult -> onPending()
            is SuccessResult -> onSuccess(result.data)
        }
    }
}