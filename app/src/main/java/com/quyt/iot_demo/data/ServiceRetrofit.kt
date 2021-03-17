package com.quyt.iot_demo.data

import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.model.response.DeviceResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit


interface ServiceRetrofit {

    @GET("/device")
    fun getDevices(
    ): Call<DeviceResponse>

}

class SetupConnectToServer {
    fun setupConnect(): ServiceRetrofit {

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val okHttpClient = OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .addInterceptor(logging)
                .build()


        val builder = Retrofit.Builder()
                .baseUrl("${Constant.API_HOST}")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()

        return retrofit.create(ServiceRetrofit::class.java)
    }

    private var interceptor: Interceptor = Interceptor { chain ->
        val newRequest: Request = chain.request().newBuilder()
                .method(chain.request().method(), chain.request().body())
                .build()

        chain.proceed(newRequest)
    }

}