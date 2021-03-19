package com.quyt.iot_demo.adapter

import android.annotation.SuppressLint
import android.view.*
import android.widget.FrameLayout
import android.widget.Toast
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

    fun updateStatus(device: Device?){
        val deviceNeedUpdate =  mListDevice?.find {
            it.macAddress == device?.macAddress
        }
        val pos = mListDevice?.indexOf(deviceNeedUpdate)
        deviceNeedUpdate?.state = device?.state
        if (deviceNeedUpdate!= null){
            mListDevice?.set(pos?:0,deviceNeedUpdate)
            notifyItemChanged(pos?:0)
        }
    }
}

class DeviceViewHolder(val binding: ItemDeviceBinding,val listener: OnDeviceListener) : RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("ClickableViewAccessibility")
    fun bind(item: Device?) {
        binding.tvTitle.text = item?.name
        binding.scSwitch.isChecked = item?.state == "ON"
        binding.scSwitch.setOnCheckedChangeListener { _, isChecked ->
             item?.state = if(isChecked) "ON" else "OFF"
             listener.onDeviceStateChange(item,binding.scSwitch.isPressed)
        }
        binding.cvRoot.setOnClickListener {
            binding.llBrightness.visibility = if(binding.llBrightness.visibility == View.GONE) View.VISIBLE else View.GONE
        }


        val param = FrameLayout.LayoutParams(30,ViewGroup.LayoutParams.MATCH_PARENT)
        param.gravity = Gravity.START

        binding.cvProgress.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_DOWN -> {
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    val width = event.x.toInt()
                    if (width > binding.cvProgress.width || width < 0) return@setOnTouchListener true
                    param.width = width
                    binding.tvProgress.text = widthProgressToVal(width).toString()
                    if (item?.brightness != widthProgressToVal(width)){
                        listener.onBrightnessChange(item)
                    }
                    item?.brightness = widthProgressToVal(width)
                    binding.rlProgress.layoutParams = param
                    return@setOnTouchListener true
                }
                else -> {
                    return@setOnTouchListener true
                }
            }
        }
    }

    private fun widthProgressToVal(width : Int) : Int{
        val value = width / (binding.cvProgress.width/100)
        return if (value > 100) 100 else value - (value  % 1)
    }
}

interface  OnDeviceListener {
     fun onDeviceStateChange(device: Device?,isClick : Boolean)
     fun onItemClicked(device: Device?)
     fun onBrightnessChange(device: Device?)
}
