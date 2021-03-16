package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Device {

    @SerializedName("id")
    var id : String? = null

    @SerializedName("name")
    var name : String? = null

    @SerializedName("state")
    var state : String? = null

}