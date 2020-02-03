package com.example.elec490;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.jjoe64.graphview.series.DataPoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

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

    private ProgressBar spinner;

    HashSet<BluetoothDevice> devices = new HashSet<>();
    ArrayList<String> deviceNames = new ArrayList<>();

    BluetoothDevice chosenDevice;

    private int REQUEST_ENABLE_BT = 1;

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothLeScanner scanner = adapter.getBluetoothLeScanner();

    //Create empty graph dataset
    ArrayList<DataPoint> dataPoints = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, "The permission to get BLE location data is required", Toast.LENGTH_SHORT).show();
            }else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }else{
            Toast.makeText(this, "Location permissions already granted", Toast.LENGTH_SHORT).show();
        }

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

        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setVisibility(View.VISIBLE);

        //Scan for device
        if (scanner != null) {
            scanner.startScan(scanCallback);
            Log.d(TAG, "scan started");
        }  else {
            Log.e(TAG, "could not get scanner object");
        }

        final Button rescan = findViewById(R.id.rescan);
        rescan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
                //TODO: Still has animation - get rid of it
                Intent intent = getIntent().addFlags(FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void goToReadDevice(BluetoothDevice device) {
        Intent intent = new Intent(this, ReadDevice.class);
        //Not sure which extras are needed
        intent.putExtra("deviceName", device.getName());
        intent.putExtra("deviceAddr", device.getAddress());
        intent.putExtra("sensorVal","Reading...");
        intent.putExtra("count", 0);
        Bundle args = new Bundle();
        args.putSerializable("dataPoints", (Serializable) dataPoints);
        intent.putExtra("BUNDLE", args);
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
            try {
                //Look for specified device using set naming convention (e.g. .contains("CS-"))
                //TODO: Still getting dupes - try to fio
                if (device.getName().contains("CS")) {
                    setProgressBarIndeterminateVisibility(false);
                    spinner.setVisibility(View.GONE);
                    devices.add(device);
                    deviceNames.add(device.getName());
                    scanner.stopScan(scanCallback);
                    listDevices();
                }
            } catch (Exception e){
                Log.d(TAG, "Device name is null");
            }
        }
    };

    public void listDevices() {
        final ListView simpleList = (ListView)findViewById(R.id.devices);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                this, R.layout.activity_listview, R.id.textView, deviceNames);
        simpleList.setAdapter(adapter);
        simpleList.setClickable(true);
        simpleList.setTextFilterEnabled(true);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick (AdapterView < ? > arg0, View view,int position, long id){
            String deviceName = (String) simpleList.getItemAtPosition(position);

            for (BluetoothDevice device : devices) {
                if (device.getName().equals(deviceName)) {
                    chosenDevice = device;
                }
            }
            goToReadDevice(chosenDevice);
            }
        });
    }
}