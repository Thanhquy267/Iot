package com.quyt.iot_demo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityLoginBinding
import com.quyt.iot_demo.ui.HomeActivity
import io.reactivex.functions.Consumer

class LoginActivity : AppCompatActivity() {

    private lateinit var mLayoutBinding: ActivityLoginBinding
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        if (mSharedPreference.currentUser != null && mSharedPreference.isLogging == true) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        if (mSharedPreference.isLogging == true && mSharedPreference.currentUser != null) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            return
        }
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mLayoutBinding.cvLogin.setOnClickListener {
            login(mLayoutBinding.etEmail.text.toString(), mLayoutBinding.etPassword.text.toString())
        }

    }

    private fun login(email: String, pw: String) {
        Api.request(this, Api.service.login(email, pw),
            success = Consumer {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                mSharedPreference.currentUser = it.data
                mSharedPreference.isLogging = true
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            },
            error = Consumer {
            })
    }
}