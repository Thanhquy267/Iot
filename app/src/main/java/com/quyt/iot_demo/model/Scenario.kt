package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class Scenario {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("homeId")
    var homeId: Int = 0

    @SerializedName("userId")
    var userId: Int = 0

    @SerializedName("isActivate")
    var isActivate: Boolean = true

    @SerializedName("actions")
    var actions: ArrayList<Device>? = null

    @SerializedName("conditions")
    var conditions: ArrayList<Condition>? = null
}