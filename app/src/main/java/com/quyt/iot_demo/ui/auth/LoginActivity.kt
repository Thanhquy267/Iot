package com.quyt.iot_demo.ui.auth

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.databinding.ActivityLoginBinding

class LoginActivity : BaseActivity() {

    private lateinit var mLayoutBinding : ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)


    }

    private fun login(email : String, pw : String){
        Api.request(Api.service.getDevices())
    }
}