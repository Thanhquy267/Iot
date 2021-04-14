package com.quyt.iot_demo.custom

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.quyt.iot_demo.R

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
                    setTextColor(ContextCompat.getColor(this.context, R.color.normal_text))
                }
            }
            if (currentView != null) {
                (currentView.findViewById<TextView>(R.id.tv_time)).apply {
//                    setTextSize(TypedValue.COMPLEX_UNIT_PX, recyclerView.context.resources.getDimension(R.dimen.large_text))
//                    setTypeface(null, Typeface.BOLD)
                    setTextColor(ContextCompat.getColor(this.context, R.color.title_text))
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