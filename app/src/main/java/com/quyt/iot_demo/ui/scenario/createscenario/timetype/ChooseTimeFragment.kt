package com.quyt.iot_demo.ui.scenario.createscenario.timetype

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.OnSnapPositionChangeListener
import com.quyt.iot_demo.custom.SnapOnScrollListener
import com.quyt.iot_demo.custom.TimeAdapter
import com.quyt.iot_demo.databinding.FragmentChooseTimeBinding
import com.quyt.iot_demo.ui.scenario.createscenario.CreateScenarioActivity

class ChooseTimeFragment : Fragment() {
    lateinit var mLayoutBinding: FragmentChooseTimeBinding
    lateinit var mActivity: CreateScenarioActivity
    private var mIsInput = false
    private lateinit var mHourAdapter: TimeAdapter
    private lateinit var mMinutesAdapter: TimeAdapter
    private var mListHour: ArrayList<Int> = ArrayList()
    private var mListMinutes: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_choose_time, container, false)
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar()
        initTime()
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            mActivity.supportFragmentManager.popBackStack()
        }
    }

    private fun initTime(){
        for (i in 0..24){
            mListHour.add(i)
        }
        for (i in 0..60){
            mListMinutes.add(i)
        }
        //
        mHourAdapter = TimeAdapter(mListHour)
        mMinutesAdapter = TimeAdapter(mListMinutes)
        //
        mLayoutBinding.rvHour.adapter = mHourAdapter
        mLayoutBinding.rvHour.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        val hourSnapHelper = LinearSnapHelper()
        hourSnapHelper.attachToRecyclerView(mLayoutBinding.rvHour)
        mLayoutBinding.rvHour.addOnScrollListener(SnapOnScrollListener(hourSnapHelper,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                    }
                }))
        //
        mLayoutBinding.rvMinutes.adapter = mMinutesAdapter
        mLayoutBinding.rvMinutes.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL,false)
        val minutesSnapHelper = LinearSnapHelper()
        minutesSnapHelper.attachToRecyclerView(mLayoutBinding.rvMinutes)
        mLayoutBinding.rvMinutes.addOnScrollListener(SnapOnScrollListener(minutesSnapHelper,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                    }
                }))
    }
    companion object {
        fun newInstance(activity: CreateScenarioActivity, isInput: Boolean = true): ChooseTimeFragment {
            val fragment = ChooseTimeFragment()
            fragment.mActivity = activity
            fragment.mIsInput = isInput
            return fragment
        }
    }
}