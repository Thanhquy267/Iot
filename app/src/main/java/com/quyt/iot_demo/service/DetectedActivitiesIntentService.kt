package com.quyt.iot_demo.service

import android.app.IntentService
import android.content.Intent
import android.util.Log

import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

import java.util.ArrayList

open class DetectedActivitiesIntentService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        val result = ActivityRecognitionResult.extractResult(intent)

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        val detectedActivities = result.probableActivities as ArrayList<DetectedActivity>

        for (activity in detectedActivities) {
            if (!mShowContinue) {
                stopSelf()
                return
            }
            Log.e(TAG, "Detected activity: " + activity.type + ", " + activity.confidence)
            broadcastActivity(activity)
        }
    }

    private fun broadcastActivity(activity: DetectedActivity) {
        val intent = Intent("activity_intent")
        intent.putExtra("type", activity.type)
        intent.putExtra("confidence", activity.confidence)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    companion object {

        protected val TAG = DetectedActivitiesIntentService::class.java.simpleName ?: ""
        var mShowContinue = true
    }
}// Use the TAG to name the worker thread.
