package com.quyt.iot_demo.ui.auto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.ContextSensorAdapter
import com.quyt.iot_demo.adapter.ContextSensorListener
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.DialogSensorStateBinding
import com.quyt.iot_demo.databinding.FragmentIfBinding
import com.quyt.iot_demo.model.Context
import com.quyt.iot_demo.model.Device

class IfFragment : Fragment(), ContextSensorListener {
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(requireContext()) }
    lateinit var mLayoutBinding: FragmentIfBinding
    lateinit var mActivity: AutoActivity
    private var mListSensor = ArrayList<Device>()
    private var mSensorAdapter: ContextSensorAdapter? = null
    private var mContextModel = Context()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_if, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        mSharedPreference.currentHome?.devices?.forEach { device ->
            if (device.type == "sensor") {
                mListSensor.add(device)
            }
        }
        mSensorAdapter = ContextSensorAdapter(mListSensor, this)
        mLayoutBinding.rvSensor.adapter = mSensorAdapter
        mLayoutBinding.rvSensor.layoutManager = LinearLayoutManager(requireContext())
        (mLayoutBinding.rvSensor.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        mContextModel.sensors = ArrayList()

        mLayoutBinding.cvNext.setOnClickListener {
            val fragmentManager = mActivity.supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.rl_root, ThenFragment.newInstance(mActivity, mContextModel), ThenFragment().javaClass.simpleName)
            ft.addToBackStack(ThenFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    override fun onContextSensorClicked(item: Device?) {
        showCustomDialog(item)
    }

    private fun showCustomDialog(item: Device?) {
        if (item == null) return
        val alertDialogBuilder = android.app.AlertDialog.Builder(requireContext())
        val binding = DataBindingUtil.inflate<DialogSensorStateBinding>(
                LayoutInflater.from(requireContext()),
                R.layout.dialog_sensor_state, null, false
        )
        alertDialogBuilder.setView(binding.root)
        val alert = alertDialogBuilder.show()
        alert.setCancelable(true)
        alert.setCanceledOnTouchOutside(false)
        alert?.window?.setBackgroundDrawableResource(R.color.transparent)
        //
        val itemAdded = mContextModel.sensors?.find { it.macAddress == item.macAddress }
        if (itemAdded?.state == "ON") {
            binding.rgSensorState.check(R.id.rb_active)
        } else {
            binding.rgSensorState.check(R.id.rb_deactive)
        }
        //
        binding.rgSensorState.setOnCheckedChangeListener { group, checkedId ->
            item.state = if (checkedId == R.id.rb_active) "ON" else "OFF"
            if (itemAdded != null) {
                mContextModel.sensors?.remove(itemAdded)
            }
            mContextModel.sensors?.add(item)
            alert.dismiss()
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    companion object {
        fun newInstance(activity: AutoActivity): IfFragment {
            val fragment = IfFragment()
            fragment.mActivity = activity
            return fragment
        }
    }


}