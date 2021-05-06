package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemSensorBinding
import com.quyt.iot_demo.databinding.ItemTempHumBinding
import com.quyt.iot_demo.model.Device

class SensorAdapter(private val mListSensor: ArrayList<Device>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1){
            TempSensorViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_temp_hum, parent, false))
        }else{
            SensorViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_sensor, parent, false))
        }
    }

    override fun getItemCount(): Int {
        return mListSensor?.size ?: 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (mListSensor?.get(position)?.hum != 0 && mListSensor?.get(position)?.temp != 0f) 1 else 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SensorViewHolder) {
            holder.bind(mListSensor?.get(position))
        }else if (holder is TempSensorViewHolder){
            holder.bind(mListSensor?.get(position))
        }
    }

    fun updateStatus(device: Device?) {
        if(device?.temp != 0f && device?.hum != 0){
            val deviceNeedUpdate = mListSensor?.find {
                it.macAddress == device?.macAddress
            }
            val pos = mListSensor?.indexOf(deviceNeedUpdate)
            deviceNeedUpdate?.hum = device?.hum?:0
            deviceNeedUpdate?.temp = device?.temp?:0f
            if (deviceNeedUpdate != null) {
                mListSensor?.set(pos ?: 0, deviceNeedUpdate)
                notifyItemChanged(pos ?: 0)
            }
        }else{
            val deviceNeedUpdate = mListSensor?.find {
            it.macAddress == device?.macAddress
        }
        val pos = mListSensor?.indexOf(deviceNeedUpdate)
        deviceNeedUpdate?.state = device?.state
        if (deviceNeedUpdate != null) {
            mListSensor?.set(pos ?: 0, deviceNeedUpdate)
            notifyItemChanged(pos ?: 0)
        }
        }
    }

    fun setData(listData: ArrayList<Device>) {
        mListSensor?.clear()
        ArrayList(listData).forEach {
            mListSensor?.add(it)
        }
        notifyDataSetChanged()
    }

}

class SensorViewHolder(val binding: ItemSensorBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.ivIcon.imageTintList = if (item?.state == "ON") ColorStateList.valueOf(ContextCompat.getColor(binding.root.context,R.color.bluef5))
        else ColorStateList.valueOf(ContextCompat.getColor(binding.root.context,R.color.normal_text))
    }
}

class TempSensorViewHolder(val binding: ItemTempHumBinding) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        val temp = "${item?.temp}°C"
        val hum = "${item?.hum}%"
        binding.tvHum.text = hum
        binding.tvTemp.text = temp
    }
}
