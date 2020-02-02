package com.example.elec490;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class UpdateDeviceReading extends AppCompatActivity {

    private final static String TAG = "READ";

    String sensorVal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
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

//        final GraphView graph = (GraphView) findViewById(R.id.graph);
//        graph.setVisibility(View.VISIBLE);

//        try {
//            //TODO: Replace these with values from sensor
//            //TODO: Figure out scale for y and how to track x (e.g. iterations)
//            //TODO: Figure out how to keep adding to series with each new reading
//            //TODO: Add this graph to ReadDevice.java
//            LineGraphSeries <DataPoint> series = new LineGraphSeries< >(new DataPoint[] {
//                    new DataPoint(0, 1),
//                    new DataPoint(1, 1),
//                    new DataPoint(2, 2),
//                    new DataPoint(3, 3),
//                    new DataPoint(4, 4)
//            });
//            graph.addSeries(series);
//        } catch (IllegalArgumentException e) {
//            Toast.makeText(UpdateDeviceReading.this, e.getMessage(), Toast.LENGTH_LONG).show();
//        }

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
