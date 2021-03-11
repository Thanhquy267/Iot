/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quyt.iot_demo

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

/**
 * Utility class for access to runtime permissions.
 */
object PermissionUtils {

    /**
     * Requests the fine location permission. If a rationale with an additional explanation should
     * be shown to the user, displays a dialog that triggers the request.
     */
    fun requestPermission(activity: AppCompatActivity, requestId: Int,
                          permission: String, finishActivity: Boolean) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//            // Display a dialog with rationale.
//            PermissionUtils.RationaleDialog.newInstance(requestId, finishActivity)
//                    .show(activity.supportFragmentManager, "dialog")
        } else {
            // Loaction permission has not been granted yet, request it.
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestId)

        }
    }

    /**
     * Checks if the result contains a [PackageManager.PERMISSION_GRANTED] result for a
     * permission from a runtime permissions request.
     *
     * @see androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
     */
    fun isPermissionGranted(grantPermissions: Array<String>, grantResults: IntArray,
                            permission: String): Boolean {
        for (i in grantPermissions.indices) {
            if (permission == grantPermissions[i]) {
                return grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        }
        return false
    }

    fun isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
                GlobalApplication.appContext,
                permission
        ) == PackageManager.PERMISSION_GRANTED
        return false
    }

    /**
     * A dialog that displays a permission denied message.
     */
    class PermissionDeniedDialog : DialogFragment() {

        private var mFinishActivity = false

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            mFinishActivity = requireArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY)

            return AlertDialog.Builder(activity)
                    .setMessage(R.string.location_permission_denied)
                    .setPositiveButton(android.R.string.ok, null)
                    .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (mFinishActivity) {
                Toast.makeText(activity, R.string.permission_required_toast,
                        Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }

        companion object {

            private val ARGUMENT_FINISH_ACTIVITY = "finish"

            /**
             * Creates a new instance of this dialog and optionally finishes the calling Activity
             * when the 'Ok' button is clicked.
             */
            fun newInstance(finishActivity: Boolean): PermissionDeniedDialog {
                val arguments = Bundle()
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)

                val dialog = PermissionDeniedDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }

    /**
     * A dialog that explains the use of the location permission and requests the necessary
     * permission.
     *
     *
     * The activity should implement
     * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback]
     * to handle permit or denial of this permission request.
     */
    class RationaleDialog : DialogFragment() {

        private var mFinishActivity = false

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val arguments = arguments
            val requestCode = requireArguments().getInt(ARGUMENT_PERMISSION_REQUEST_CODE)
            mFinishActivity = arguments?.getBoolean(ARGUMENT_FINISH_ACTIVITY)?:false

            return AlertDialog.Builder(activity)
                    .setMessage(R.string.permission_rationale_location)
                    .setPositiveButton(android.R.string.ok) { dialog, which ->
                        // After click on Ok, request the permission.
                        ActivityCompat.requestPermissions(requireActivity(),
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                requestCode)
                        // Do not finish the Activity while requesting permission.
                        mFinishActivity = false
                    }
                    .setNegativeButton(android.R.string.cancel, null)
                    .create()
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            if (mFinishActivity) {
                Toast.makeText(activity,
                        R.string.permission_required_toast,
                        Toast.LENGTH_SHORT)
                        .show()
                requireActivity().finish()
            }
        }

        companion object {

            private val ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode"

            private val ARGUMENT_FINISH_ACTIVITY = "finish"

            /**
             * Creates a new instance of a dialog displaying the rationale for the use of the location
             * permission.
             *
             *
             * The permission is requested after clicking 'ok'.
             *
             * @param requestCode    Id of the request that is used to request the permission. It is
             * returned to the
             * [androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback].
             * @param finishActivity Whether the calling Activity should be finished if the dialog is
             * cancelled.
             */
            fun newInstance(requestCode: Int, finishActivity: Boolean): RationaleDialog {
                val arguments = Bundle()
                arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode)
                arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity)
                val dialog = RationaleDialog()
                dialog.arguments = arguments
                return dialog
            }
        }
    }
}
