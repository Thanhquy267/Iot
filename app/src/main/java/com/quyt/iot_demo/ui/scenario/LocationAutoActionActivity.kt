package com.quyt.iot_demo.ui.scenario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ActivityLocationAutoActionBinding

class LocationAutoActionActivity  : AppCompatActivity(){
    private lateinit var mLayoutBinding : ActivityLocationAutoActionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_location_auto_action)
    }
}