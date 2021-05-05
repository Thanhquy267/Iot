package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemConditionBinding
import com.quyt.iot_demo.model.Condition

class ConditionAdapter(private val mListCondition: ArrayList<Condition>?, val listener: ConditionListener) : RecyclerView.Adapter<ConditionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConditionViewHolder {
        return ConditionViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_condition, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mListCondition?.size ?: 0
    }

    override fun onBindViewHolder(holder: ConditionViewHolder, position: Int) {
        holder.bind(mListCondition?.get(position))
    }
}

class ConditionViewHolder(val binding: ItemConditionBinding, val listener: ConditionListener) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Condition?) {
        when (item?.type) {
            "device" -> {
                binding.ivIcon.setImageResource(if (item.device?.type == "control") R.drawable.ic_light_bulb else R.drawable.ic_sensor)
                binding.tvTitle.text = item.device?.name
                if (item.device?.type == "control") {
                    binding.tvState.text = if (item.device?.data?.state == "ON") "Bật" + " | " + item.device?.data?.brightness else "Tắt"
                } else {
                    binding.tvState.text = if (item.device?.data?.state == "ON") "Bật" else "Tắt"
                }
            }
            "time" -> {
                binding.ivIcon.setImageResource(R.drawable.ic_timer)
                binding.tvTitle.text = item.time
            }
            "location" -> {
                binding.ivIcon.setImageResource(R.drawable.ic_baseline_location_on_24)
                binding.tvTitle.text = item.location
            }
        }
        binding.cvRoot.setOnClickListener {
            listener.onConditionClicked(item)
        }
    }
}

interface ConditionListener {
    fun onConditionClicked(item: Condition?)
}
