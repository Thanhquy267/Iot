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

    @SerializedName("state")
    var state: String? = null

    @SerializedName("brightness")
    var brightness: Int? = 0

    @SerializedName("temp")
    var temp: Float? = 0f

    @SerializedName("hum")
    var hum: Int? = 0

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

    fun toPushModel() : DevicePush{
        return DevicePush().apply {
            this.macAddress = this@Device.macAddress
            this.type = this@Device.type
            this.state = this@Device.state
            this.hum = this@Device.hum
            this.temp = this@Device.temp
            this.brightness = this@Device.brightness
        }
    }

}