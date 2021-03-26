package com.quyt.iot_demo.ui.auto

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ActivityAutoBinding
import com.quyt.iot_demo.ui.IfFragment

class AutoActivity : AppCompatActivity() {
    lateinit var mLayoutBinding: ActivityAutoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_auto)
        setupActionBar()
        mLayoutBinding.cvLocationTracking.setOnClickListener {
            val intent = Intent(this, LocationAutoActionActivity::class.java)
            startActivity(intent)
        }
        mLayoutBinding.cvDoorTracking.setOnClickListener {
            val intent = Intent(this, DoorTrackingActivity::class.java)
            startActivity(intent)
        }

        mLayoutBinding.cvAdd.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.rl_root, IfFragment.newInstance(this), IfFragment().javaClass.simpleName)
            ft.addToBackStack(IfFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }
}