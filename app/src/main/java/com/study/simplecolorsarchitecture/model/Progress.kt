package com.study.simplecolorsarchitecture.model

sealed class Progress

data object EmptyProgress : Progress()

data class PercentageProgress(
    val percentage: Int
) : Progress() {

    companion object {
        val START = PercentageProgress(0)
    }
}

fun Progress.isInProgress() = this !is EmptyProgress

fun Progress.getPercentage(): Int =
    (this as? PercentageProgress)?.percentage ?: PercentageProgress.START.percentage