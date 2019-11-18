package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


/** Modified from:
 * https://medium.com/@martijn.van.welie/making-android-ble-work-part-1-a736dcd53b02
 */

/** This is the main activity
 *  The program will begin by searching for devices, since the device will be unpaired after
 *  previous session
 *
 *  When a device is selected, the main activity will end and the next activity,
 *  ReadDevice, will begin
 */

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    HashSet<BluetoothDevice> devices = new HashSet<>();
    ArrayList<String> deviceNames = new ArrayList<>();

    BluetoothDevice chosenDevice;

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    private int REQUEST_ENABLE_BT = 1;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //If bluetooth off, ask to turn on
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "No LE Support.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        //Scan for device
        if (scanner != null) {
            scanner.startScan(scanCallback);
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ListView simpleList = (ListView)findViewById(R.id.devices);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                this, R.layout.activity_listview, R.id.textView, deviceNames);
        simpleList.setAdapter(adapter);
        simpleList.setClickable(true);
        simpleList.setTextFilterEnabled(true);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
            String deviceName=(String) simpleList.getItemAtPosition(position);

            for (BluetoothDevice device : devices) {
                if (device.getName().equals(deviceName))
                {
                    chosenDevice = device;
                }
            }

            goToReadDevice(chosenDevice);
            }

        });
    }

    protected void goToReadDevice(BluetoothDevice device) {
        Intent intent = new Intent(this, ReadDevice.class);
        //Not sure which extras are needed
        intent.putExtra("deviceName", device.getName());
        intent.putExtra("deviceAddr", device.getAddress());
        intent.putExtra("deviceType", device.getType());
        intent.putExtra("deviceUuids", device.getUuids());
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            //Will need to filter based on UUID (maybe also name and RSSI value)
            //TODO: Should not have empty catch - find better way to implement
            //TODO: Shouldn't be getting dupes because of ArrayList but we are - fio
            //TODO: Add loading bar - not urgent but would look nice
            try {
                if (!device.getName().equals("null")) {
                    devices.add(device);
                }
            } catch (Exception e){}

            //TODO: Figure out why size not always set at 10 - not actually stopping or is at least restarting the scan
            //TODO: Change this in case 5 devices can't be found - don't want to get NPE
            if (devices.size() == 5) {
                scanner.stopScan(scanCallback);
                for (BluetoothDevice dev : devices) {
                    deviceNames.add(dev.getName());
                }
                //TODO: Create new method - don't use onResume - likely why there are dupes
                onResume();
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            // Ignore for now
        }

        @Override
        public void onScanFailed(int errorCode) {
            // Ignore for now
        }

    };
}