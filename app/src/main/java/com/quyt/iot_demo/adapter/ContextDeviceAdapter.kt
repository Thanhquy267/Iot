package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemContextDeviceBinding
import com.quyt.iot_demo.model.Device

class ContextDeviceAdapter(private val mListDevice: ArrayList<Device>?, val listener: ContextDeviceListener) : RecyclerView.Adapter<ContextDeviceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextDeviceViewHolder {
        return ContextDeviceViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_context_device, parent, false), listener)
    }

    override fun getItemCount(): Int {
        return mListDevice?.size ?: 0
    }

    override fun onBindViewHolder(holder: ContextDeviceViewHolder, position: Int) {
        holder.bind(mListDevice?.get(position))
    }
}

class ContextDeviceViewHolder(val binding: ItemContextDeviceBinding, val listener: ContextDeviceListener) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.cvRoot.setOnClickListener {
            listener.onContextDeviceClicked(item)
        }
    }
}

interface ContextDeviceListener {
    fun onContextDeviceClicked(item: Device?)
}
