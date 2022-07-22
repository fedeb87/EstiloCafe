package com.federicoberon.estilocafe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String LOG_TAG = NetworkChangeReceiver.class.getSimpleName();
    private boolean isConnected = false;
    private final View view;

    public NetworkChangeReceiver(View view) {
        this.view = view;
    }

    public NetworkChangeReceiver() {
        this.view = null;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {

        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            if (view == null) {
                if (!isNetworkAvailable(context))

                    Toast.makeText(context, "Sin conexion internet. Algunas funciones podrian no estar disponibles", Toast.LENGTH_LONG).show();
            } else {
                if (isNetworkAvailable(context)) view.setVisibility(View.GONE);
                else view.setVisibility(View.VISIBLE);
            }
        }
    }


    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if(!isConnected){
                            Log.v(LOG_TAG, "------- Now you are connected to Internet!");
                            isConnected = true;
                        }
                        return true;
                    }
                }
            }
        }
        Log.v(LOG_TAG, "------- You are not connected to Internet!");

        isConnected = false;
        return false;
    }
}
