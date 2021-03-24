package com.quyt.iot_demo.ui.add

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityAddDeviceBinding
import com.quyt.iot_demo.databinding.DialogAddNameBinding
import com.quyt.iot_demo.esptouch.util.TouchNetUtil
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.service.EspTouchListener
import com.quyt.iot_demo.service.EsptouchAsyncTask4
import com.quyt.iot_demo.utils.NetUtils
import io.reactivex.functions.Consumer
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.net.InetAddress

class AddDeviceActivity : BaseActivity(), EspTouchListener {

    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    private lateinit var mLayoutBinding: ActivityAddDeviceBinding
    private val mDatabase = FirebaseDatabase.getInstance()
    var mTask: EsptouchAsyncTask4? = null
    private lateinit var mWifiManager: WifiManager
    var mStateResult: StateResult? = null
    var mListPermission = arrayOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var mAllPermissionGranted = false
    var mMacId: String? = null
    var mProgressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_add_device)
        setupActionBar()
        //
        initData()
        initCheckPermission()
    }

    override fun onConnectSuccess() {
        super.onConnectSuccess()
    }

    override fun onMessageArrived(topic: String?, message: MqttMessage?) {
        super.onMessageArrived(topic, message)
        val espPushModel = Gson().fromJson(message.toString(), PushMqtt::class.java)
        if (espPushModel.clientType == ClientType.ESP_TYPE.value && espPushModel.actionType == ActionType.CONNECT.value) {
            if (espPushModel.data?.state == "Connect success") {
                mProgressDialog?.dismiss()
                showCustomDialog()
            } else {

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 113) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                initData()
            } else {
                //Handle always denied
            }
        }
    }

    private fun initCheckPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initData()
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), 113
            )

        }
    }

    private fun initData() {
        mWifiManager = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
        mStateResult = checkWifi()
        mLayoutBinding.tvWifi.text = mStateResult?.ssid
        mLayoutBinding.cvAdd.setOnClickListener {
            executeEsptouch()
        }
    }


    fun showCustomDialog() {
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
        }
        binding.tvOk.setOnClickListener {
            alert.dismiss()
            updateDeviceInfo(mMacId, binding.etName.text.toString())
        }
    }

    private fun listenDevice(macID: String?) {

        mMqttClient.subscribe(
            macID.toString(),
            1,
            object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    val msg = "Subscribed to: ${macID.toString()}"
                    Log.d("MQTTClient", msg)
                }

                override fun onFailure(
                    asyncActionToken: IMqttToken?,
                    exception: Throwable?
                ) {
                    Log.d("MQTTClient", "Failed to publish message to topic")
                }
            })
    }

    private fun updateDeviceInfo(macID: String?, deviceName: String?) {
        val device = Device().apply {
            this.macAddress = macID
            this.name = deviceName
            this.state = "OFF"
            this.brightness = 0
        }
        Api.request(this, Api.service.addDeviceToHome(1, device),
            success = Consumer {
                finish()
            },
            error = Consumer {
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            })


//        val pushBody = PushMqtt().apply {
//            clientType = ClientType.APP_TYPE.value
//            actionType = ActionType.CREATE.value
//            data = device
//        }
//
//        mMqttClient.publish(
//            macID.toString(),
//            Gson().toJson(pushBody),
//            1,
//            false,
//            object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken?) {
//                    Log.d("MQTTClient", "Publish info")
//                    finish()
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
//                    Log.d("MQTTClient", "Failed to publish message to topic")
//                    Toast.makeText(this@AddDeviceActivity,exception?.message.toString(),Toast.LENGTH_SHORT).show()
//                }
//            })
    }

    override fun onPostExecute(macID: String?, progressDialog: ProgressDialog) {
        runOnUiThread {
            mMacId = macID
            listenDevice(macID)
            mProgressDialog = progressDialog
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }


    private fun executeEsptouch() {
        mTask = EsptouchAsyncTask4(this, this)
        mTask?.execute(
            mStateResult?.ssid?.toByteArray(),
            TouchNetUtil.parseBssid2bytes(mStateResult?.bssid),
            mLayoutBinding.etPassword.text.toString().toByteArray(), "1".toByteArray(),
            "1".toByteArray()
        )
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