package com.somcrea.smartads.ble;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ruben.
 */

public class BluetoothStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {
                    context.stopService(new Intent(context, BeaconsMonitoringService.class));
                } else {
                    context.startService(new Intent(context, BeaconsMonitoringService.class));
                }
            }
        }
        catch (Exception e){e.printStackTrace();}
    }
}
