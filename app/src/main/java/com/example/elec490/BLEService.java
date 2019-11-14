package com.example.elec490;

import android.app.Service;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;

import java.util.List;

/**
 * Adapted from https://microchipdeveloper.com/wireless:ble-android-service
 * Currently not used, may be needed in the future?
 */

public class BLEService {//extends Service
    /* @Override
    protected void onStart() {
        super.onStart();

        Intent gattServiceIntent = new Intent(this, BLEService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBleService != null) {
            if (mBleService.serviceStatus) {
                unbindService(mServiceConnection);
            }
            mBleService.close();
            mBleService = null;
        }
    }

    void startScan(List<ScanFilter> filters, ScanSettings settings, ScanCallback callback) {

    } */
}
