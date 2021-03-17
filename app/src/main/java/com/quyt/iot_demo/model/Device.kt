package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Device {

    @SerializedName("macAddress")
    var macAddress : String? = null

    @SerializedName("name")
    var name : String? = null

    @SerializedName("userId")
    var userId : Int? = null

    @SerializedName("state")
    var state : String? = null

}