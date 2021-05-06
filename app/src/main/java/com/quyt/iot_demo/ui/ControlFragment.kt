package com.quyt.iot_demo.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.DeviceAdapter
import com.quyt.iot_demo.adapter.OnDeviceListener
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.FragmentControlBinding
import com.quyt.iot_demo.model.ActionType
import com.quyt.iot_demo.model.ClientType
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.PushMqtt
import com.quyt.iot_demo.mqtt.MQTTClient
import io.reactivex.functions.Consumer
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

class ControlFragment : Fragment(), OnDeviceListener, SwipeRefreshLayout.OnRefreshListener {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(requireContext()) }
    lateinit var mLayoutBinding: FragmentControlBinding
    lateinit var mActivity: HomeActivity
    private var mListDevice = ArrayList<Device>()
    private var mDeviceAdapter: DeviceAdapter? = null
    private var mDeviceLayoutManager : GridLayoutManager? = null
    private var mMqttClient: MQTTClient? = null
    private var mHomeId = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_control, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        initSwipeToRefreshLayout()
        getDevices()
    }


    override fun onDeviceStateChange(device: Device?, isClick: Boolean) {
        if (!isClick) return
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = ActionType.CHANGE_STATE.value
            data = device
        }
        mMqttClient?.publish(
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
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("device", Gson().toJson(device))
        startActivity(intent)
    }

    override fun onBrightnessChange(device: Device?) {
        val pushBody = PushMqtt().apply {
            clientType = ClientType.APP_TYPE.value
            actionType = ActionType.CHANGE_STATE.value
            data = device
        }
        mMqttClient?.publish(
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

    override fun onRefresh() {
        getDevices()
    }

    private fun initRecyclerView() {
        mDeviceLayoutManager = GridLayoutManager(requireContext(),1)
        mDeviceAdapter = DeviceAdapter(mDeviceLayoutManager,ArrayList(), this)
        mLayoutBinding.rvDevice.adapter = mDeviceAdapter
        mLayoutBinding.rvDevice.layoutManager = mDeviceLayoutManager
        (mLayoutBinding.rvDevice.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mLayoutBinding.ivList.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bluef5), android.graphics.PorterDuff.Mode.SRC_IN)
        mLayoutBinding.ivList.setOnClickListener {
            mDeviceLayoutManager?.spanCount = 1
            mDeviceAdapter?.notifyItemRangeChanged(0,mDeviceAdapter?.itemCount?:0)
            mLayoutBinding.ivList.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bluef5), android.graphics.PorterDuff.Mode.SRC_IN)
            mLayoutBinding.ivGrid.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
        }
        mLayoutBinding.ivGrid.setOnClickListener {
            mDeviceLayoutManager?.spanCount = 2
            mDeviceAdapter?.notifyItemRangeChanged(0,mDeviceAdapter?.itemCount?:0)
            mLayoutBinding.ivList.setColorFilter(ContextCompat.getColor(requireContext(), R.color.white), android.graphics.PorterDuff.Mode.SRC_IN)
            mLayoutBinding.ivGrid.setColorFilter(ContextCompat.getColor(requireContext(), R.color.bluef5), android.graphics.PorterDuff.Mode.SRC_IN)
        }

    }

    private fun initSwipeToRefreshLayout() {
        mLayoutBinding.swipeContainer.setOnRefreshListener(this)
        mLayoutBinding.swipeContainer.setColorSchemeResources(R.color.bluef5,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
    }

    private fun getDevices() {
        mLayoutBinding.swipeContainer.isRefreshing = true
        Api.request(requireContext(), Api.service.getDevice(mSharedPreference.currentHome?.id
                ?: 0, "control"),
                Consumer {
                    mListDevice.clear()
                    it.data?.forEach { device ->
                        mListDevice.add(device)
                    }
//                    for (i in 0..10){
//                        mListDevice.add(it.data?.get(0)!!)
//                    }
                    mDeviceAdapter?.setData(mListDevice)
                    mLayoutBinding.swipeContainer.isRefreshing = false
                },
                Consumer {
                    mLayoutBinding.swipeContainer.isRefreshing = false
                }, false)
    }

    fun syncButtonState(device: Device?) {
        mDeviceAdapter?.updateStatus(device)
    }

    companion object {
        fun newInstance(activity: HomeActivity, mMqttClient: MQTTClient): ControlFragment {
            val fragment = ControlFragment()
            fragment.mActivity = activity
            fragment.mMqttClient = mMqttClient
            return fragment
        }
    }

}