package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Home {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("address")
    var address: String? = null

    @SerializedName("geom")
    var geom: Geom? = null

    @SerializedName("devices")
    var devices: ArrayList<Device>? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null
}