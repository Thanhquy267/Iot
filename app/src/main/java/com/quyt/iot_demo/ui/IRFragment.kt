package com.quyt.iot_demo.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.FragmentIrBinding

class IRFragment : Fragment() {
    lateinit var mLayoutBinding: FragmentIrBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_ir, container, false)
        return mLayoutBinding.root
    }
}