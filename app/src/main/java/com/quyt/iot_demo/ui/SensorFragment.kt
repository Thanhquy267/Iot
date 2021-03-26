package com.quyt.iot_demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.SensorAdapter
import com.quyt.iot_demo.databinding.FragmentSensorBinding
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.mqtt.MQTTClient

class SensorFragment : Fragment() {
    lateinit var mLayoutBinding: FragmentSensorBinding
    lateinit var mActivity: HomeActivity
    private var mListSensor = ArrayList<Device>()
    private var mSensorAdapter: SensorAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sensor, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSensorAdapter = SensorAdapter(mListSensor)
        mLayoutBinding.rvSensor.adapter = mSensorAdapter
        mLayoutBinding.rvSensor.layoutManager = LinearLayoutManager(requireContext())
        (mLayoutBinding.rvSensor.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
    }

    fun changeStatus(device: Device?) {
        mSensorAdapter?.updateStatus(device)
    }

    companion object {
        fun newInstance(activity: HomeActivity, listSensor: ArrayList<Device>,mMqttClient : MQTTClient): SensorFragment {
            val fragment = SensorFragment()
            fragment.mActivity = activity
            fragment.mListSensor = listSensor
            return fragment
        }
    }

}