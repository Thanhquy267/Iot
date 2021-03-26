package com.quyt.iot_demo.service

import android.content.Intent
import android.location.Location
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.quyt.iot_demo.Notification
import com.quyt.iot_demo.data.SharedPreferenceHelper
import java.util.*


class LocationService : BaseLocationService() {


    private var mStopMySelfTask: TimerTask? = null

    private var mStopMySelfTimer: Timer? = null
    private var mLastLocation: Location? = null
    private val mDatabase = FirebaseDatabase.getInstance().getReference("location")
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_REDELIVER_INTENT
    }


    override fun onLocationUpdate(lastLocation: Location?, movement: StatusMovementType) {
        super.onLocationUpdate(lastLocation, movement)
        val location: Location? = GPSTracker(this).getLocation()
        mDatabase.child("lat").setValue(location?.latitude)
        mDatabase.child("lng").setValue(location?.longitude)
        //
        val homeLocation = Location("homeLocation")
        val crLocation = Location("currentLocation")
        crLocation.latitude = location?.latitude?:0.0
        crLocation.longitude = location?.longitude?:0.0
        homeLocation.latitude =  mSharedPreference.currentHome?.geom?.coordinates?.get(0)?:0.0
        homeLocation.longitude = mSharedPreference.currentHome?.geom?.coordinates?.get(1)?:0.0
        val distance = crLocation.distanceTo(homeLocation)
        Log.d("DistanceCrToHome",distance.toString())
        if (distance < 100){
            Notification.createNotification(this)
        }
    }

}
