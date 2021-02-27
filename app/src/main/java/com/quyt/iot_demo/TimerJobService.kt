package com.quyt.iot_demo

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.quyt.iot_demo.rxBus.BusMessage
import com.quyt.iot_demo.rxBus.RxBus

class TimerJobService : JobService(){

    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("Timer JobService","End")
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        val mDatabase = FirebaseDatabase.getInstance()
        mDatabase.getReference("ledState").setValue(if (params?.jobId == Constant.TURN_BULB_ON) "ON" else "OFF")
        RxBus.publish(BusMessage.OnDoneTimerJob(params?.jobId == Constant.TURN_BULB_ON))
//        Notification.createNotification(applicationContext)
        return true
    }

}