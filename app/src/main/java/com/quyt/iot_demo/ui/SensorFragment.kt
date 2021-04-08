package com.quyt.iot_demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.SensorAdapter
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.FragmentSensorBinding
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.mqtt.MQTTClient
import io.reactivex.functions.Consumer

class SensorFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(requireContext()) }
    lateinit var mLayoutBinding: FragmentSensorBinding
    lateinit var mActivity: HomeActivity
    private var mListSensor = ArrayList<Device>()
    private var mSensorAdapter: SensorAdapter? = null
    private var mHaveData = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwipeToRefreshLayout()
        initRecyclerView()
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible && !mHaveData) {
            getSensors()
        }
    }

    override fun onRefresh() {
        getSensors()
    }

    private fun initRecyclerView(){

        mSensorAdapter = SensorAdapter(ArrayList())
        mLayoutBinding.rvSensor.adapter = mSensorAdapter
        mLayoutBinding.rvSensor.layoutManager = LinearLayoutManager(requireContext())
        (mLayoutBinding.rvSensor.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    private fun initSwipeToRefreshLayout() {
        mLayoutBinding.swipeContainer.setOnRefreshListener(this)
        mLayoutBinding.swipeContainer.setColorSchemeResources(R.color.bluef5,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark)
    }

    private fun getSensors() {
        mLayoutBinding.swipeContainer.isRefreshing = true
        Api.request(requireContext(), Api.service.getDevice(mSharedPreference.currentHome?.id
                ?: 0, "sensor"),
                Consumer {
                    mListSensor.clear()
                    it.data?.forEach { device ->
                        mListSensor.add(device)
                    }
                    mSensorAdapter?.setData(mListSensor)
                    mLayoutBinding.swipeContainer.isRefreshing = false
                    mHaveData = true
                },
                Consumer {
                    mLayoutBinding.swipeContainer.isRefreshing = false
                }, false)
    }

    fun changeStatus(device: Device?) {
        mSensorAdapter?.updateStatus(device)
    }


    companion object {
        fun newInstance(activity: HomeActivity, mMqttClient: MQTTClient): SensorFragment {
            val fragment = SensorFragment()
            fragment.mActivity = activity
            return fragment
        }
    }


}