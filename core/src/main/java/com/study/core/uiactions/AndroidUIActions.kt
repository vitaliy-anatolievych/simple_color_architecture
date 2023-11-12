package com.study.core.uiactions

import android.content.Context
import android.widget.Toast
import com.study.core.contracts.UiActions

class AndroidUIActions(
    private val context: Context
): UiActions {

    override fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun getString(messageRes: Int, vararg args: Any): String {
        return context.getString(messageRes, *args)
    }

}