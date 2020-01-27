package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class UpdateDeviceReading extends AppCompatActivity {

    private final static String TAG = "READ";

    String sensorVal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mbluetoothAdapter = manager.getAdapter();

        String deviceAddr = getIntent().getExtras().getString("deviceAddr");

        BluetoothDevice device = mbluetoothAdapter.getRemoteDevice(deviceAddr);

        String deviceName = getIntent().getExtras().getString("deviceName");
        TextView setDeviceName = (TextView) findViewById(R.id.deviceName);
        setDeviceName.setText(deviceName);

        sensorVal = getIntent().getExtras().getString("sensorVal");
        TextView setDeviceReading = (TextView) findViewById(R.id.deviceReading);
        setDeviceReading.setText(sensorVal);

        goBackToRead(device);
    }

    public void goBackToRead(BluetoothDevice device) {
        //Update in new activity
        Intent intent = new Intent(this, ReadDevice.class);
        //Not sure which extras are needed
        intent.putExtra("deviceAddr", device.getAddress());
        intent.putExtra("deviceName", device.getName());
        intent.putExtra("sensorVal", sensorVal);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}
