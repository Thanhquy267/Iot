package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Camera {

    @SerializedName("name")
    var name: String? = null

    @SerializedName("ip")
    var ip: String? = null

    @SerializedName("port")
    var port: String? = null

    @SerializedName("user")
    var user: String? = null

    @SerializedName("pw")
    var pw: String? = null
}