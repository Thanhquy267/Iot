package com.quyt.iot_demo.data.response

import com.google.gson.annotations.SerializedName
import com.quyt.iot_demo.model.Home

class HomeResponse {
    @SerializedName("data")
    var data: ArrayList<Home>? = null
}