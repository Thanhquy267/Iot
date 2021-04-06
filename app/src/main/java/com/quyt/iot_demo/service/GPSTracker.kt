package com.quyt.iot_demo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng

@SuppressLint("Registered")
class GPSTracker(private val mContext: Context?) : Service(), LocationListener {

    companion object {
        const val LOCATION_TIME_INTERVAL = 1000L
        const val LOCATION_MINIMUM_TOLERANCE = 5f
    }

    private var isGPSEnabled = false
    private var isPassiveEnabled = false
    private var isNetworkEnabled = false
    private var canGetLocation = false
    private var location: Location? = null
    private var latitude: Double = 0.toDouble()
    private var longitude: Double = 0.toDouble()
    private var latLng: LatLng? = null
    private var locationManager: LocationManager? = null

    private val defaultLocation: Location
        get() {
            val location = Location("")
            location.latitude = 0.0
            location.longitude = 0.0
            return location
        }

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        try {
            locationManager = mContext?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager == null) {
                location = defaultLocation
                return location
            }
            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isPassiveEnabled = locationManager!!.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if (!isGPSEnabled && !isNetworkEnabled && !isPassiveEnabled) {
                location = defaultLocation
                return location
            }
            this.canGetLocation = true
            if (!permissionGranted()) {
                location = defaultLocation
                return location
            }
            if (isGPSEnabled) {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_TIME_INTERVAL, LOCATION_MINIMUM_TOLERANCE, this)
                location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                }
            }
            if (isNetworkEnabled && location == null) {
                locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_TIME_INTERVAL, LOCATION_MINIMUM_TOLERANCE, this)
                location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                }
            }
            if (isPassiveEnabled && location == null) {
                locationManager!!.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, LOCATION_TIME_INTERVAL, LOCATION_MINIMUM_TOLERANCE, this)
                location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if (location != null) {
                    latitude = location!!.latitude
                    longitude = location!!.longitude
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (location == null) {
            location = Location("")
            location!!.latitude = 0.0
            location!!.longitude = 0.0
        }
        if (location != null) stopUsingGPS()
        return location
    }

    fun getLatLng(): LatLng? {
        if (latLng != null) {
            return latLng
        }
        if (location != null) {
            latLng = LatLng(latitude, longitude)
        }
        return latLng
    }

    private fun permissionGranted(): Boolean {
        if (mContext == null) return false
        val granted = PackageManager.PERMISSION_GRANTED
        return ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == granted || ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) == granted
    }

    private fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSTracker)
        }
    }

    fun getLatitude(): Double {
        if (location != null) {
            latitude = location!!.latitude
        }
        return latitude
    }

    fun getLongitude(): Double {
        if (location != null) {
            longitude = location!!.longitude
        }
        return longitude
    }

    fun canGetLocation(): Boolean {
        return this.canGetLocation
    }

    override fun onLocationChanged(location: Location) {
        if (this.location == null || isSameLatLng(LatLng(this.location!!.latitude, this.location!!.longitude), LatLng(0.0, 0.0))) {
            this.location = location
            stopUsingGPS()
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onBind(arg0: Intent): IBinder? {
        return null
    }

    private fun isSameLatLng(left: LatLng?, right: LatLng?): Boolean {
        return left != null && right != null &&
                left.latitude.roundLatLng() == right.latitude.roundLatLng()
                && left.longitude.roundLatLng() == right.longitude.roundLatLng()
    }
}

fun Double?.roundLatLng(): Double? {
    if(this == null) return null
    try {
        return java.lang.Double.valueOf(String.format("%.5f", this))
    } catch (ex: NumberFormatException){

    }
    return null
}
