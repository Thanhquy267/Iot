package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemContextDeviceBinding
import com.quyt.iot_demo.model.Device

class ScenarioDeviceAdapter(private val mListDevice: ArrayList<Device>?, val listener: ContextDeviceListener) : RecyclerView.Adapter<ScenarioDeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioDeviceViewHolder {
        return ScenarioDeviceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_context_device, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mListDevice?.size ?: 0
    }

    override fun onBindViewHolder(holder: ScenarioDeviceViewHolder, position: Int) {
        holder.bind(mListDevice?.get(position))
    }

    fun changeState(item: Device) {
        mListDevice?.forEachIndexed { index, device ->
            if (device.id == item.id) {
                device.state = item.state
//                device.brightness = item.brightness
                notifyItemChanged(index)
            }
        }
    }
}

class ScenarioDeviceViewHolder(val binding: ItemContextDeviceBinding, val listener: ContextDeviceListener) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.ivIcon.setImageResource(if (item?.type == "control") R.drawable.ic_light_bulb else R.drawable.ic_sensor)
        binding.tvTitle.text = item?.name
        if (item?.type == "control") {
            binding.tvState.text = if (item.state == "ON") "Bật" + " | " + item.brightness else "Tắt"
        } else {
            binding.tvState.text = if (item?.state == "ON") "Bật" else "Tắt"
        }
        binding.cvRoot.setOnClickListener {
            listener.onContextDeviceClicked(item)
        }
    }
}

interface ContextDeviceListener {
    fun onContextDeviceClicked(item: Device?)
}
