package com.quyt.iot_demo.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.DetectedActivity
import com.quyt.iot_demo.PermissionUtils
import com.quyt.iot_demo.R
import com.quyt.iot_demo.ui.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@SuppressLint("Registered")
open class BaseLocationService : Service() {
    private var mLocationManager: LocationManager? = null

    private var mLocationListeners = arrayOf(LocationListener(LocationManager.PASSIVE_PROVIDER))

    //
    private var mIntentService: Intent? = null
    private var mPendingIntent: PendingIntent? = null
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null

    private lateinit var movementReceiver: BroadcastReceiver

    private var mCurrentMovement = StatusMovementType.NOT_MOVING
    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false


    var mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        val serverInstance: BaseLocationService
            get() = this@BaseLocationService
    }

    private inner class LocationListener(provider: String) : android.location.LocationListener {
        internal var mLastLocation: Location

        init {
            Log.d(TAG,"LocationListener $provider")
            mLastLocation = Location(provider)
        }

        override fun onLocationChanged(location: Location) {
            Log.d(TAG,"onLocationChanged: $location")
            mLastLocation.set(location)
            onLocationUpdate(location, mCurrentMovement)
        }

        override fun onProviderDisabled(provider: String) {
            Log.d(TAG,"onProviderDisabled: $provider")
        }

        override fun onProviderEnabled(provider: String) {
            Log.d(TAG,"onProviderEnabled: $provider")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            Log.d(TAG,"onStatusChanged: $provider")
        }
    }

    override fun onBind(arg0: Intent): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
//        Log.d(TAG,"onStartCommand")
//        super.onStartCommand(intent, flags, startId)
//        return START_REDELIVER_INTENT
        Log.d(TAG,"onStartCommand executed with startId: $startId")
        val action = intent.action
        Log.d(TAG,"using an intent with action $action")
        when (action) {
            Actions.START.name -> startService()
            Actions.STOP.name -> stopService()
            else -> Log.d(TAG,"This should never happen. No action in the received intent")
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }



    override fun onCreate() {
        initMovementRelated()
        Log.d(TAG,"onCreate")

        initializeLocationManager()
        onLocationUpdate(null, mCurrentMovement)
        val notification = createNotification()
        startForeground(1, notification)
        try {
            mLocationManager!!.requestLocationUpdates(
                    LocationManager.PASSIVE_PROVIDER,
                    LOCATION_INTERVAL.toLong(),
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            )
        } catch (ex: SecurityException) {
            Log.d(TAG,"fail to request location update, ignore")
        }
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
//        GlobalScope.launch(Dispatchers.IO) {
//            while (isServiceStarted) {
//                launch(Dispatchers.IO) {
//                    pingFakeServer()
//                }
//                delay(1 * 60 * 1000)
//            }
//            Log.d(TAG,"End of the loop for the service")
//        }
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

    private fun initMovementRelated() {
        movementReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == "activity_intent") {
                    val type = intent.getIntExtra("type", -1)
                    handleUserActivity(type)
                }
            }
        }
        //
        LocalBroadcastManager.getInstance(this).registerReceiver(movementReceiver,
                IntentFilter("activity_intent"))
        //
        // handle movement detection
        mActivityRecognitionClient = ActivityRecognitionClient(this)
        mIntentService = Intent(this, DetectedActivitiesIntentService::class.java)
        DetectedActivitiesIntentService.mShowContinue = true
        mPendingIntent = PendingIntent.getService(this, 1, mIntentService!!, PendingIntent.FLAG_UPDATE_CURRENT)
        requestActivityUpdatesButtonHandler()
    }

    private fun handleUserActivity(type: Int) {

        mCurrentMovement = when (type) {
            DetectedActivity.IN_VEHICLE -> {
               StatusMovementType.ON_VEHICLE
            }
            DetectedActivity.ON_BICYCLE -> {
                StatusMovementType.ON_BICYCLE
            }
            DetectedActivity.ON_FOOT -> {
                StatusMovementType.WALKING
            }
            DetectedActivity.RUNNING -> {
                StatusMovementType.RUNNING
            }
            DetectedActivity.STILL -> {
                StatusMovementType.NOT_MOVING
            }
            DetectedActivity.TILTING -> {
                StatusMovementType.NOT_MOVING
            }
            DetectedActivity.WALKING -> {
                StatusMovementType.WALKING
            }
            DetectedActivity.UNKNOWN -> {
                StatusMovementType.WALKING
            }
            else -> {
                StatusMovementType.WALKING
            }
        }

    }


    open fun onLocationUpdate(lastLocation: Location? = GPSTracker(this).getLocation(), movement: StatusMovementType){}

    override fun onDestroy() {
        DetectedActivitiesIntentService.mShowContinue = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(movementReceiver)
        Log.d(TAG,"The service has been destroyed".toUpperCase())
        super.onDestroy()
        if (mLocationManager != null) {
            for (i in mLocationListeners.indices) {
                try {
                    if (!PermissionUtils.isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION) ||
                          !PermissionUtils.isPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)  ) {
                        return
                    }
                    mLocationManager!!.removeUpdates(mLocationListeners[i])
                } catch (ex: Exception) {
                    Log.d(TAG,"fail to remove location listener, ignore $ex")
                }

            }
        }
        //
        removeActivityUpdatesButtonHandler()
    }

    private fun initializeLocationManager() {
        Log.d(TAG,"initializeLocationManager - LOCATION_INTERVAL: $LOCATION_INTERVAL LOCATION_DISTANCE: $LOCATION_DISTANCE")
        if (mLocationManager == null) {
            mLocationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun requestActivityUpdatesButtonHandler() {
        mActivityRecognitionClient?.requestActivityUpdates(
            (30 * 1000).toLong(),
                mPendingIntent)

    }

    private fun removeActivityUpdatesButtonHandler() {
        val task = mActivityRecognitionClient?.removeActivityUpdates(
                mPendingIntent)
        task?.addOnSuccessListener {
            Log.d(TAG,"Removed activity updates successfully!")
        }

        task?.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to remove activity updates!",
                    Toast.LENGTH_SHORT).show()
            Log.d(TAG,"Removed activity updates successfully!")
        }
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
                it.enableVibration(false)
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
            .setContentTitle("Vị trí")
            .setContentText("Ứng dụng đang sử dụng vị trí")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.logo_2)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

    companion object {
        const val TAG = "BaseLocationService"
        private const val LOCATION_INTERVAL = 1000
        private const val LOCATION_DISTANCE = 5f
    }
}

enum class StatusMovementType(val value: String) {
    ON_VEHICLE("On vehicle"),
    ON_BICYCLE("On bicycle"),
    WALKING("Walking"),
    RUNNING("Running"),
    NOT_MOVING("Not moving")
}
