package com.quyt.iot_demo.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson

/**
 * this is manager class that support manage settings of app.
 * @author Sang
 */
class SharedPreferenceHelper private constructor(private val context: Context,private val gson : Gson) {
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

    var address : String?
        get() = preference.getString(ADDRESS, "")
        set(value) = preference.edit().putString(ADDRESS, value).apply()

    var latlng : LatLng?
        get() {
            val json = preference.getString(LAT_LNG, "")
            return gson.fromJson(json, LatLng::class.java)
        }
        set(value) {
            PreferenceUtils.saveToPrefs(context, LAT_LNG, Gson().toJson(value))
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