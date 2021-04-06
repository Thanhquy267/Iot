package com.quyt.iot_demo.model

import com.google.gson.annotations.SerializedName

class User {
    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("createdAt")
    var createdAt: String? = null

    @SerializedName("updatedAt")
    var updatedAt: String? = null

    @SerializedName("token")
    var token: String? = null

}