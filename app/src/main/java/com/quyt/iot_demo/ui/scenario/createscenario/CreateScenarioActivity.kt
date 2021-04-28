package com.quyt.iot_demo.ui.scenario.createscenario

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.ConditionAdapter
import com.quyt.iot_demo.adapter.ConditionListener
import com.quyt.iot_demo.adapter.ContextDeviceListener
import com.quyt.iot_demo.adapter.ScenarioDeviceAdapter
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityCreateScenarioBinding
import com.quyt.iot_demo.model.Condition
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.Scenario
import com.quyt.iot_demo.ui.scenario.createscenario.devicetype.ChooseDeviceFragment
import io.reactivex.functions.Consumer

class CreateScenarioActivity : AppCompatActivity(), ContextDeviceListener,ConditionListener {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    lateinit var mLayoutBinding: ActivityCreateScenarioBinding
    private var mListConditions = ArrayList<Condition>()
    private var mListOutput = ArrayList<Device>()
    private var mInputAdapter: ConditionAdapter? = null
    private var mOutputAdapter: ScenarioDeviceAdapter? = null
    private var mScenario: Scenario? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_scenario)
        mScenario = Gson().fromJson(intent?.extras?.getString("SCENARIO", ""), Scenario::class.java)
        setupActionBar()
        if (mScenario == null) {
            handleAddInput()
            handleAddOutput()
            createScenario()
        } else {
            mLayoutBinding.cvAddInput.visibility = View.GONE
            mLayoutBinding.cvAddOutput.visibility = View.GONE
            mLayoutBinding.cvCreate.visibility = View.GONE
            mLayoutBinding.etName.isFocusableInTouchMode = false
            mLayoutBinding.etName.isFocusable = false
            Handler().postDelayed({
                mLayoutBinding.etName.setText(mScenario?.name)
            }, 300)
//            mScenario?.conditions?.forEach {
//                mListConditions.add(it)
//            }
            mScenario?.actions?.forEach {
                mListOutput.add(it)
            }
        }
        initInputList()
        initOutputList()
    }

    private fun createScenario() {
        mLayoutBinding.cvCreate.setOnClickListener {
            val scenario = Scenario().apply {
                this.name = mLayoutBinding.etName.text.toString()
                this.userId = mSharedPreference.currentUser?.id ?: 0
                this.homeId = mSharedPreference.currentHome?.id ?: 0
                this.actions = mListOutput
                this.conditions = mListConditions
                this.isActivate = true
            }
            Api.request(this, Api.service.createScenario(scenario),
                    success = Consumer {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    error = Consumer {
                    })
        }
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
        mLayoutBinding.tvTitle.text = if (mScenario == null) "Tạo kịch bản" else "Chi tiết"
    }

    private fun initInputList() {
        mInputAdapter = ConditionAdapter(mListConditions, this)
        mLayoutBinding.rvInput.adapter = mInputAdapter
        mLayoutBinding.rvInput.layoutManager = LinearLayoutManager(this)
    }

    private fun initOutputList() {
        mOutputAdapter = ScenarioDeviceAdapter(mListOutput, this)
        mLayoutBinding.rvOutput.adapter = mOutputAdapter
        mLayoutBinding.rvOutput.layoutManager = LinearLayoutManager(this)
    }

    private fun handleAddInput() {
        mLayoutBinding.cvAddInput.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.setCustomAnimations(
                    R.anim.slide_in_left,
                    R.anim.slide_out_left,
                    R.anim.slide_in_right,
                    R.anim.slide_out_right
            )
            ft.replace(
                    R.id.rl_root,
                    SelectScenarioTypeFragment.newInstance(this),
                    SelectScenarioTypeFragment().javaClass.simpleName
            )
            ft.addToBackStack(ChooseDeviceFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    private fun handleAddOutput() {
        mLayoutBinding.cvAddOutput.setOnClickListener {
            val fragmentManager = supportFragmentManager
            val ft = fragmentManager.beginTransaction()
            ft.replace(
                    R.id.rl_root,
                    ChooseDeviceFragment.newInstance(this, false),
                    ChooseDeviceFragment().javaClass.simpleName
            )
            ft.addToBackStack(ChooseDeviceFragment().javaClass.simpleName)
            ft.commitAllowingStateLoss()
        }
    }

    fun addInput(device: Condition) {
        mListConditions.add(device)
        mInputAdapter?.notifyDataSetChanged()
    }

    fun addOutput(device: Device) {
        mListOutput.add(device)
        mOutputAdapter?.notifyDataSetChanged()
    }

    override fun onContextDeviceClicked(item: Device?) {
    }

    override fun onConditionClicked(item: Condition?) {

    }
}