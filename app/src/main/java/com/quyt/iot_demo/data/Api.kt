package com.quyt.iot_demo.data

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.quyt.iot_demo.Constant
import com.quyt.iot_demo.GlobalApplication
import com.quyt.iot_demo.data.response.*
import com.quyt.iot_demo.model.Device
import com.quyt.iot_demo.model.Home
import com.quyt.iot_demo.model.Scenario
import com.quyt.iot_demo.utils.DialogUtils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.HttpException
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


object Api {

    val service: AppRepository by lazy {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
        val client = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request: Request = chain.request().newBuilder().addHeader("x-access-token", SharedPreferenceHelper.getInstance(GlobalApplication.appContext).currentUser?.token
                            ?: "").build()
                    chain.proceed(request)
                }
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

        @FormUrlEncoded
        @POST("/access/login")
        fun login(
                @Field("email") email: String? = null,
                @Field("password") password: String? = null
        ): Observable<LoginResponse>

        @GET("/home")
        fun getHome(): Observable<HomeResponse>

        @POST("/home/{homeId}/add-device")
        fun addDeviceToHome(
                @Path("homeId") homeId: Int,
                @Body device: Device
        ): Observable<Device>

        @PUT("/home/{homeId}/update-location")
        fun updateHomeLocation(
                @Path("homeId") homeId: Int,
                @Body home: Home
        ): Observable<Home>

        @POST("/scenario/create")
        fun createScenario(
                @Body context: Scenario
        ): Observable<CreateScenarioResponse>

        @GET("/scenario/{homeId}/{userId}")
        fun getScenario(
                @Path("homeId") homeId: Int,
                @Path("userId") userId: Int
        ): Observable<ScenarioResponse>

        @PUT("/scenario/{scenarioId}/update")
        fun updateScenario(
                @Path("scenarioId") scenarioId: Int,
                @Body scenario: Scenario
        ): Observable<ScenarioResponse>

        @GET("/device/{homeId}")
        fun getDevice(
                @Path("homeId") homeId: Int,
                @Query("type") type: String
        ): Observable<DeviceResponse>

    }

    fun <T> request(
            context: Context,
            request: Observable<T>,
            success: Consumer<T>,
            error: Consumer<Throwable>,
            showLoading: Boolean = true
    ) {
        val disposebag = CompositeDisposable()
        var loading: AlertDialog? = null
        if (showLoading) {
            loading = DialogUtils.loading(context)
        }
        disposebag.add(
                request.subscribeOn(Schedulers.io()) //(*)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ result ->
                            success.accept(result)
                            if (showLoading) {
                                loading?.dismiss()
                            }
                        }, { e ->
                            try {
                                if (showLoading) {
                                    loading?.dismiss()
                                }
                                val errorRes = Gson().fromJson(
                                        (e as? HttpException)?.response()?.errorBody()?.string(),
                                        BaseResponse::class.java
                                )
                                if (!errorRes.errorMessage.isNullOrEmpty()) {
                                    Toast.makeText(context, errorRes.errorMessage, Toast.LENGTH_LONG).show()
                                }
                                error.accept(e)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
        )
    }
}