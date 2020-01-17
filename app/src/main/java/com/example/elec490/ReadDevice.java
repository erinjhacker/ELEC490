package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;

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
    BluetoothGattCharacteristic characteristic;
    boolean enabled;
    int connectionState = BluetoothProfile.STATE_DISCONNECTED;

    private static final String TAG = "ReadDevice";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    //private static final String serviceUUID = "00001809-0000-1000-8000-00805f9b34fb";
    private static final UUID serviceUUID = UUID.nameUUIDFromBytes("1809".getBytes());
    UUID charUUID = UUID.nameUUIDFromBytes("2A1C".getBytes());


    byte[] value;
    String stringVal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

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
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    connectionState = STATE_CONNECTED;
                    Log.d(TAG, "YOU FUCKING DID IT!!!!!!!!!!");
                    gatt.discoverServices();
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    connectionState = STATE_DISCONNECTED;
                    gatt.close();
                    goBackToScan();
                }
            }

            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == 129) {
                    Log.e(TAG, "Service discovery failed");
                    gatt.disconnect();
                }
                else {
                    final List<BluetoothGattService> services = gatt.getServices();
                    Log.i(TAG, String.format(Locale.ENGLISH, "discovered %d services", services.size()));
                    characteristic = new BluetoothGattCharacteristic(serviceUUID,
                            BluetoothGattCharacteristic.PERMISSION_WRITE | BluetoothGattCharacteristic.PERMISSION_READ,
                            BluetoothGattCharacteristic.PROPERTY_WRITE | BluetoothGattCharacteristic.PROPERTY_READ);

                    // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
                    int properties = characteristic.getProperties();
                    if ((properties & PROPERTY_NOTIFY) > 0) {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                        Log.d(TAG, "fuck");
                    } else {
                        Log.e(TAG, String.format("ERROR: Characteristic %s does not have notify or indicate property", characteristic.getUuid()));
                    }
                    //Set notification
                    if (value.length > 0) {
                        gatt.setCharacteristicNotification(characteristic, true);
                    }

                    Log.d(TAG, "fuck me");
                }
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    byte[] reading = characteristic.getValue();
                    displayVal(stringVal);
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

    public void goBackToScan() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCharacteristicRead(BluetoothGatt gatt,
                                     BluetoothGattCharacteristic characteristic,
                                     int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            byte[] reading = characteristic.getValue();
            displayVal(stringVal);
        }
    }

    public void onCharacteristicChanged(BluetoothGatt gatt,
                                        BluetoothGattCharacteristic characteristic) {
        byte[] reading = characteristic.getValue();
        displayVal(stringVal);
    }

    public void displayVal(String sensorReading) {
        TextView setDeviceReading = (TextView) findViewById(R.id.deviceReading);
        setDeviceReading.setText(sensorReading);
    }
}