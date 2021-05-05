package com.quyt.iot_demo.ui.scenario.createscenario.devicetype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.ContextDeviceListener
import com.quyt.iot_demo.adapter.ScenarioDeviceAdapter
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.DialogDeviceStateBinding
import com.quyt.iot_demo.databinding.FragmentChooseDeviceBinding
import com.quyt.iot_demo.model.Condition
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.ui.scenario.createscenario.CreateScenarioActivity
import com.quyt.iot_demo.ui.scenario.createscenario.SelectScenarioTypeFragment

class ChooseDeviceFragment : Fragment(), ContextDeviceListener {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(requireContext()) }
    lateinit var mLayoutBinding: FragmentChooseDeviceBinding
    lateinit var mActivity: CreateScenarioActivity
    private var mListSensor = ArrayList<Device>()
    private var mSensorAdapter: ScenarioDeviceAdapter? = null
    private var mIsInput = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_device, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        if (mIsInput) {
            mSharedPreference.currentHome?.devices?.forEach { device ->
                mListSensor.add(device)
            }
        } else {
            mSharedPreference.currentHome?.devices?.forEach { device ->
                if (device.type == "control") {
                    mListSensor.add(device)
                }
            }
        }
        mSensorAdapter = ScenarioDeviceAdapter(mListSensor, this)
        mLayoutBinding.rvSensor.adapter = mSensorAdapter
        mLayoutBinding.rvSensor.layoutManager = LinearLayoutManager(requireContext())
        (mLayoutBinding.rvSensor.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

    }

    override fun onContextDeviceClicked(item: Device?) {
        showCustomDialog(item)
    }

    private fun showCustomDialog(item: Device?) {
        if (item == null) return
        val alertDialogBuilder = android.app.AlertDialog.Builder(requireContext())
        val binding = DataBindingUtil.inflate<DialogDeviceStateBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_device_state, null, false
        )
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        binding.tvBrightness.visibility = if (mIsInput) View.GONE else View.VISIBLE
        binding.sbBrightness.visibility = if (mIsInput) View.GONE else View.VISIBLE
        //
        binding.sbBrightness.progress = item.data?.brightness?:0
        //
        binding.sbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                item.data?.brightness = seekBar?.progress ?: 0
            }
        })

        binding.rgSensorState.setOnCheckedChangeListener { _, checkedId ->
            item.data?.state = if (checkedId == R.id.rb_on) "ON" else "OFF"
            if (mIsInput) {
                mActivity.addInput(Condition().apply {
                    type = "device"
                    device = item
                })
            } else {
                mActivity.addOutput(item)
            }
            alert.dismiss()
            val fm = mActivity.supportFragmentManager.findFragmentByTag(SelectScenarioTypeFragment().javaClass.simpleName)
            if (fm != null) {
                mActivity.supportFragmentManager.beginTransaction().remove(fm).commit()
            }
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(activity: CreateScenarioActivity, isInput: Boolean = true): ChooseDeviceFragment {
            val fragment =
                    ChooseDeviceFragment()
            fragment.mActivity = activity
            fragment.mIsInput = isInput
            return fragment
        }
    }


}