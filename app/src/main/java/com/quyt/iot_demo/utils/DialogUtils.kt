package com.quyt.iot_demo.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.LayoutAddCameraDialogBinding
import com.quyt.iot_demo.databinding.LayoutLoadingDialogBinding
import com.quyt.iot_demo.model.Camera
import io.reactivex.functions.Consumer

object DialogUtils {

    fun loading(context: Context): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val binding = DataBindingUtil.inflate<LayoutLoadingDialogBinding>(
            LayoutInflater.from(context),
            R.layout.layout_loading_dialog,
            null,
            false
        )
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        return alert
    }

    fun addCamera(context: Context, successConsumer: Consumer<Camera>) {
        val camera = Camera()
        val alertDialogBuilder = AlertDialog.Builder(context)
        val binding = DataBindingUtil.inflate<LayoutAddCameraDialogBinding>(
            LayoutInflater.from(context),
            R.layout.layout_add_camera_dialog,
            null,
            false
        )
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        binding.cvAdd.setOnClickListener {
            camera.name = binding.etName.text.toString()
            camera.ip = binding.etIp.text.toString()
            camera.port = binding.etPort.text.toString()
            camera.user = binding.etUser.text.toString()
            camera.pw = binding.etPw.text.toString()
            successConsumer.accept(camera)
            alert.dismiss()
        }

    }
}