package com.quyt.iot_demo.data.request

import com.google.gson.annotations.SerializedName

class LoginRequest {

    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null
}