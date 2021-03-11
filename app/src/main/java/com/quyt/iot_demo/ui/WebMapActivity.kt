package com.quyt.iot_demo.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ActivityWebMapBinding


class WebMapActivity : AppCompatActivity() {
    lateinit var mLayoutBinding : ActivityWebMapBinding

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_web_map)
        mLayoutBinding.wvMap.settings.javaScriptEnabled = true
        mLayoutBinding.wvMap.webChromeClient = WebMapClient()
        mLayoutBinding.wvMap.loadUrl("https://www.google.com/maps")
        mLayoutBinding.wvMap.settings.setGeolocationEnabled(true)
        //
        mLayoutBinding.cvOk.setOnClickListener {
            Log.d("Life",mLayoutBinding.wvMap.url?:"")
            val returnIntent = Intent()
            returnIntent.putExtra("result", mLayoutBinding.wvMap.url?:"")
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }
    }



    inner class WebMapClient : WebChromeClient(){
        override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
        ) {
            callback?.invoke(origin,true,true)
        }
//        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
//            Log.d("WebResourceRequest",request?.url.toString())
//            return true
//        }
    }
}

