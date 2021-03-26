package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Context {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("isActivate")
    var isActivate: Boolean = true

    @SerializedName("sensors")
    var sensors: ArrayList<Device>? = null

    @SerializedName("devices")
    var devices: ArrayList<Device>? = null
}