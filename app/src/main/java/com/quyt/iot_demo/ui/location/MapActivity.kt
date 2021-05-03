package com.quyt.iot_demo.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.quyt.iot_demo.data.Api
import com.quyt.iot_demo.data.SharedPreferenceHelper
import com.quyt.iot_demo.databinding.ActivityMapBinding
import com.quyt.iot_demo.model.Geom
import com.quyt.iot_demo.model.Home
import io.reactivex.functions.Consumer
import java.util.*
import kotlin.math.roundToInt


class MapActivity : BaseActivity() {
    private val mSharedPreference by lazy { SharedPreferenceHelper.getInstance(this) }
    lateinit var mLayoutBinding: ActivityMapBinding
    private var mGoogleMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var mLagLng: LatLng? = null
    private var mAddress = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_map)
        setupActionBar()
        initMap()
        mLayoutBinding.cvConfirm.setOnClickListener {
            Api.request(this, Api.service.updateHomeLocation(mSharedPreference.currentHome?.id ?: 0, Home().apply {
                this.address = mAddress
                this.geom = Geom().apply {
                    val coordinates = ArrayList<Double>()
                    coordinates.add(mLagLng?.latitude ?: 0.0)
                    coordinates.add(mLagLng?.longitude ?: 0.0)
                    this.coordinates = coordinates
                }
            }),
                    success = Consumer {
                        mSharedPreference.currentHome = it
                        finish()
                    },
                    error = Consumer {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    })
//            mSharedPreference.currentHome?.geom = Geom().apply {
//                val coordinates = ArrayList<Double>()
//                coordinates.add(mLagLng?.latitude ?: 0.0)
//                coordinates.add(mLagLng?.longitude ?: 0.0)
//                this.coordinates = coordinates
//            }
//            mSharedPreference.currentHome?.address = mAddress
//            finish()
        }
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
        mLayoutBinding.cvAddress.visibility = View.VISIBLE
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
        mLagLng = lng
        mAddress = getCompleteAddressString(lng)
        Log.d("GeocoderAddress", mAddress)
        mLayoutBinding.tvLocation.text = mAddress
    }

    private fun getCompleteAddressString(lng: LatLng): String {
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
                Log.w("My Current location address", strReturnedAddress.toString())
            } else {
                Log.w("My Current location address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("My Current location address", "Cannot get Address!")
        }
        return strAdd
    }

    private fun getColorWithAlpha(color: Int, ratio: Float): Int {
        val newColor: Int
        val alpha = (Color.alpha(color) * ratio).roundToInt()
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)
        newColor = Color.argb(alpha, r, g, b)
        return newColor
    }

}