package com.quyt.iot_demo.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.DeviceAdapter
import com.quyt.iot_demo.adapter.OnDeviceListener
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityHomeBinding
import com.quyt.iot_demo.databinding.LayoutLocationDialogBinding
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.service.Actions
import com.quyt.iot_demo.service.LocationService
import com.quyt.iot_demo.service.ServiceState
import com.quyt.iot_demo.service.getServiceState
import com.quyt.iot_demo.ui.add.AddDeviceActivity
import com.quyt.iot_demo.ui.auto.AutoActivity
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.function.Consumer


class HomeActivity : BaseActivity(), OnDeviceListener {

    lateinit var mLayoutBinding: ActivityHomeBinding
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    private var mDeviceAdapter: DeviceAdapter? = null
    private var mListDevice = ArrayList<Device>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
//        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // Dark text on status bar
        window?.decorView?.systemUiVisibility = 0 // Light text status bar
        window?.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        //
        mSharedPreference.userId = "quythanh24"
        //
        mLayoutBinding.layoutNavigation.llLocation.setOnClickListener {
            showCustomDialog()
        }
        mLayoutBinding.layoutNavigation.llAuto.setOnClickListener {
            val intent = Intent(this, AutoActivity::class.java)
            startActivity(intent)
        }
        mLayoutBinding.view.ivAdd.setOnClickListener {
            val intent = Intent(this, AddDeviceActivity::class.java)
            startActivity(intent)
        }
        mLayoutBinding.view.ivMenu.setOnClickListener {
            mLayoutBinding.drawerLayout.openDrawer(Gravity.LEFT)
        }

//        if (intent?.extras?.getBoolean("TURN_ON", false) == true) {
//            Handler().postDelayed({
//                mLayoutBinding.view.scLivingRoom.performClick()
//            }, 500)
//        }
    }

    override fun onResume() {
        super.onResume()
        getDevices()
    }

    override fun onConnectSuccess() {
        getDevices()
        subscribeButtonState()
    }

    override fun onMessageArrived(topic: String?, message: MqttMessage?) {
        super.onMessageArrived(topic, message)
        val pushBody = Gson().fromJson(message.toString(), PushMqtt::class.java)
        if (pushBody.clientType == ClientType.SERVER_TYPE.value && pushBody.actionType == ActionType.CHANGE_STATE.value) {
            syncButtonState(pushBody.data)
        }
    }

    private fun syncButtonState(device: Device?) {
        mDeviceAdapter?.updateStatus(device)
    }

    override fun onDeviceStateChange(device: Device?,isClick : Boolean) {
        if (!isClick) return
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = ActionType.CHANGE_STATE.value
            data = device
        }
        mMqttClient.publish(
            device?.macAddress.toString(),
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


    override fun onItemClicked(device: Device?) {
        val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("device", Gson().toJson(device))
            startActivity(intent)
    }

    override fun onBrightnessChange(device: Device?) {
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = ActionType.CHANGE_STATE.value
            data = device
        }
        mMqttClient.publish(
            device?.macAddress.toString(),
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

    private fun getDevices() {
        Api.request(Api.service.getDevices(), this,
            Consumer { result ->
                mListDevice.clear()
                result.data?.forEach {
                    mListDevice.add(it)
                }
//                for(i in 0..10){
//                    mListDevice.add((mListDevice.clone() as ArrayList<Device>)[0])
//                }
                setupRecyclerView()
                Log.d("getDeviceApi", result.data?.size.toString())
            }, Consumer {
                Log.d("getDeviceApi", it.message.toString())
            })
    }

    private fun subscribeButtonState() {
        if (mMqttClient.isConnected()) {
            mMqttClient.subscribe(
                "1",
                1,
                object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d("MQTTClient", "Subscribe button state success")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d("MQTTClient", "Subscribe button state fail")
                    }
                })
        } else {
            connectMqtt()
        }
    }

    private fun setupRecyclerView() {
        mDeviceAdapter = DeviceAdapter(mListDevice, this)
        mLayoutBinding.view.rvDevice.adapter = mDeviceAdapter
        mLayoutBinding.view.rvDevice.layoutManager = LinearLayoutManager(this)
        (mLayoutBinding.view.rvDevice.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    fun String.getBytesByString(): ByteArray? {
        return try {
            this.toByteArray(charset("utf-8"))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException("the charset is invalid")
        }
    }

    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, LocationService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("HomeActivity", "Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            Log.d("HomeActivity", "Starting the service in < 26 Mode")
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
        fun createIntent(context: Context, value: Boolean = false): Intent {
            val intent = Intent(context, HomeActivity::class.java)
            intent.putExtra("TURN_ON", value)
            return intent
        }
    }


}