package com.study.core.contracts

import org.intellij.lang.annotations.Identifier

interface NotifyAdapter {

    @Identifier
    val containerId: Int

    fun notifyScreenUpdates()
}