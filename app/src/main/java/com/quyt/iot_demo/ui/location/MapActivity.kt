package com.quyt.iot_demo.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.quyt.iot_demo.R
import com.quyt.iot_demo.custom.BaseActivity
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityMapBinding
import java.util.*


class MapActivity : BaseActivity() {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    lateinit var mLayoutBinding: ActivityMapBinding
    private var mGoogleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        setupActionBar()
        initMap()
    }

    private fun setupActionBar() {
        mLayoutBinding.ivBack.setOnClickListener {
            finish()
        }
    }

    private fun initMap() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        // onMapReady
        mapFragment?.getMapAsync { googleMap ->
            mGoogleMap = googleMap
            mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mGoogleMap?.isMyLocationEnabled = true
                val currentLatLng = LatLng(mSharedPreference.currentLocation?.latitude
                        ?: 0.0, mSharedPreference.currentLocation?.longitude ?: 0.0)
                Handler().postDelayed({
                    mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
                }, 500)
            }
            //
            mGoogleMap?.setOnMapLongClickListener {
                addMarker(it)
            }
        }
        //
        mLayoutBinding.cvCurrentLocation.setOnClickListener {
            val currentLatLng = LatLng(mSharedPreference.currentLocation?.latitude
                    ?: 0.0, mSharedPreference.currentLocation?.longitude ?: 0.0)
            mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16f))
        }
    }


    private fun addMarker(lng: LatLng) {
        mGoogleMap?.clear()
        mGoogleMap?.addMarker(MarkerOptions().position(lng))
        mGoogleMap?.addCircle(
                CircleOptions()
                        .center(lng)
                        .radius(100.0)
                        .strokeColor(getColorWithAlpha(R.color.grey_33, 0.4f))
                        .strokeWidth(3.0f)
                        .fillColor(getColorWithAlpha(R.color.grey_33, 0.15f))
        )
        mGoogleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(lng, 16.5f))
        Log.d("GeocoderAddress", getCompleteAddressString(lng).toString())
    }

    private fun getCompleteAddressString(lng: LatLng): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(lng.latitude, lng.longitude, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                Log.w("My Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("My Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    fun getColorWithAlpha(color: Int, ratio: Float): Int {
        val newColor: Int
        val alpha = Math.round(Color.alpha(color) * ratio)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }

}