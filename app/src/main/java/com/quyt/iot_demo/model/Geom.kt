package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Geom {
    @SerializedName("coordinates")
    var coordinates : ArrayList<Double>? = null
}