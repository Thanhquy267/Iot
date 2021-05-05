package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Device {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("type")
    var type: String? = null

    @SerializedName("macAddress")
    var macAddress: String? = null

    @SerializedName("name")
    var name: String? = null

    @SerializedName("userId")
    var userId: Int? = null

    @SerializedName("data")
    var data: DeviceData? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

}

class DeviceData {

    @SerializedName("state")
    var state: String? = null

    @SerializedName("brightness")
    var brightness: Int = 0
}