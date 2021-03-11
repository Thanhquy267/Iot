package com.quyt.iot_demo.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.TimePickerCustom
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityHomeBinding
import com.quyt.iot_demo.databinding.LayoutLocationDialogBinding
import com.quyt.iot_demo.service.Actions
import com.quyt.iot_demo.service.LocationService
import com.quyt.iot_demo.service.ServiceState
import com.quyt.iot_demo.service.getServiceState
import java.net.URLDecoder

class HomeActivity : AppCompatActivity() {
    lateinit var mLayoutBinding: ActivityHomeBinding
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    private val mDatabase = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
//        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // Dark text on status bar
        window?.decorView?.systemUiVisibility = 0 // Light text status bar
        window?.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        //
        actionOnService(Actions.START)
        //

//        mLayoutBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
//            override fun onDrawerStateChanged(newState: Int) {
//                if (newState == DrawerLayout.STATE_SETTLING) {
//                    if (window?.statusBarColor == ContextCompat.getColor(this@HomeActivity,R.color.black)){
//                        setStatusBarColor(false)
//                    }else{
//                        setStatusBarColor(true)
//                    }
//                }
//            }
//            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
//            override fun onDrawerClosed(drawerView: View) {}
//            override fun onDrawerOpened(drawerView: View) {}
//
//        })

        mLayoutBinding.layoutNavigation.llLocation.setOnClickListener {
            showCustomDialog()
        }
        switchState()
        goToDetailAction()

        if (intent?.extras?.getBoolean("TURN_ON", false) == true){
            Handler().postDelayed({
                mLayoutBinding.view.scLivingRoom.performClick()
            },500)
        }
    }


    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, LocationService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("HomeActivity","Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            Log.d("HomeActivity","Starting the service in < 26 Mode")
            startService(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            val result = data?.extras?.getString("result", "") ?: ""
            val dataString = result.replace("https://www.google.com/maps/place/", "")
            val rawLatlng = dataString.substring(dataString.indexOf("/")).replace("@", "")

            val rawAddress = dataString.replace(dataString.substring(dataString.indexOf("/")), "")
            val finalAddress = URLDecoder.decode(rawAddress)

            val latlng = rawLatlng.substring(rawLatlng.lastIndexOf(","))
            val latlng2 = rawLatlng.split(",")
            val finalLatLng = LatLng(latlng2[0].replace("/", "").toDouble(), latlng2[1].toDouble())
            Log.d("onActivityResult", "${finalLatLng.latitude},${finalLatLng.longitude}")
            Log.d("onActivityResult", finalAddress)
            mSharedPreference.address = finalAddress
            mSharedPreference.latlng = finalLatLng
            val center = Location("center")
            val crLocation = Location("current")
            center.latitude = finalLatLng.latitude
            center.longitude = finalLatLng.longitude
            crLocation.latitude = 10.8062817
            crLocation.longitude = 106.6763683

            val distance = center.distanceTo(crLocation)
            Log.d("onActivityResult", distance.toString())
        }
    }


    private fun goToDetailAction() {
        mLayoutBinding.view.ivMenu.setOnClickListener {
            mLayoutBinding.drawerLayout.openDrawer(Gravity.LEFT)
        }

        mLayoutBinding.view.cvLivingRoom.setOnClickListener {
            val timePicker = TimePickerCustom()
            timePicker.show(supportFragmentManager, "timePicker")
//            val intent = Intent(this,MainActivity::class.java)
//            intent.putExtra("type",RoomType.LIVING_ROOM)
//            startActivity(intent)
        }
        mLayoutBinding.view.cvBathroom.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("type", RoomType.BATHROOM)
            startActivity(intent)
        }
        mLayoutBinding.view.cvBedroom.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("type", RoomType.BEDROOM)
            startActivity(intent)
        }
        mLayoutBinding.view.cvKitchen.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("type", RoomType.KITCHEN)
            startActivity(intent)
        }
    }

    private fun switchState() {
        mLayoutBinding.view.scLivingRoom.setOnCheckedChangeListener { _, isChecked ->
            mDatabase.getReference(RoomType.LIVING_ROOM.key).child("state").setValue(isChecked)
        }
        mLayoutBinding.view.scKitchen.setOnCheckedChangeListener { _, isChecked ->
            mDatabase.getReference(RoomType.KITCHEN.key).child("state").setValue(isChecked)
        }
        mLayoutBinding.view.scBedroom.setOnCheckedChangeListener { _, isChecked ->
            mDatabase.getReference(RoomType.BEDROOM.key).child("state").setValue(isChecked)
        }
        mLayoutBinding.view.scBathroom.setOnCheckedChangeListener { _, isChecked ->
            mDatabase.getReference(RoomType.BATHROOM.key).child("state").setValue(isChecked)
        }
    }

    fun showCustomDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        val binding = DataBindingUtil.inflate<LayoutLocationDialogBinding>(
            LayoutInflater.from(this),
            R.layout.layout_location_dialog,
            null,
            false
        )
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        binding.tvLocation.text = mSharedPreference.address

        binding.tvChange.setOnClickListener {
            val intent = Intent(this, WebMapActivity::class.java)
            startActivityForResult(intent, 1)
            alert.dismiss()
        }
        binding.tvOk.setOnClickListener {
            alert.dismiss()
        }

    }

    companion object {
        fun createIntent(context: Context,value : Boolean = false) : Intent{
            val intent = Intent(context,HomeActivity::class.java)
            intent.putExtra("TURN_ON",value)
            return intent
        }
    }
}