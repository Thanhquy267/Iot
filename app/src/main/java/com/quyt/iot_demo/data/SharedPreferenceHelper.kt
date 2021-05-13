package com.quyt.iot_demo.data

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.quyt.iot_demo.model.Camera
import com.quyt.iot_demo.model.Home
import com.quyt.iot_demo.model.User

/**
 * this is manager class that support manage settings of app.
 * @author Sang
 */
class SharedPreferenceHelper private constructor(
        private val context: Context,
        private val gson: Gson
) {
    /**
     * get current doing preference
     * @return
     */
    private val preference: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)
//    private val gson: Gson? = null

    //    var selectedAvatarBitmapPath: String
//        get() = preference.getString(AVATAR_PATH, "")
//        set(value) = preference.edit().putString(AVATAR_PATH, value).apply()
//
//    var userInfo: UserModel?
//        get() {
//            val json = preference.getString(USER_INFO, "")
//            var user: UserModel? = gson?.fromJson(json, UserModel::class.java)
//            if (user != null) user = UserModel()
//            return user
//        }
//        set(userValue) {
//            PreferenceUtils.saveToPrefs(context, USER_INFO, Gson().toJson(userValue))
//        }


    var isLogging: Boolean?
        get() = preference.getBoolean(ISLOGGING, false)
        set(value) = preference.edit().putBoolean(ISLOGGING, value ?: false).apply()

    var currentUser: User?
        get() {
            val json = preference.getString(USER, "")
            return gson.fromJson(json, User::class.java)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(context, USER, Gson().toJson(value))
        }

    var currentHome: Home?
        get() {
            val json = preference.getString(HOME, "")
            return gson.fromJson(json, Home::class.java)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(context, HOME, Gson().toJson(value))
        }

    var currentLocation: Location?
        get() {
            val str = PreferenceUtils.getFromPrefs(context, USER_LOCATION_KEY, "User Location,0,0") as String
            val arr = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            if (arr.size < 3) return null
            val location = Location(arr[0])
            location.latitude = java.lang.Double.parseDouble(arr[1])
            location.longitude = java.lang.Double.parseDouble(arr[2])
            return location
        }
        set(value) {
            val lat = value?.latitude
            val lng = value?.longitude
            val provider = value?.provider
            val str = "$provider,$lat,$lng"
            PreferenceUtils.saveToPrefs(context, USER_LOCATION_KEY, str)
        }

    var camera: Camera?
        get() {
            val json = preference.getString(CAMERA, "")
            return gson.fromJson(json, Camera::class.java)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(context, CAMERA, Gson().toJson(value))
        }

    /**
     * support get value from key
     * @return
     */
    /**
     * support track custom data
     * @return
     */

    companion object {
        private var mInstance: SharedPreferenceHelper? = null
        private const val TURN_ON_TIMER = "TURN_ON_TIMER"
        private const val TURN_OFF_TIMER = "TURN_OFF_TIMER"
        private const val ADDRESS = "ADDRESS"
        private const val LAT_LNG = "LAT_LNG"
        private const val USER_ID = "USER_ID"
        private const val DEVICE = "DEVICE"
        private const val USER = "USER"
        private const val ISLOGGING = "ISLOGGING"
        private const val HOME = "HOME"
        private const val CAMERA = "CAMERA"
        private const val USER_LOCATION_KEY = "USER_LOCATION_KEY"

        /**
         * get settings instance
         * @param context
         * @return
         */
        fun getInstance(context: Context): SharedPreferenceHelper {
            if (mInstance == null) {
                mInstance = SharedPreferenceHelper(context, Gson())
            }
            return mInstance as SharedPreferenceHelper
        }
    }

}