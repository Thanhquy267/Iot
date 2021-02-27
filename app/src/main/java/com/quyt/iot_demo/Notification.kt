package com.quyt.iot_demo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class Notification {
    companion object{

        @RequiresApi(Build.VERSION_CODES.O)

        fun createNotification(context: Context){
            val notificationManager: NotificationManager = GlobalApplication.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()
            val notifyChanel = NotificationChannel("123", "test_chanel", NotificationManager.IMPORTANCE_HIGH)
            notifyChanel.setSound(defaultSound, null)
            notifyChanel.enableVibration(true)
            notificationManager.createNotificationChannel(notifyChanel)
            //
            val builder = NotificationCompat.Builder(GlobalApplication.appContext,"123")
            builder.setSmallIcon(R.drawable.logo_2)
                .setContentTitle("Trạng thái đèn")
                .setContentText("Đã bật")
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            val notification = builder.build()
            notificationManager.notify(123, notification)
        }
    }
}