package com.quyt.iot_demo.service

import android.content.Intent
import android.content.SharedPreferences
import android.location.GnssNavigationMessage
import android.location.Location
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.GlobalApplication
import com.quyt.iot_demo.Notification
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.mqtt.MQTTClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttClient
import java.util.*


class LocationService() : BaseLocationService() {


    private var mStopMySelfTask: TimerTask? = null

    private var mStopMySelfTimer: Timer? = null
    private var mLastLocation: Location? = null
    private val mDatabase = FirebaseDatabase.getInstance().getReference("location")
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    var mOldDistance = 0f
    private var mGoHome = 0
    private var mLeaveHome = 0
    private var mAtHome = false

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
        crLocation.latitude = location?.latitude ?: 0.0
        crLocation.longitude = location?.longitude ?: 0.0
        homeLocation.latitude = mSharedPreference.currentHome?.geom?.coordinates?.get(0) ?: 0.0
        homeLocation.longitude = mSharedPreference.currentHome?.geom?.coordinates?.get(1) ?: 0.0
        val distance = crLocation.distanceTo(homeLocation)
        Log.d("DistanceCrToHome", distance.toString())
        mSharedPreference.currentLocation = crLocation
        if (mOldDistance == 0f) mOldDistance = distance
        if (mOldDistance > distance){
            if (distance < 200){
                Log.d("Test Location","Đã về nhà")
                mGoHome++
            }else{
                Log.d("Test Location","Đang về nhà")
                mGoHome = 0
                mLeaveHome = 0
            }
        }else{
            if (distance < 150){
                Log.d("Test Location","Đang ra khỏi nhà")
                mGoHome = 0
                mLeaveHome = 0
            }else{
                Log.d("Test Location","Đã ra khỏi nhà")
                mLeaveHome++
            }
        }
        mOldDistance = distance
        if (mGoHome > 4 && !mAtHome){
            Notification.createNotification(this,"Đã về nhà")
            pushLocationState("Đã về nhà")
            mAtHome = true
        }
        if (mLeaveHome > 4 && mAtHome){
            Notification.createNotification(this,"Đã ra khỏi nhà")
            pushLocationState("Đã ra khỏi nhà")
            mAtHome = false
        }

    }

    fun pushLocationState(message: String){
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = ActionType.LOCATION.value
            data = Device().apply {
                state = message
            }
        }
        GlobalApplication.mqttClient.publish(
                mSharedPreference.currentUser?.id.toString(),
                Gson().toJson(pushBody),
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTTClient", "Publish info")
                    }

                    override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                    ) {
                        Log.d("MQTTClient", "Failed to publish message to topic")
                    }
                })
    }

}
