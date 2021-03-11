package com.quyt.iot_demo.custom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ItemTimeBinding

class TimeAdapter(var mListTime: ArrayList<Int>) : RecyclerView.Adapter<TimeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_time, parent, false))
    }

    override fun getItemCount(): Int {
        return mListTime.size
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        return holder.bind(mListTime[position], mListTime.size)
    }
}

class TimeViewHolder(val binding: ItemTimeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Int,size : Int) {
        binding.tvTime.text = item.toString()
        when(adapterPosition){
            0 ->{
              binding.rlTopView.visibility = View.VISIBLE
            }
            size - 1 -> {
                binding.rlBottomView.visibility = View.VISIBLE
            }

            else ->{
                binding.rlTopView.visibility = View.GONE
                binding.rlBottomView.visibility = View.GONE
            }
        }
    }
}
