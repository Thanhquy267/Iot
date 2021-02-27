package com.quyt.iot_demo.data

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import java.lang.ref.WeakReference

object PreferenceUtils {

    /**
     * Called to save supplied value in shared preferences against given key.
     *
     * @param context Context of caller activity
     * @param key     Key of value to save against
     * @param value   Value to save
     */
    fun saveToPrefs(context: Context, key: String, value: Any) {
        val contextWeakReference = WeakReference(context)
        try {
            if (contextWeakReference.get() != null) {
                val prefs = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get())
                val editor = prefs.edit()
                when (value) {
                    is Int -> editor.putInt(key, value)
                    is String -> editor.putString(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                    is Long -> editor.putLong(key, value)
                    is Float -> editor.putFloat(key, value)
                    is Double -> editor.putLong(key, java.lang.Double.doubleToRawLongBits(value))
                }
                editor.apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * Called to retrieve required value from shared preferences, identified by given key.
     * Default value will be returned of no value found or error occurred.
     *
     * @param context      Context of caller activity
     * @param key          Key to find value against
     * @param defaultValue Value to return if no data found against given key
     * @return Return the value found against given key, default if not found or any error occurs
     */
    fun getFromPrefs(context: Context, key: String, defaultValue: Any): Any? {
        val contextWeakReference = WeakReference(context)
        if (contextWeakReference.get() != null) {
            val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get())
            try {
                if (defaultValue is String) {
                    return sharedPrefs.getString(key, defaultValue.toString())
                } else if (defaultValue is Int) {
                    return sharedPrefs.getInt(key, defaultValue)
                } else if (defaultValue is Boolean) {
                    return sharedPrefs.getBoolean(key, defaultValue)
                } else if (defaultValue is Long) {
                    return sharedPrefs.getLong(key, defaultValue)
                } else if (defaultValue is Float) {
                    return sharedPrefs.getFloat(key, defaultValue)
                } else if (defaultValue is Double) {
                    return java.lang.Double.longBitsToDouble(
                        sharedPrefs.getLong(
                            key,
                            java.lang.Double.doubleToLongBits(defaultValue)
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("Execption", e.message.toString())
                return defaultValue
            }

        }
        return defaultValue
    }

    /**
     * @param context Context of caller activity
     * @param key     Key to delete from SharedPreferences
     */
    fun removeFromPrefs(context: Context, key: String) {
        val contextWeakReference = WeakReference(context)
        if (contextWeakReference.get() != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get())
            val editor = prefs.edit()
            editor.remove(key)
            editor.apply()
        }
    }

    fun hasKey(context: Context, key: String): Boolean {
        val contextWeakReference = WeakReference(context)
        if (contextWeakReference.get() != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(contextWeakReference.get())
            return prefs.contains(key)
        }
        return false
    }
}