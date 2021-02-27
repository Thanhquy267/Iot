package com.quyt.iot_demo

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.ikovac.timepickerwithseconds.MyTimePickerDialog
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.DialogTimerBinding
import com.quyt.iot_demo.rxBus.BusMessage
import com.quyt.iot_demo.rxBus.RxBus
import java.util.function.Consumer


class TimerDialog : DialogFragment() {
    private lateinit var mLayoutBinding: DialogTimerBinding
    val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(context!!) }

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
        Handler().postDelayed({
            availableOnTimer()
            availableOffTimer()
        }, 300)

        mLayoutBinding.tvTimeTurnOn.setOnClickListener {
            turnOnTimePicker()
        }
        mLayoutBinding.tvTimeTurnOff.setOnClickListener {
            turnOffTimePicker()
        }
        mLayoutBinding.cbOn.setOnCheckedChangeListener { _, isChecked ->
            availableOnTimer(isChecked)
            if (!isChecked) cancelTimerJob(context!!)
        }
        mLayoutBinding.cbOff.setOnCheckedChangeListener { _, isChecked ->
            availableOffTimer(isChecked)
            if (!isChecked) cancelTimerJob(context!!, false)
        }
        listenData()
    }

    private fun listenData() {
        RxBus.listen(BusMessage.OnDoneTimerJob::class.java, Consumer {
            if (it.isOn) {
                mLayoutBinding.tvTimeTurnOn.text = "0 giây"
                mLayoutBinding.cbOn.isChecked = false
                availableOnTimer(false)
            } else {
                mLayoutBinding.tvTimeTurnOff.text = "0 giây"
                mLayoutBinding.cbOff.isChecked = false
                availableOffTimer(false)
            }
        })
    }

    private fun availableOnTimer(on: Boolean = false) {
        mLayoutBinding.tvTimeTurnOn.alpha = if (on) 1f else 0.5f
        mLayoutBinding.tvTimeTurnOn.isClickable = on
    }

    private fun availableOffTimer(off: Boolean = false) {
        mLayoutBinding.tvTimeTurnOff.alpha = if (off) 1f else 0.5f
        mLayoutBinding.tvTimeTurnOff.isClickable = off
    }

    private fun turnOnTimePicker() {
        val mTimePicker =
            MyTimePickerDialog(
                context,
                MyTimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, seconds ->
                    mLayoutBinding.tvTimeTurnOn.text = milliToTimeFormat(hourOfDay, minute, seconds)
                    scheduleTimerJob(context!!, dateToMillisecond(hourOfDay, minute, seconds))
                },
                0,
                0,
                0,
                true
            )
        mTimePicker.show()
    }

    private fun turnOffTimePicker() {
        val mTimePicker =
            MyTimePickerDialog(
                context,
                MyTimePickerDialog.OnTimeSetListener { view, hourOfDay, minute, seconds ->
                    mLayoutBinding.tvTimeTurnOff.text =
                        milliToTimeFormat(hourOfDay, minute, seconds)
                    scheduleTimerJob(
                        context!!,
                        dateToMillisecond(hourOfDay, minute, seconds),
                        false
                    )
                },
                0,
                0,
                0,
                true
            )
        mTimePicker.show()
    }

    private fun dateToMillisecond(hour: Int, minutes: Int, second: Int): Long {
        return (second * 1000 + minutes * 60000 + hour * 3600000).toLong()
    }

    private fun milliToTimeFormat(hour: Int, minutes: Int, second: Int): String {
        var timeText = ""
        if (hour > 0) {
            timeText += "${hour}h "
        }
        if (minutes > 0) {
            timeText += "${minutes}phút "
        }
        if (second > 0) {
            timeText += "${second}giây"
        }
        return timeText
    }

    private fun scheduleTimerJob(
        context: Context,
        timerInMilliseconds: Long,
        isOn: Boolean = true
    ) {
        val componentName = ComponentName(context, TimerJobService::class.java)
        val info = JobInfo.Builder(
            if (isOn) Constant.TURN_BULB_ON else Constant.TURN_BULB_OFF,
            componentName
        )
            .setRequiresCharging(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(false)
            .setMinimumLatency(timerInMilliseconds)
            .build()
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val resultCode = scheduler.schedule(info)
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("Timer JobService", "JobService job scheduled")
        } else {
            Log.d("Timer JobService", "JobService job scheduling failed")
        }
    }

    private fun cancelTimerJob(context: Context, isOn: Boolean = true) {
        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(if (isOn) Constant.TURN_BULB_ON else Constant.TURN_BULB_OFF)
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