package com.study.core.contracts

import com.study.core.viewmodels.CoreViewModel


interface FragmentsHolder {

    fun getActivityScopeViewModel(): CoreViewModel

    fun notifyScreenUpdates()
}