package com.example.elec490;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import static android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION;

public class ProgressActivity extends AppCompatActivity {

    private static final String TAG = "ProgressActivity";

    ArrayList<String> files;

    String output = "waiting";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_progress);

        Button startButton = findViewById(R.id.begin2);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProgressActivity.this, MainActivity.class));
            }
        });

        File fileDirectory = new File(Environment.getDataDirectory()+"/data/com.example.elec490/files/track_data/");
        String directoryOutput = fileDirectory.toString();
        File[] trackFiles = fileDirectory.listFiles();
        files = new ArrayList<String>(trackFiles.length);

        if (trackFiles.length != 0) {
            for (int ii = 0; ii < trackFiles.length; ii++) {
                String fileOutput = trackFiles[ii].toString();
                //System.out.println(fileOutput);
                files.add(ii,fileOutput);
            }
        }

        try {
            String filename = files.get(trackFiles.length - 1);
            FileInputStream fis = new FileInputStream (new File(filename));;
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            output = sb.toString();

        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }

        TextView setTrackedData = (TextView) findViewById(R.id.dateAndTime1);
        String fullname1 = files.get(trackFiles.length -1 );

        String year1 = "";
        String month1 = "";
        String day1 = "";
        String hr1 = "";
        String min1 = "";
        year1 = year1 + fullname1.charAt(48)+fullname1.charAt(49)+fullname1.charAt(50)+fullname1.charAt(51);
        month1 = month1 + fullname1.charAt(52) + fullname1.charAt(53);
        day1 = day1 + fullname1.charAt(54) + fullname1.charAt(55);
        hr1 = hr1 + fullname1.charAt(56) + fullname1.charAt(57);
        min1 = min1 + fullname1.charAt(58) + fullname1.charAt(59);
        String fullDate1 = "";
        fullDate1 = "Taken " + month1 + "/" + day1 + "/" + year1 + " at " + hr1 + ":" + min1;
        setTrackedData.setText(fullDate1);

        ArrayList<DataPoint> dataPt1;
        ArrayList<String> dataSt1;
        int slashCount = 0;

        for(int i = 0; i < output.length() - 2; i++){
            if(output.charAt(i) == '/'){
                slashCount++;
            }
            else{ }
        }

        dataPt1 = new ArrayList<DataPoint>(slashCount);
        dataSt1 = new ArrayList<String>(slashCount);

        int count_in_pt = 0;
        int count_add = 0;
        for(int i = 0; i < output.length() - 1; i++){
            if(output.charAt(i) == '/'){
                String toAdd = "";
                for(int j = count_in_pt; j > 0; j--) {
                    toAdd = toAdd + output.charAt(i - j);
                }
                //dataSt1.add(count_add,toAdd);
                dataPt1.add(new DataPoint(count_add, Double.parseDouble(toAdd)));

                count_in_pt = 0;
                count_add++;
            }
            else{
                count_in_pt++;
            }
        }

        final GraphView graph1 = (GraphView) findViewById(R.id.progGraph1);

        if (dataPt1.size() > 0) {
            try {
                final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (DataPoint dataPoint : dataPt1) {
                    series.appendData(dataPoint, true, dataPt1.size());
                }
                graph1.getViewport().setXAxisBoundsManual(true);
                graph1.getViewport().setMaxX(dataPt1.size());
                if (dataPt1.size() > 100) {
                    graph1.getViewport().setMinX(dataPt1.size() - 100);
                }
                graph1.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph1.addSeries(series);
                    }
                });
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        // -----------EVERYTHING NEW BELOW-----------------

        try {
            String filename = files.get(trackFiles.length - 2);
            FileInputStream fis = new FileInputStream (new File(filename));;
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            output = sb.toString();

        } catch (FileNotFoundException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }

        TextView setTrackedData2 = (TextView) findViewById(R.id.dateAndTime2);
        String fullname2 = files.get(trackFiles.length -2 );

        String year2 = "";
        String month2 = "";
        String day2 = "";
        String hr2 = "";
        String min2 = "";
        year2 = year2 + fullname2.charAt(48)+fullname2.charAt(49)+fullname2.charAt(50)+fullname2.charAt(51);
        month2 = month2 + fullname2.charAt(52) + fullname2.charAt(53);
        day2 = day2 + fullname2.charAt(54) + fullname2.charAt(55);
        hr2 = hr2 + fullname2.charAt(56) + fullname2.charAt(57);
        min2 = min2 + fullname2.charAt(58) + fullname2.charAt(59);
        String fullDate2 = "";
        fullDate2 = "Taken " + month2 + "/" + day2 + "/" + year2 + " at " + hr2 + ":" + min2;
        setTrackedData2.setText(fullDate2);

        ArrayList<DataPoint> dataPt2;
        slashCount = 0;

        for(int i = 0; i < output.length() - 2; i++){
            if(output.charAt(i) == '/'){
                slashCount++;
            }
            else{ }
        }

        dataPt2 = new ArrayList<DataPoint>(slashCount);

        count_in_pt = 0;
        count_add = 0;
        for(int i = 0; i < output.length() - 1; i++){
            if(output.charAt(i) == '/'){
                String toAdd = "";
                for(int j = count_in_pt; j > 0; j--) {
                    toAdd = toAdd + output.charAt(i - j);
                }

                dataPt2.add(new DataPoint(count_add, Double.parseDouble(toAdd)));

                count_in_pt = 0;
                count_add++;
            }
            else{
                count_in_pt++;
            }
        }

        final GraphView graph2 = (GraphView) findViewById(R.id.progGraph2);

        if (dataPt2.size() > 0) {
            try {
                final LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
                for (DataPoint dataPoint : dataPt2) {
                    series.appendData(dataPoint, true, dataPt2.size());
                }
                graph2.getViewport().setXAxisBoundsManual(true);
                graph2.getViewport().setMaxX(dataPt2.size());
                if (dataPt1.size() > 100) {
                    graph2.getViewport().setMinX(dataPt2.size() - 100);
                }
                graph2.getGridLabelRenderer().setHorizontalLabelsVisible(false);
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        graph2.addSeries(series);
                    }
                });
            } catch (IllegalArgumentException e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

    }

}

