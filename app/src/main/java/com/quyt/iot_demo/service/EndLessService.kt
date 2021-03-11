package com.quyt.iot_demo.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.quyt.iot_demo.R
import com.quyt.iot_demo.service.BaseLocationService.Companion.TAG
import com.quyt.iot_demo.ui.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class EndlessService : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG,"Some component want to bind with the service")
        // We don't provide binding, so return null
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            Log.d(TAG,"using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Log.d(TAG,"This should never happen. No action in the received intent")
            }
        } else {
            Log.d(TAG,
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"The service has been created".toUpperCase())
        var notification = createNotification()
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"The service has been destroyed".toUpperCase())
        Toast.makeText(this, "Service destroyed", Toast.LENGTH_SHORT).show()
    }

    private fun startService() {
        if (isServiceStarted) return
        Log.d(TAG,"Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        // we're starting a loop in a coroutine
        GlobalScope.launch(Dispatchers.IO) {
            while (isServiceStarted) {
                launch(Dispatchers.IO) {
                    pingFakeServer()
                }
                delay(1 * 60 * 1000)
            }
            Log.d(TAG,"End of the loop for the service")
        }
    }

    private fun stopService() {
        Log.d(TAG,"Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }
            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            Log.d(TAG,"Service stopped without being started: ${e.message}")
        }
        isServiceStarted = false
        setServiceState(this, com.quyt.iot_demo.service.ServiceState.STOPPED)
    }

    private fun pingFakeServer() {
//        val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.mmmZ")
//        val gmtTime = df.format(Date())
//
//        val deviceId = Settings.Secure.getString(applicationContext.contentResolver, Settings.Secure.ANDROID_ID)
//
//        val json =
//            """
//                {
//                    "deviceId": "$deviceId",
//                    "createdAt": "$gmtTime"
//                }
//            """
//        try {
//            Fuel.post("https://jsonplaceholder.typicode.com/posts")
//                .jsonBody(json)
//                .response { _, _, result ->
//                    val (bytes, error) = result
//                    if (bytes != null) {
//                        Log.d(TAG,"[response bytes] ${String(bytes)}")
//                    } else {
//                        Log.d(TAG,"[response error] ${error?.message}")
//                    }
//                }
//        } catch (e: Exception) {
//            Log.d(TAG,"Error making the request: ${e.message}")
//        }
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "ENDLESS SERVICE CHANNEL"

        // depending on the Android API that we're dealing with we will have
        // to use a specific method to create the notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
            val channel = NotificationChannel(
                notificationChannelId,
                "Endless Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "Endless Service channel"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, HomeActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("Endless Service")
            .setContentText("This is your favorite endless service working")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_2)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }
}

enum class Actions {
    START,
    STOP
}