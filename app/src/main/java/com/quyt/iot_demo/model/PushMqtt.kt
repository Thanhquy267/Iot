package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class PushMqtt {

    @SerializedName("clientType")
    var clientType : String? = null

    @SerializedName("actionType")
    var actionType : String? = null

    @SerializedName("data")
    var data : DevicePush? = null

}

class DevicePush {

    @SerializedName("type")
    var type: String? = null

    @SerializedName("macAddress")
    var macAddress: String? = null

    @SerializedName("state")
    var state: String? = null

    @SerializedName("brightness")
    var brightness: Int? = 0

    @SerializedName("temp")
    var temp: Float? = 0f

    @SerializedName("hum")
    var hum: Int? = 0

    fun toDeviceModel() : Device{
        return Device().apply {
            this.macAddress = this@DevicePush.macAddress
            this.type = this@DevicePush.type
            this.state = this@DevicePush.state
            this.hum = this@DevicePush.hum
            this.temp = this@DevicePush.temp
            this.brightness = this@DevicePush.brightness
        }
    }
}

enum class ClientType(val value : String){
    APP_TYPE("app"),
    ESP_TYPE("esp"),
    SERVER_TYPE("server")
}

enum class ActionType(val value : String){
    CONNECT("paring"),
    CREATE("create"),
    UPDATE_INFO("info"),
    CHANGE_STATE("state"),
    LOCATION("location")
}