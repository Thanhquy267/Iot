package com.quyt.iot_demo

import android.app.Application
import android.content.Context
import android.util.Log
import com.quyt.iot_demo.mqtt.MQTTClient
import org.eclipse.paho.client.mqttv3.*

class GlobalApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        mqttClient = MQTTClient(applicationContext, Constant.MQTT_HOST, "AndroidClient")
        mqttClient.connect("test", "123456",
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTTClient", "Connection success")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTTClient", "Connection failure: ${exception.toString()}")

                    }
                },
                object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val msg = "Receive message: ${message.toString()} from topic: $topic"
                        Log.d("MQTTClient", msg)
                    }

                    override fun connectionLost(cause: Throwable?) {
                        Log.d("MQTTClient", "Connection lost ${cause.toString()}")
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d("MQTTClient", "Delivery complete")
                    }
                })
    }

    companion object {
        lateinit var appContext: Context
        lateinit var mqttClient: MQTTClient
            private set
    }
}