package com.somcrea.smartads.ble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Ruben.
 */

public class BluetoothMonitoringService extends Service {

    private static final String TAG = "estimote";

    //region EVENTS
    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onCreate() {
        // Configure BeaconManager.
        Log.d(TAG, "Bluetooth changed");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Bluetooth destroyed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Bluetooth monitoring");

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }
    //endregion

}
