package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemContextSensorBinding
import com.quyt.iot_demo.model.Device

class ContextSensorAdapter(private val mListSensor: ArrayList<Device>?, val listener: ContextSensorListener) : RecyclerView.Adapter<ContextSensorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextSensorViewHolder {
        return ContextSensorViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_context_sensor, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mListSensor?.size ?: 0
    }

    override fun onBindViewHolder(holder: ContextSensorViewHolder, position: Int) {
        holder.bind(mListSensor?.get(position))
    }
}

class ContextSensorViewHolder(val binding: ItemContextSensorBinding, val listener: ContextSensorListener) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.cvRoot.setOnClickListener {
            listener.onContextSensorClicked(item)
        }
    }
}

interface ContextSensorListener {
    fun onContextSensorClicked(item: Device?)
}
