package com.quyt.iot_demo.custom

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.LayoutCustomTimePickerBinding

class TimePickerCustom : DialogFragment() {

    private lateinit var mLayoutBinding: LayoutCustomTimePickerBinding
    private lateinit var mHourAdapter: TimeAdapter
    private lateinit var mMinutesAdapter: TimeAdapter
    private var mListHour: ArrayList<Int> = ArrayList()
    private var mListMinutes: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.layout_custom_time_picker,
                null,
                false
        )
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //
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
        mLayoutBinding.rvHour.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        val hourSnapHelper = LinearSnapHelper()
        hourSnapHelper.attachToRecyclerView(mLayoutBinding.rvHour)
        mLayoutBinding.rvHour.addOnScrollListener(SnapOnScrollListener(hourSnapHelper,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                    }
                }))
        //
        mLayoutBinding.rvMinutes.adapter = mMinutesAdapter
        mLayoutBinding.rvMinutes.layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL,false)
        val minutesSnapHelper = LinearSnapHelper()
        minutesSnapHelper.attachToRecyclerView(mLayoutBinding.rvMinutes)
        mLayoutBinding.rvMinutes.addOnScrollListener(SnapOnScrollListener(minutesSnapHelper,
                object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                    }
                }))
    }


    override fun onResume() {
        super.onResume()
        //Set dialog size
        val width = Resources.getSystem().displayMetrics.widthPixels  - 200
        val height = Resources.getSystem().displayMetrics.heightPixels - 400

        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog?.window?.setLayout(width,height)
        //
        dialog?.setCanceledOnTouchOutside(false)
    }
}

class SnapOnScrollListener(
        private val snapHelper: SnapHelper,
        var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
       ) : RecyclerView.OnScrollListener() {



    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            maybeNotifySnapPositionChange(recyclerView)
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val currentPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != currentPosition
        if (snapPositionChanged) {

            val lastView = recyclerView.layoutManager?.findViewByPosition(this.snapPosition)
            val currentView = recyclerView.layoutManager?.findViewByPosition(currentPosition)

            if (lastView != null) {
                (lastView.findViewById<AppCompatTextView>(R.id.tv_time)).apply {
//                    setTextSize(TypedValue.COMPLEX_UNIT_PX, recyclerView.context.resources.getDimension(R.dimen.normal_text))
//                    setTypeface(null, Typeface.NORMAL)
                    setTextColor(ContextCompat.getColor(this.context, R.color.greyae))
                }
            }
            if (currentView != null) {
                (currentView.findViewById<TextView>(R.id.tv_time)).apply {
//                    setTextSize(TypedValue.COMPLEX_UNIT_PX, recyclerView.context.resources.getDimension(R.dimen.large_text))
//                    setTypeface(null, Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(this.context, R.color.grey25))
                }
            }
            onSnapPositionChangeListener?.onSnapPositionChange(currentPosition)
            this.snapPosition = currentPosition
        }

    }

    fun setOnItemClick(position: Int) {
        val snapPositionChanged = this.snapPosition != position
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(position)
            this.snapPosition = position
        }
    }

    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
}

interface OnSnapPositionChangeListener {

    fun onSnapPositionChange(position: Int)
}