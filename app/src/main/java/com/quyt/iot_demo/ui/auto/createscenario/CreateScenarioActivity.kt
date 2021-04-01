package com.quyt.iot_demo.ui.auto.createscenario

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.ContextDeviceListener
import com.quyt.iot_demo.adapter.ScenarioDeviceAdapter
import com.quyt.iot_demo.databinding.ActivityCreateScenarioBinding
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.ui.auto.ChooseDeviceFragment

class CreateScenarioActivity : AppCompatActivity(), ContextDeviceListener {
    lateinit var mLayoutBinding: ActivityCreateScenarioBinding
    private var mListInput = ArrayList<Device>()
    private var mListOutput = ArrayList<Device>()
    private var mInputAdapter: ScenarioDeviceAdapter? = null
    private var mOutputAdapter: ScenarioDeviceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_scenario)
        initInputList()
        initOutputList()
        handleAddInput()
        handleAddOutput()
    }

    fun initInputList() {
        mInputAdapter = ScenarioDeviceAdapter(mListInput, this)
        mLayoutBinding.rvInput.adapter = mInputAdapter
        mLayoutBinding.rvInput.layoutManager = LinearLayoutManager(this)
    }

    fun initOutputList() {
        mOutputAdapter = ScenarioDeviceAdapter(mListOutput, this)
        mLayoutBinding.rvOutput.adapter = mOutputAdapter
        mLayoutBinding.rvOutput.layoutManager = LinearLayoutManager(this)
    }

    fun handleAddInput() {
        mLayoutBinding.cvAddInput.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.rl_root, ChooseDeviceFragment.newInstance(this), ChooseDeviceFragment().javaClass.simpleName)
            ft.addToBackStack(ChooseDeviceFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    fun handleAddOutput() {
        mLayoutBinding.cvAddOutput.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(R.id.rl_root, ChooseDeviceFragment.newInstance(this, false), ChooseDeviceFragment().javaClass.simpleName)
            ft.addToBackStack(ChooseDeviceFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    fun addInput(device: Device) {
        mListInput.add(device)
        mInputAdapter?.notifyDataSetChanged()
    }

    fun addOutput(device: Device) {
        mListOutput.add(device)
        mOutputAdapter?.notifyDataSetChanged()
    }

    override fun onContextDeviceClicked(item: Device?) {

    }
}