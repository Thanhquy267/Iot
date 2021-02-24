package com.quyt.iot_demo

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.quyt.iot_demo.databinding.DialogTimerBinding
import java.text.SimpleDateFormat
import java.util.*


class TimerDialog : DialogFragment() {
    private lateinit var mLayoutBinding: DialogTimerBinding
    private var mTurnOnCal = Calendar.getInstance()
    private var mTurnOffCal = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_timer,
            null,
            false
        )
        return mLayoutBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mLayoutBinding.tvTimeTurnOn.setOnClickListener {
            turnOnDatePicker()
        }
        mLayoutBinding.tvTimeTurnOff.setOnClickListener {
            turnOffDatePicker()
        }
    }


    private fun turnOnDatePicker() {
        DatePickerDialog(
            context!!,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mTurnOnCal.set(Calendar.YEAR, year)
                mTurnOnCal.set(Calendar.MONTH, monthOfYear)
                mTurnOnCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                turnOnTimePicker()
            },
            mTurnOnCal.get(Calendar.YEAR),
            mTurnOnCal.get(Calendar.MONTH),
            mTurnOnCal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun turnOnTimePicker() {
        TimePickerDialog(
            context!!,
            OnTimeSetListener { _, hour, minute ->
                mTurnOnCal.set(Calendar.HOUR_OF_DAY, hour)
                mTurnOnCal.set(Calendar.MINUTE, minute)
                onPickTurnOnDateResult()
            },
            mTurnOnCal.get(Calendar.HOUR_OF_DAY),
            mTurnOnCal.get(Calendar.MINUTE), true
        ).show()
    }

    private fun onPickTurnOnDateResult() {
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy")
        val time = sdf.format(mTurnOnCal.time)
        mLayoutBinding.tvTimeTurnOn.text = time
    }


    private fun turnOffDatePicker() {
        DatePickerDialog(
            context!!,
            OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                mTurnOffCal.set(Calendar.YEAR, year)
                mTurnOffCal.set(Calendar.MONTH, monthOfYear)
                mTurnOffCal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                turnOffTimePicker()
            },
            mTurnOffCal.get(Calendar.YEAR),
            mTurnOffCal.get(Calendar.MONTH),
            mTurnOffCal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun turnOffTimePicker() {
        TimePickerDialog(
            context!!,
            OnTimeSetListener { _, hour, minute ->
                mTurnOffCal.set(Calendar.HOUR_OF_DAY, hour)
                mTurnOffCal.set(Calendar.MINUTE, minute)
                onPickTurnOffDateResult()
            },
            mTurnOffCal.get(Calendar.HOUR_OF_DAY),
            mTurnOffCal.get(Calendar.MINUTE), true
        ).show()
    }

    private fun onPickTurnOffDateResult() {
        val sdf = SimpleDateFormat("HH:mm - dd/MM/yyyy")
        val time = sdf.format(mTurnOffCal.time)
        mLayoutBinding.tvTimeTurnOff.text = time
    }

    override fun onResume() {
        super.onResume()
        //Set dialog size
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
//        dialog?.window?.setLayout(1000,400)
        //
        dialog?.setCanceledOnTouchOutside(true)
    }
}