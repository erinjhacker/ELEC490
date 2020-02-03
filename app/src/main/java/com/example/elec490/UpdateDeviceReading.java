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

import java.io.Serializable;
import java.util.ArrayList;

public class UpdateDeviceReading extends AppCompatActivity {

    private final static String TAG = "READ";

    String sensorVal;

    ArrayList<DataPoint> dataPoints;

    int count;

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

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        Bundle args = getIntent().getBundleExtra("BUNDLE");
        dataPoints = (ArrayList<DataPoint>) args.getSerializable("dataPoints");

        count = getIntent().getExtras().getInt("count");

        if (dataPoints.size() > 0) {
            try {
                LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (DataPoint dataPoint : dataPoints) {
                    series.appendData(dataPoint, true, dataPoints.size());
                }
                graph.addSeries(series);
            } catch (IllegalArgumentException e) {
                Toast.makeText(UpdateDeviceReading.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        goBackToRead(device);
    }

    public void goBackToRead(BluetoothDevice device) {
        //Update in new activity
        Intent intent = new Intent(this, ReadDevice.class);
        //Not sure which extras are needed
        intent.putExtra("deviceAddr", device.getAddress());
        intent.putExtra("deviceName", device.getName());
        intent.putExtra("sensorVal", sensorVal);
        intent.putExtra("count", count);
        Bundle args = new Bundle();
        args.putSerializable("dataPoints", (Serializable)dataPoints);
        intent.putExtra("BUNDLE", args);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }
}
