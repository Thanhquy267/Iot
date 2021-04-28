package com.quyt.iot_demo.ui.scenario

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.quyt.iot_demo.R
import com.quyt.iot_demo.adapter.OnScenarioListener
import com.quyt.iot_demo.adapter.ScenarioAdapter
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityScenarioBinding
import com.quyt.iot_demo.model.Scenario
import com.quyt.iot_demo.ui.scenario.createscenario.CreateScenarioActivity
import io.reactivex.functions.Consumer


class ScenarioActivity : AppCompatActivity(), OnScenarioListener {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    lateinit var mLayoutBinding: ActivityScenarioBinding
    private var mListScenario = ArrayList<Scenario?>()
    private var mScenarioAdapter: ScenarioAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_scenario)
        setupActionBar()
    }

    override fun onResume() {
        super.onResume()
        getScenarios()
    }

    override fun onItemClicked(scenario: Scenario?) {
        val intent = Intent(this, CreateScenarioActivity::class.java)
        intent.putExtra("SCENARIO", Gson().toJson(scenario))
        startActivity(intent)
    }

    override fun onItemLongClicked(scenario: Scenario?) {
        val options = arrayOf("Xóa kịch bản")

        val builder = AlertDialog.Builder(this)
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    deleteScenario(scenario, Consumer {
                        mScenarioAdapter?.deleteScenario(scenario)
                    })
                }
            }
        }
        builder.show()
    }

    override fun onStateChange(scenario: Scenario?) {
        updateScenario(scenario)
    }

    private fun getScenarios() {
        Api.request(this, Api.service.getScenario(mSharedPreference.currentHome?.id
                ?: 0, mSharedPreference.currentUser?.id ?: 0),
                Consumer { result ->
                    mListScenario.clear()
                    result.data?.forEach {
                        mListScenario.add(it)
                    }
                    initRecyclerView()
                }, Consumer {})
    }

    private fun updateScenario(scenario: Scenario?) {
        Api.request(this, Api.service.updateScenario(scenario?.id ?: 0, scenario!!),
                Consumer {
                }, Consumer {})
    }

    private fun deleteScenario(scenario: Scenario?, success: Consumer<Unit>) {
        Api.request(this, Api.service.deleteScenario(scenario?.id ?: 0),
                Consumer {
                    success.accept(Unit)
                }, Consumer {})
    }

    private fun initRecyclerView() {
        mScenarioAdapter = ScenarioAdapter(mListScenario, this)
        mLayoutBinding.rvScenario.adapter = mScenarioAdapter
        mLayoutBinding.rvScenario.layoutManager = LinearLayoutManager(this)
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
        mLayoutBinding.ivAdd.setOnClickListener {
            val intent = Intent(this, CreateScenarioActivity::class.java)
            startActivity(intent)
        }
    }


}