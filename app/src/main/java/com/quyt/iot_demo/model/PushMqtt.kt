package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class PushMqtt {

    @SerializedName("clientType")
    var clientType : String? = null

    @SerializedName("actionType")
    var actionType : String? = null

    @SerializedName("data")
    var data : Device? = null

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