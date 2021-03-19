package com.quyt.iot_demo.service;

import android.app.ProgressDialog;

public interface EspTouchListener{
    void onPostExecute(String macID, ProgressDialog progressDialog);
}
