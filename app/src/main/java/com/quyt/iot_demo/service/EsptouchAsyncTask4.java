package com.quyt.iot_demo.service;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.quyt.iot_demo.R;
import com.quyt.iot_demo.esptouch.EsptouchTask;
import com.quyt.iot_demo.esptouch.IEsptouchResult;
import com.quyt.iot_demo.esptouch.IEsptouchTask;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class EsptouchAsyncTask4 extends AsyncTask<byte[], IEsptouchResult, List<IEsptouchResult>> implements OnResult{
    private WeakReference<AppCompatActivity> mActivity;

    private final Object mLock = new Object();
    ProgressDialog mProgressDialog;
    private AlertDialog mResultDialog;
    private IEsptouchTask mEsptouchTask;
    private EspTouchListener mListener;

    public EsptouchAsyncTask4(AppCompatActivity activity,EspTouchListener listener) {
        mActivity = new WeakReference<>(activity);
        mListener = listener;
    }

    void cancelEsptouch() {
        cancel(true);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (mResultDialog != null) {
            mResultDialog.dismiss();
        }
        if (mEsptouchTask != null) {
            mEsptouchTask.interrupt();
        }
    }

    @Override
    protected void onPreExecute() {
        Activity activity = mActivity.get();
        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setMessage(activity.getString(R.string.connecting_device_message));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setOnCancelListener(dialog -> {
            synchronized (mLock) {
                if (mEsptouchTask != null) {
                    mEsptouchTask.interrupt();
                }
            }
        });
        mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, activity.getText(android.R.string.cancel),
                (dialog, which) -> {
                    synchronized (mLock) {
                        if (mEsptouchTask != null) {
                            mEsptouchTask.interrupt();
                        }
                    }
                });
        mProgressDialog.show();
    }

    @Override
    protected void onProgressUpdate(IEsptouchResult... values) {
        Context context = mActivity.get();
        if (context != null) {
            IEsptouchResult result = values[0];
            Log.i(TAG, "EspTouchResult: " + result);
//            String text = result.getBssid() + " is connected to the wifi";
//            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected List<IEsptouchResult> doInBackground(byte[]... params) {
        AppCompatActivity activity = mActivity.get();
        int taskResultCount;
        synchronized (mLock) {
            byte[] apSsid = params[0];
            byte[] apBssid = params[1];
            byte[] apPassword = params[2];
            byte[] deviceCountData = params[3];
            byte[] broadcastData = params[4];
            taskResultCount = deviceCountData.length == 0 ? -1 : Integer.parseInt(new String(deviceCountData));
            Context context = activity.getApplicationContext();
            mEsptouchTask = new EsptouchTask(apSsid, apBssid, apPassword, context);
            mEsptouchTask.setPackageBroadcast(broadcastData[0] == 1);
            mEsptouchTask.setEsptouchListener(this::publishProgress);
        }
        return mEsptouchTask.executeForResults(taskResultCount);
    }

    @Override
    protected void onPostExecute(List<IEsptouchResult> result) {
        AppCompatActivity activity = mActivity.get();
//        activity.mTask = null;
//        mProgressDialog.dismiss();
        if (result == null) {
            mResultDialog = new AlertDialog.Builder(activity)
                    .setMessage(R.string.esptouch1_configure_result_failed_port)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            mResultDialog.setCanceledOnTouchOutside(false);
            return;
        }

        // check whether the task is cancelled and no results received
        IEsptouchResult firstResult = result.get(0);
        if (firstResult.isCancelled()) {
            return;
        }
        // the task received some results including cancelled while
        // executing before receiving enough results

        if (!firstResult.isSuc()) {
            mResultDialog = new AlertDialog.Builder(activity)
                    .setMessage(R.string.esptouch1_configure_result_failed)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            mResultDialog.setCanceledOnTouchOutside(false);
            return;
        }

        ArrayList<CharSequence> resultMsgList = new ArrayList<>(result.size());
        String ip = "";
        for (IEsptouchResult touchResult : result) {
            String message = activity.getString(R.string.esptouch1_configure_result_success_item,
                    touchResult.getBssid(), touchResult.getInetAddress().getHostAddress());
            resultMsgList.add(message);
            ip = touchResult.getInetAddress().getHostAddress();
        }
        new Thread(new ClientSendAndListen(ip, this)).start();
    }

    @Override
    public void onResult(@NotNull String macId) {
          Log.d("EspMacID",macId);
          mListener.onPostExecute(macId,mProgressDialog);
    }
}

