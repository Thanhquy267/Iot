package com.quyt.iot_demo.data.response

import com.google.gson.annotations.SerializedName
import com.quyt.iot_demo.model.Device

class DeviceResponse : BaseResponse() {
    @SerializedName("data")
    var data: ArrayList<Device>? = null
}