package com.quyt.iot_demo.data.response

import com.google.gson.annotations.SerializedName
import com.quyt.iot_demo.model.Scenario

class CreateScenarioResponse : BaseResponse() {
    @SerializedName("data")
    var data: Scenario? = null
}