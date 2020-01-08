package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

/** Modified from:
 * https://medium.com/@martijn.van.welie/making-android-ble-work-part-2-47a3cdaade07
 *
 * and
 *
 * https://medium.com/@avigezerit/bluetooth-low-energy-on-android-22bc7310387a
 *
 * and
 *
 * https://developer.android.com/guide/topics/connectivity/bluetooth-le#java
 */

/** In this class, the app will connect to the selected device and constantly read data
 *  The program will begin by displaying the name of the selected device then connecting to it
 *
 *  When a device is connected, the sensor will be read and the value will be displayed
 *  Unsure for now if we will need to decode here or on IC microprocessor
 */

public class ReadDevice extends AppCompatActivity {

    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String bluetoothDeviceAddress;
    private BluetoothGatt bluetoothGatt;
    int connectionState = BluetoothProfile.STATE_DISCONNECTED;

    private static final String TAG = "ReadDevice";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Set name of connected device
        String deviceName = getIntent().getExtras().getString("deviceName");
        TextView setDeviceName = (TextView) findViewById(R.id.deviceName);
        setDeviceName.setText(deviceName);

        TextView setDeviceReading = (TextView) findViewById(R.id.deviceReading);
        setDeviceReading.setText("0");

        final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                String intentAction;
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    intentAction = ACTION_GATT_CONNECTED;
                    connectionState = STATE_CONNECTED;
                    Log.d(TAG, "YOU FUCKING DID IT!!!!!!!!!!");
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    intentAction = ACTION_GATT_DISCONNECTED;
                    connectionState = STATE_DISCONNECTED;
                    gatt.close();
                    goBackToScan();
                }
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    //Show it in the app??
                }
            }
        };

        //Connect to the device
        String deviceAddr = getIntent().getExtras().getString("deviceAddr");
        Log.d(TAG, deviceAddr);
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddr);
        BluetoothGatt gatt = device.connectGatt(this, false, gattCallback, TRANSPORT_LE);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()== android.R.id.home) {
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goBackToScan() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
