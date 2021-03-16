package com.quyt.iot_demo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemDeviceBinding
import com.quyt.iot_demo.model.Device

class DeviceAdapter(private val mListDevice: ArrayList<Device>?,val listener: OnDeviceListener) : RecyclerView.Adapter<DeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_device, parent, false),listener)
    }

    override fun getItemCount(): Int {
        return mListDevice?.size ?: 0
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        holder.bind(mListDevice?.get(position))
    }

}

class DeviceViewHolder(val binding: ItemDeviceBinding,val listener: OnDeviceListener) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.scSwitch.setOnCheckedChangeListener { _, isChecked ->
             item?.state = if(isChecked) "ON" else "OFF"
             listener.onDeviceStateChange(item)
        }
    }
}

interface  OnDeviceListener {
     fun onDeviceStateChange(device: Device?)
}
