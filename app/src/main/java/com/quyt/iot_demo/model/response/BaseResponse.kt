package com.quyt.iot_demo.model.response

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("name")
    var name: String? = null

    @SerializedName("code")
    var code : Int = 0

    @SerializedName("message")
    var message : String? = null
}