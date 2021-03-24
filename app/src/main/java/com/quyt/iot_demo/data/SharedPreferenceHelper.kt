package com.quyt.iot_demo.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quyt.iot_demo.model.Device
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

    var userId: String?
        get() = preference.getString(USER_ID, "")
        set(value) = preference.edit().putString(USER_ID, value).apply()

    var address: String?
        get() = preference.getString(ADDRESS, "")
        set(value) = preference.edit().putString(ADDRESS, value).apply()

    var latlng: LatLng?
        get() {
            val json = preference.getString(LAT_LNG, "")
            return gson.fromJson(json, LatLng::class.java)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(context, LAT_LNG, Gson().toJson(value))
        }

    var listDevice: ArrayList<Device>?
        get() {
            val json = preference.getString(DEVICE, "")
            return gson.fromJson(json, object : TypeToken<ArrayList<Device>>() {}.type)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(
                context,
                DEVICE,
                Gson().toJson(value, object : TypeToken<ArrayList<Device>>() {}.type)
            )
        }


    var turnOnTimer: Long
        get() = preference.getLong(TURN_ON_TIMER, 0)
        set(value) = preference.edit().putLong(TURN_ON_TIMER, value).apply()

    var turnOffTimer: Long
        get() = preference.getLong(TURN_OFF_TIMER, 0)
        set(value) = preference.edit().putLong(TURN_OFF_TIMER, value).apply()

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