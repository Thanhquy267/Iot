package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemSensorBinding
import com.quyt.iot_demo.model.Device

class SensorAdapter(private val mListSensor: ArrayList<Device>?) : RecyclerView.Adapter<SensorViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        return SensorViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sensor, parent, false))
    }

    override fun getItemCount(): Int {
        return mListSensor?.size ?: 0
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        holder.bind(mListSensor?.get(position))
    }

    fun updateStatus(device: Device?){
        val deviceNeedUpdate =  mListSensor?.find {
            it.macAddress == device?.macAddress
        }
        val pos = mListSensor?.indexOf(deviceNeedUpdate)
        deviceNeedUpdate?.state = device?.state
        if (deviceNeedUpdate!= null){
            mListSensor?.set(pos?:0,deviceNeedUpdate)
            notifyItemChanged(pos?:0)
        }
    }
}

class SensorViewHolder(val binding: ItemSensorBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.scSwitch.isChecked = item?.state == "ON"
    }
}
