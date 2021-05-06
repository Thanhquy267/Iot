package com.quyt.iot_demo.ui

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.DeviceAdapter
import com.quyt.iot_demo.adapter.MainPagerAdapter
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityHomeBinding
import com.quyt.iot_demo.databinding.LayoutLocationDialogBinding
import com.quyt.iot_demo.model.*
import com.quyt.iot_demo.service.Actions
import com.quyt.iot_demo.service.LocationService
import com.quyt.iot_demo.service.ServiceState
import com.quyt.iot_demo.service.getServiceState
import com.quyt.iot_demo.ui.add.AddDeviceActivity
import com.quyt.iot_demo.ui.scenario.ScenarioActivity
import com.quyt.iot_demo.ui.location.MapActivity
import io.reactivex.functions.Consumer
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.io.UnsupportedEncodingException
import java.net.URLDecoder


class HomeActivity : BaseActivity() {

    lateinit var mLayoutBinding: ActivityHomeBinding
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    private var mDeviceAdapter: DeviceAdapter? = null
    private var mListDevice = ArrayList<Device>()
    private var mListSensor = ArrayList<Device>()
    private var mCurrentHome: Home? = null
    private lateinit var mControlFragment: ControlFragment
    private lateinit var mSensorFragment: SensorFragment
    private lateinit var mIRFragment: IRFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_home)
//        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR  // Dark text on status bar
        window?.decorView?.systemUiVisibility = 0 // Light text status bar
        window?.statusBarColor = ContextCompat.getColor(this, R.color.primary)
        //
        mLayoutBinding.layoutNavigation.llLocation.setOnClickListener {
//            showCustomDialog()
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
        mLayoutBinding.layoutNavigation.llAuto.setOnClickListener {
            val intent = Intent(this, ScenarioActivity::class.java)
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
        if (!isLocationServiceRunning()) {
            actionOnService(Actions.START)
        }
    }

    override fun onResume() {
        super.onResume()
        getHome()
    }

    override fun onConnectSuccess() {
//        getHome()
    }

    override fun onMessageArrived(topic: String?, message: MqttMessage?) {
        super.onMessageArrived(topic, message)
        val pushBody = Gson().fromJson(message.toString(), PushMqtt::class.java)
        if (pushBody.clientType == ClientType.SERVER_TYPE.value && pushBody.actionType == ActionType.CHANGE_STATE.value) {
//            if (pushBody.data?.type == "control") {
//                mControlFragment.syncButtonState(pushBody.data)
//            } else {
//                mSensorFragment.changeStatus(pushBody.data)
//            }
            val data = pushBody.data
            mSensorFragment.changeStatus(data)
            mControlFragment.syncButtonState(data)
        }
    }

    private fun initViewPager() {
        mControlFragment = ControlFragment.newInstance(this, mMqttClient)
        mSensorFragment = SensorFragment.newInstance(this, mMqttClient)
        mIRFragment = IRFragment()
        val adapter = MainPagerAdapter(supportFragmentManager)
        adapter.addFrag(mControlFragment, "Device")
        adapter.addFrag(mSensorFragment, "Sensor")
//        adapter.addFrag(mIRFragment, "IR")
        mLayoutBinding.view.vpMain.adapter = adapter
//        mLayoutBinding.view.tlBottom.setupWithViewPager(mLayoutBinding.view.vpMain)
    }

    private fun getHome() {
        Api.request(this, Api.service.getHome(),
                Consumer { result ->
                    mCurrentHome = result.data?.firstOrNull()
                    mListDevice.clear()
                    mListSensor.clear()
                    result.data?.firstOrNull()?.devices?.forEach { device ->
                        if (device.type == "control") {
                            mListDevice.add(device)
                        } else {
                            mListSensor.add(device)
                        }
                    }
//                for(i in 0..10){
//                    mListDevice.add((mListDevice.clone() as ArrayList<Device>)[0])
//                }
                    mSharedPreference.currentHome = result.data?.firstOrNull()
                    setupRecyclerView()
                    initViewPager()
                    subscribeButtonState()
                    Log.d("getDeviceApi", result.data?.size.toString())
                }, Consumer {
            Log.d("getDeviceApi", it.message.toString())
        })
    }

    private fun subscribeButtonState() {
        if (mMqttClient.isConnected()) {
            mMqttClient.subscribe(
                    mSharedPreference.currentUser?.id.toString(),
                    1,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d("MQTTClient", "Subscribe button state success ${mSharedPreference.currentUser?.id}")
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
//        mDeviceAdapter = DeviceAdapter(mListDevice, this)
//        mLayoutBinding.view.rvDevice.adapter = mDeviceAdapter
//        mLayoutBinding.view.rvDevice.layoutManager = LinearLayoutManager(this)
//        (mLayoutBinding.view.rvDevice.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        setHomeData()
    }

    private fun setHomeData() {
        mLayoutBinding.layoutNavigation.tvHomeName.text = mCurrentHome?.name
    }

    fun String.getBytesByString(): ByteArray? {
        return try {
            this.toByteArray(charset("utf-8"))
        } catch (e: UnsupportedEncodingException) {
            throw IllegalArgumentException("the charset is invalid")
        }
    }

    @Suppress("DEPRECATION")
    private fun isLocationServiceRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.getRunningServices(Int.MAX_VALUE).forEach {
            if (LocationService::class.java.name == it.service.className)
                return true
        }
        return false
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
        if (requestCode == 1 && data != null) {
            val result = data.extras?.getString("result", "") ?: ""
            val dataString = result.replace("https://www.google.com/maps/place/", "")
            val rawLatlng = dataString.substring(dataString.indexOf("/")).replace("@", "")

            val rawAddress = dataString.replace(dataString.substring(dataString.indexOf("/")), "")
            val finalAddress = URLDecoder.decode(rawAddress)

            val latlng = rawLatlng.substring(rawLatlng.lastIndexOf(","))
            val latlng2 = rawLatlng.split(",")
            val finalLatLng = LatLng(latlng2[0].replace("/", "").toDouble(), latlng2[1].toDouble())
            Log.d("onActivityResult", "${finalLatLng.latitude},${finalLatLng.longitude}")
            Log.d("onActivityResult", finalAddress)
//            Api.request(this, Api.service.updateHomeLocation(mSharedPreference.currentHome?.id?:0, Home().apply {
//                this.address = finalAddress
//                this.geom = Geom().apply {
//                    val corninate = ArrayList<Double>()
//                    corninate[0] = finalLatLng.latitude
//                    corninate[1] = finalLatLng.longitude
//                    this.coordinates = corninate
//                }
//            }),
//                success = Consumer {
//                   mSharedPreference.currentHome = it
//                },
//                error = Consumer {
//                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//                })

//            mSharedPreference.address = finalAddress
//            mSharedPreference.latlng = finalLatLng
//            val center = Location("center")
//            val crLocation = Location("current")
//            center.latitude = finalLatLng.latitude
//            center.longitude = finalLatLng.longitude
//            crLocation.latitude = 10.8062817
//            crLocation.longitude = 106.6763683

//            val distance = center.distanceTo(crLocation)
//            Log.d("onActivityResult", distance.toString())
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