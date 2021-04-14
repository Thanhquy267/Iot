package com.quyt.iot_demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemScenarioBinding
import com.quyt.iot_demo.model.Scenario

class ScenarioAdapter(private var mListScenario: ArrayList<Scenario?>?, val listener: OnScenarioListener) : RecyclerView.Adapter<ScenarioViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        return ScenarioViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_scenario, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mListScenario?.size ?: 0
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        return holder.bind(mListScenario?.get(position))
    }

    fun deleteScenario(scenario: Scenario?) {
        val pos = mListScenario?.indexOf(scenario)
        mListScenario?.remove(scenario)
        notifyItemRemoved(pos ?: 0)
    }
}

class ScenarioViewHolder(val binding: ItemScenarioBinding, val listener: OnScenarioListener) : RecyclerView.ViewHolder(binding.root) {
    fun bind(scenario: Scenario?) {
        binding.tvName.text = scenario?.name
        binding.scActive.isChecked = scenario?.isActivate ?: false
        binding.cvRoot.setOnClickListener {
            listener.onItemClicked(scenario)
        }
        binding.cvRoot.setOnLongClickListener {
            listener.onItemLongClicked(scenario)
            return@setOnLongClickListener true
        }
        binding.scActive.setOnCheckedChangeListener { _, isChecked ->
            scenario?.isActivate = isChecked
            listener.onStateChange(scenario)
        }
    }
}

interface OnScenarioListener {
    fun onItemClicked(scenario: Scenario?)
    fun onItemLongClicked(scenario: Scenario?)
    fun onStateChange(scenario: Scenario?)
}
