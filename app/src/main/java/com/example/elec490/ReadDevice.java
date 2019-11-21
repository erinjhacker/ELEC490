package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCallback;
import android.os.Bundle;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.TimerTask;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/** Modified from:
 * https://medium.com/@martijn.van.welie/making-android-ble-work-part-2-47a3cdaade07
 *
 * and
 *
 * https://medium.com/@avigezerit/bluetooth-low-energy-on-android-22bc7310387a
 */

/** In this class, the app will connect to the selected device and constantly read data
 *  The program will begin by displaying the name of the selected device then connecting to it
 *
 *  When a device is connected, the sensor will be read and the value will be displayed
 *  Unsure for now if we will need to decode here or on IC microprocessor
 */

public class ReadDevice extends AppCompatActivity {

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

    int connectionState = BluetoothProfile.STATE_DISCONNECTED;
    private static final String TAG = "ReadDevice";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        //Set name of connected device
        String deviceName = getIntent().getExtras().getString("deviceName");
        TextView setDeviceName = (TextView)findViewById(R.id.deviceName);
        setDeviceName.setText(deviceName);

        //Connect to the device
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothGattCallback gattCallBack = new BluetoothGattCallback() {
            @Override
            public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                super.onPhyUpdate(gatt, txPhy, rxPhy, status);
            }
        };
        String deviceAddr = getIntent().getExtras().getString("deviceAddr");
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddr);
        BluetoothGatt gatt = device.connectGatt(this, false, gattCallBack, TRANSPORT_LE);
        try {
            waitUntilConnected();
        } catch (InterruptedException e) {
            Log.d(TAG, "Failure to connect to BLE device");
        }
        //TODO: Not reaching this line - fio
        Log.d(TAG, "Connected to device");

        //set initial device reading


        //Continuously read and update sensor reading


    }

    private synchronized void waitUntilConnected() throws InterruptedException {
        while (connectionState != BluetoothProfile.STATE_CONNECTED) {
            wait();
        }
    }
}
