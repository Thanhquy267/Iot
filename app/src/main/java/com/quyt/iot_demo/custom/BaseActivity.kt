package com.quyt.iot_demo.custom

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.mqtt.MQTTClient
import org.eclipse.paho.client.mqttv3.*
import java.util.function.Consumer

open class BaseActivity : AppCompatActivity() {

    val mMqttClient = MQTTClient(this, Constant.MQTT_HOST, "AndroidClient")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!mMqttClient.isConnected()){
            connectMqtt()
        }
    }

    override fun onResume() {
        super.onResume()
//        if (!mMqttClient.isConnected()){
//            connectMqtt()
//        }
    }

     fun connectMqtt(success : Consumer<Unit>? = null) {
        mMqttClient.connect("test", "123456",
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d("MQTTClient", "Connection success")
                    success?.accept(Unit)
                    onConnectSuccess()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.d("MQTTClient", "Connection failure: ${exception.toString()}")

                }
            },
            object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = "Receive message: ${message.toString()} from topic: $topic"
                    Log.d("MQTTClient", msg)
                    //
                    onMessageArrived(topic,message)
                }

                override fun connectionLost(cause: Throwable?) {
                    Log.d("MQTTClient", "Connection lost ${cause.toString()}")
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    Log.d("MQTTClient", "Delivery complete")
                }
            })
    }

    open fun onMessageArrived(topic: String?, message: MqttMessage?){}

    open fun onConnectSuccess(){}

}