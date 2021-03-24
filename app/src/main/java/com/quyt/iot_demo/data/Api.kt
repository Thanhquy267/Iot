package com.quyt.iot_demo.data

import android.content.Context
import com.google.gson.GsonBuilder
import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.data.response.DeviceResponse
import com.quyt.iot_demo.utils.DialogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.function.Consumer


object Api {

    val service: AppRepository by lazy {
        val logging = HttpLoggingInterceptor()
        val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl(Constant.API_HOST)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create()) //(*)
            .client(client)
            .build().create<AppRepository>(AppRepository::class.java)
    }

    interface AppRepository {

        @GET("/device")
        fun getDevices(): Observable<DeviceResponse>
    }

    fun <T> request(request : Observable<T>,context : Context,success : Consumer<T>,error : Consumer<Throwable>){
        val disposebag = CompositeDisposable()
        val loading = DialogUtils.loading(context)
        disposebag.add(request
            .subscribeOn(Schedulers.io()) //(*)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                success.accept(result)
                loading.dismiss()
            }, { e ->
                error.accept(e)
                loading.dismiss()
            }))
    }
}