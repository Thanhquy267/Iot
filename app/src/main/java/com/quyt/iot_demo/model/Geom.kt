package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Geom {
    @SerializedName("type")
    var type : String = "Point"

    @SerializedName("coordinates")
    var coordinates : ArrayList<Double>? = null
}