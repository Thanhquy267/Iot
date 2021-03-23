package com.quyt.iot_demo.ui.auto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.databinding.ActivityAutoBinding
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

class AutoActivity : BaseActivity() {
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
            val command = mLayoutBinding.etCommand.text.toString()
            if (command.isEmpty()) return@setOnClickListener
            publish(command)
        }
    }

    override fun onConnectSuccess() {
        subscribe()
    }

    private fun publish(command : String){
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = "voice"
            data = Device().apply {
                state = command
            }
        }
        mMqttClient.publish(
            "voiceCommand1",
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

    private fun subscribe(){
        mMqttClient.subscribe(
            "voidCommand1",
            1,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTTClient", "Subscribe button state success")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTTClient", "Subscribe button state fail")
                }
            })
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }
}