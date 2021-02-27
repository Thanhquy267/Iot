package com.quyt.iot_demo

import android.app.Application
import android.content.Context

class GlobalApplication : Application(){

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object{
        lateinit var appContext: Context
            private set
    }
}