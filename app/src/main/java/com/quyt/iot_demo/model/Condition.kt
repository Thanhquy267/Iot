package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Condition {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("type")
    var type: String? = null

    @SerializedName("device")
    var device : Device? = null

    @SerializedName("time")
    var time : String? = null

    @SerializedName("location")
    var location: String? = null
}