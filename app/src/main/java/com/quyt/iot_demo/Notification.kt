package com.quyt.iot_demo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.quyt.iot_demo.ui.HomeActivity
import java.util.*

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
            var resultIntent = Intent(context, HomeActivity::class.java)
//                resultIntent = HomeActivity.createIntent(context, true)
            resultIntent.putExtra("TURN_ON",true)
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, Random().nextInt(), resultIntent, 0)
//            val pendingIntent: PendingIntent = HomeActivity.createIntent(context,true).let { notificationIntent ->
////                notificationIntent.putExtra("TURN_ON",true)
//                PendingIntent.getActivity(context, Random().nextInt(), notificationIntent, 0)
//            }
            val builder = NotificationCompat.Builder(GlobalApplication.appContext,"123")
            builder.setSmallIcon(R.drawable.logo_2)
                .setContentIntent(pendingIntent)
                .setContentTitle("Tính năng tự động")
                .setContentText("Bạn đã về nhà, bạn có muốn bật đèn không?")
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            val notification = builder.build()
            notificationManager.notify(123, notification)
        }
    }
}