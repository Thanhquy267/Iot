package com.quyt.iot_demo.ui.add

import android.content.Context
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.R
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityAddDeviceBinding
import com.quyt.iot_demo.databinding.DialogAddNameBinding
import com.quyt.iot_demo.esptouch.util.TouchNetUtil
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.mqtt.MQTTClient
import com.quyt.iot_demo.service.EspTouchListener
import com.quyt.iot_demo.service.EsptouchAsyncTask4
import com.quyt.iot_demo.utils.NetUtils
import io.reactivex.functions.Consumer
import org.eclipse.paho.client.mqttv3.*
import java.net.InetAddress

class AddDeviceActivity : AppCompatActivity(), EspTouchListener {

    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    private lateinit var mLayoutBinding : ActivityAddDeviceBinding
    private val mDatabase = FirebaseDatabase.getInstance()
    var mTask: EsptouchAsyncTask4? = null
    private lateinit var mWifiManager: WifiManager
    var mStateResult : StateResult? = null
    val mMqttClient = MQTTClient(this, Constant.HOST, "AndroidClient")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_device)
        setupActionBar()
        //
        initData()
    }

    fun showCustomDialog(macID: String?) {
            val newDevice = Device().apply {
                id = macID
                state = "off"
            }
            val alertDialogBuilder = android.app.AlertDialog.Builder(this)
            val binding = DataBindingUtil.inflate<DialogAddNameBinding>(
                LayoutInflater.from(this),
                R.layout.dialog_add_name, null, false
            )
            alertDialogBuilder.setView(binding.root)
            val alert = alertDialogBuilder.show()
            alert.setCancelable(true)
            alert.setCanceledOnTouchOutside(false)
            alert?.window?.setBackgroundDrawableResource(R.color.transparent)

            binding.tvCancel.setOnClickListener {
                alert.dismiss()
//                newDevice.name = "defaul1"
//                mDatabase.getReference("${mSharedPreference.userId}")
//                    .child(macID.toString())
//                    .setValue(newDevice)
            }
            binding.tvOk.setOnClickListener {
                alert.dismiss()
                  listenDevice(macID,binding.etName.text.toString())
//                newDevice.name = binding.etName.text.toString()
//                mDatabase.getReference("${mSharedPreference.userId}")
//                    .child(macID.toString())
//                    .setValue(newDevice)
            }
    }

    private fun listenDevice(macID: String?,deviceName: String?){
        mMqttClient.connect("test", "123456",
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTTClient", "Connection success")
                        mMqttClient.subscribe(
                                macID.toString(),
                                1,
                                object : IMqttActionListener {
                                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                                    val msg = "Subscribed to: ${macID.toString()}"
                                        Log.d("MQTTClient", msg)
                                    }

                                    override  fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                        Log.d("MQTTClient", "Failed to publish message to topic")
                                    }
                                })
                        updateDeviceInfo(macID,deviceName)
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

    override fun onDestroy() {
        super.onDestroy()
        mMqttClient.disconnect(object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MQTTClient","onSuccess")
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.d("MQTTClient","onFailure")
            }

        })
    }


    private fun updateDeviceInfo(macID: String?,deviceName : String?){
        val device = Device().apply {
            this.id = macID
            this.name = deviceName
            this.state = "off"
        }

        val pushBody = PushMqtt().apply {
            actionType = "app"
            data = device
        }

        mMqttClient.publish(
                macID.toString(),
                Gson().toJson(pushBody),
                1,
                false,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTTClient", "Publish info")
                        val listDevice = ArrayList<Device>()
                        listDevice.add(device)
                        mSharedPreference.listDevice = listDevice
                    }

                    override  fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTTClient", "Failed to publish message to topic")
                    }
                })
    }

    override fun onPostExecute(macID: String?) {
       runOnUiThread {
           showCustomDialog(macID)
       }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initData(){
        mWifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mStateResult = checkWifi()
        mLayoutBinding.tvWifi.text = mStateResult?.ssid
        mLayoutBinding.cvAdd.setOnClickListener {
            executeEsptouch()
        }
    }


    private fun executeEsptouch(){
        mTask = EsptouchAsyncTask4(this,this)
        mTask?.execute(mStateResult?.ssid?.toByteArray(),
            TouchNetUtil.parseBssid2bytes(mStateResult?.bssid),
            mLayoutBinding.etPassword.text.toString().toByteArray(), "1".toByteArray(),
            "1".toByteArray())
    }

    private fun checkWifi(): StateResult? {
        val result = StateResult()
        result.wifiConnected = false
        val wifiInfo: WifiInfo = mWifiManager.connectionInfo
        val connected: Boolean = NetUtils.isWifiConnected(mWifiManager)
        if (!connected) {
            result.message = getString(R.string.esptouch_message_wifi_connection)
            return result
        }
        val ssid: String = NetUtils.getSsidString(wifiInfo)
        val ipValue = wifiInfo.ipAddress
        if (ipValue != 0) {
            result.address = NetUtils.getAddress(wifiInfo.ipAddress)
        } else {
            result.address = NetUtils.getIPv4Address()
            if (result.address == null) {
                result.address = NetUtils.getIPv6Address()
            }
        }
        result.wifiConnected = true
        result.message = ""
        result.is5G = NetUtils.is5G(wifiInfo.frequency)
        if (result.is5G) {
            result.message = getString(R.string.esptouch_message_wifi_frequency)
        }
        result.ssid = ssid
        result.ssidBytes = NetUtils.getRawSsidBytesOrElse(wifiInfo, ssid.toByteArray())
        result.bssid = wifiInfo.bssid
        return result
    }

     class StateResult {
        var message: CharSequence? = null
        var permissionGranted = false
        var locationRequirement = false
        var wifiConnected = false
        var is5G = false
        var address: InetAddress? = null
        var ssid: String? = null
        var ssidBytes: ByteArray? = null
        var bssid: String? = null
    }

}