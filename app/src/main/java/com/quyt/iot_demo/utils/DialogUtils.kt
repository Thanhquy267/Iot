package com.quyt.iot_demo.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.LayoutLoadingDialogBinding

object DialogUtils {

    fun loading(context : Context) : AlertDialog{
        val alertDialogBuilder = android.app.AlertDialog.Builder(context)
        val binding = DataBindingUtil.inflate<LayoutLoadingDialogBinding>(LayoutInflater.from(context), R.layout.layout_loading_dialog, null, false)
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        return alert
    }
}