package com.quyt.iot_demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.ContextDeviceAdapter
import com.quyt.iot_demo.adapter.ContextDeviceListener
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.DialogDeviceStateBinding
import com.quyt.iot_demo.databinding.FragmentThenBinding
import com.quyt.iot_demo.model.Context
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.ui.auto.AutoActivity
import io.reactivex.functions.Consumer

class ThenFragment : Fragment(), ContextDeviceListener {
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(requireContext()) }
    lateinit var mLayoutBinding: FragmentThenBinding
    lateinit var mActivity: AutoActivity
    private var mListDevice = ArrayList<Device>()
    private var mSensorAdapter: ContextDeviceAdapter? = null
    private var mContextModel = Context()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_then, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        mListDevice.clear()
        mSharedPreference.currentHome?.devices?.forEach { device ->
            if (device.type == "control") {
                mListDevice.add(device)
            }
        }
        mSensorAdapter = ContextDeviceAdapter(mListDevice, this)
        mLayoutBinding.rvDevice.adapter = mSensorAdapter
        mLayoutBinding.rvDevice.layoutManager = LinearLayoutManager(requireContext())
        (mLayoutBinding.rvDevice.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mContextModel.devices = ArrayList()

        mLayoutBinding.cvDone.setOnClickListener {
            Api.request(requireContext(), Api.service.createContext(mContextModel),
                    success = Consumer {
                        mActivity.finish()
                    },
                    error = Consumer {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    })
        }
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
        //
        val itemAdded = mContextModel.devices?.find { it.macAddress == item.macAddress }
        if (itemAdded?.state == "ON") {
            binding.rgSensorState.check(R.id.rb_on)
        } else {
            binding.rgSensorState.check(R.id.rb_off)
        }
        binding.sbBrightness.progress = itemAdded?.brightness ?: 0
        //
        binding.sbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                item.brightness = seekBar?.progress ?: 0
                if (itemAdded != null) {
                    mContextModel.devices?.remove(itemAdded)
                }
                mContextModel.devices?.add(item)
            }
        })

        binding.rgSensorState.setOnCheckedChangeListener { group, checkedId ->
            item.state = if (checkedId == R.id.rb_on) "ON" else "OFF"
            if (itemAdded != null) {
                mContextModel.devices?.remove(itemAdded)
            }
            mContextModel.devices?.add(item)
            alert.dismiss()
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(activity: AutoActivity, contextModel: Context): ThenFragment {
            val fragment = ThenFragment()
            fragment.mActivity = activity
            fragment.mContextModel = contextModel
            return fragment
        }
    }


}