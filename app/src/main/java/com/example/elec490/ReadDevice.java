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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;
import static android.bluetooth.BluetoothDevice.BOND_BONDING;
import static android.bluetooth.BluetoothDevice.BOND_NONE;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGatt.GATT_INSUFFICIENT_AUTHENTICATION;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

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

    BluetoothGattCharacteristic characteristic;
    int connectionState = BluetoothProfile.STATE_DISCONNECTED;

    private static final String TAG = "ReadDevice";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;

    private int flag = 0;

    //Arduino ids
//    private static final UUID serviceUUID = UUID.fromString("19B10000-E8F2-537E-4F6C-D104768A1214");
//    private static final UUID charUUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214");
//    private static final UUID configUUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");
//    private static final UUID configUUID = UUID.fromString("19B12902-E8F2-537E-4F6C-D104768A1214");

    //BGM ids for temperature sensor
    private static final UUID serviceUUID = UUID.fromString("00001809-0000-1000-8000-00805F9B34FB");
    private static final UUID charUUID = UUID.fromString("00002A1C-0000-1000-8000-00805F9B34FB");
    private static final UUID configUUID = UUID.fromString("00002902-0000-1000-8000-00805F9B34FB");

    //BGM ids for custom service (our sensor)


    byte[] value;

    BluetoothDevice device;

    String deviceName;

    String sensorVal;

    ArrayList<DataPoint> dataPoints;

    int count;

    boolean error = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_read);

        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mbluetoothAdapter = manager.getAdapter();

        //Set name of connected device
        deviceName = getIntent().getExtras().getString("deviceName");
        TextView setDeviceName = (TextView) findViewById(R.id.deviceName);
        setDeviceName.setText(deviceName);

        sensorVal = getIntent().getExtras().getString("sensorVal");
        TextView setDeviceReading = (TextView) findViewById(R.id.deviceReading);
        setDeviceReading.setText(sensorVal);

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        dataPoints = (ArrayList<DataPoint>) args.getSerializable("dataPoints");

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        count = getIntent().getExtras().getInt("count");

        //Create graph
        if (dataPoints.size() > 0) {
            try {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (DataPoint dataPoint : dataPoints) {
                    series.appendData(dataPoint, true, dataPoints.size());
                }
                graph.addSeries(series);
            } catch (IllegalArgumentException e) {
                Toast.makeText(ReadDevice.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
                if ((newState == BluetoothProfile.STATE_CONNECTED) && (status == GATT_SUCCESS)) {
                    connectionState = STATE_CONNECTED;
                    Log.d(TAG, "YOU FUCKING DID IT!!!!!!!!!!");
                    int bondstate = device.getBondState();
                    if (bondstate == BOND_BONDED || bondstate == BOND_NONE) {
                        Log.d(TAG, "Bonded");
                        gatt.discoverServices();
                    } else if (bondstate == BOND_BONDING) {
                        // Bonding process has already started let it complete
                        Log.i(TAG, "waiting for bonding to complete");
                    }
                } else if (!error) {
                    error = true;
                    if ((newState == BluetoothProfile.STATE_DISCONNECTED) && (status == BluetoothGatt.GATT)) {
                        connectionState = STATE_DISCONNECTED;
                        Log.e(TAG, "Disconnected");
                        finish();
                        gatt.disconnect();
                        gatt.close();
                        return;
                    } else {
                        Log.e(TAG, "GATT Error");
                        finish();
                        gatt.disconnect();
                        gatt.close();
                        //goBackToScan();
                        return;
                    }
                }
            }

            //TODO: Figure out why main is called back several times
            @Override
            // New services discovered
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == 133) {
                    finish();
                    gatt.disconnect();
                    gatt.close();
                    return;
                }
                if (status == 129) {
                    Log.e(TAG, "Service discovery failed");
                    finish();
                    gatt.disconnect();
                    gatt.close();
                    return;
                }
                else {
                    //check that service has desired characteristic and set charactertistic if true
                    final List<BluetoothGattService> services = gatt.getServices();
                    Log.i(TAG, String.format(Locale.ENGLISH, "discovered %d services", services.size()));
                    for (BluetoothGattService service : services) {
                        UUID serviceUuid = serviceUUID;
                        if (service.getUuid().equals(serviceUUID)) {
                            for (BluetoothGattCharacteristic serviceCharacteristic : service.getCharacteristics()) {
                                UUID characteristicUuid = serviceCharacteristic.getUuid();
                                if (characteristicUuid.equals(charUUID)) {
                                    characteristic = serviceCharacteristic;
                                }
                            }
                        }
                    }

                    // Check if characteristic has NOTIFY or INDICATE properties and set the correct byte value to be written
                    int properties = characteristic.getProperties();
                    if ((properties & PROPERTY_NOTIFY) > 0 || (properties & PROPERTY_INDICATE) > 0) {
                        value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
                    } else {
                        Log.e(TAG, String.format("ERROR: Characteristic %s does not have notify or indicate property", characteristic.getUuid()));
                    }

                    //Set notification and read characteristic
                    if (value.length > 0) {
                        gatt.setCharacteristicNotification(characteristic, true);
                        UUID actualUUID = characteristic.getUuid();
                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(configUUID);

                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);

                        gatt.writeDescriptor(descriptor);
                        if (actualUUID.equals(charUUID)) {
                            gatt.readCharacteristic(characteristic);
                        }
                    }
                }
            }

            @Override
            public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                BluetoothGattCharacteristic characteristic = gatt.getService(serviceUUID).getCharacteristic(charUUID);

                characteristic.setValue(new byte[]{1,1});
                gatt.writeCharacteristic(characteristic);
            }

            @Override
            // Result of a characteristic read operation
            public void onCharacteristicChanged(BluetoothGatt gatt,
                                                BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                if (flag == 0) {
                    flag = 1;
                    byte[] reading = characteristic.getValue();
                    //TODO: Figure out how to read bgm (bit 1) and arduino (bit 0)
                    displayVal(String.valueOf(reading[1]));
                }
                flag = 0;
            }
        };

        //Connect to the device
        String deviceAddr = getIntent().getExtras().getString("deviceAddr");
        Log.d(TAG, deviceAddr);
        device = mbluetoothAdapter.getRemoteDevice(deviceAddr);
        BluetoothGatt gatt = device.connectGatt(this, false, gattCallback, TRANSPORT_LE);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void goBackToScan() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent.addFlags(FLAG_ACTIVITY_NO_ANIMATION));
    }

    public void displayVal(String sensorVal) {
        dataPoints.add(new DataPoint(count, Integer.parseInt(sensorVal)));
        count++;

        TextView setDeviceReading = (TextView) findViewById(R.id.deviceReading);
        setDeviceReading.setText(sensorVal);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        if (dataPoints.size() > 0) {
            try {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (DataPoint dataPoint : dataPoints) {
                    series.appendData(dataPoint, true, dataPoints.size());
                }
                graph.addSeries(series);
            } catch (IllegalArgumentException e) {
                Toast.makeText(ReadDevice.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}