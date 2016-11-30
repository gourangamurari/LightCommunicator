package com.sam.saurabh.andproximity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView proxText, maxProx, showVal,showbin;
    SensorManager sm;
    Sensor proxSensor;
    Button but;
    ProgressBar lightMeter;
    File file;
    String[] perms = {"android.permission. WRITE_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;
    FileOutputStream outputStream;
    long gv=0;
    boolean first=false;
    Queue<Float> q;

    float baseval;
    boolean setbaseval;
    int basevalcount;
    final float ambience=1300;
    boolean one;
    boolean zero;
    boolean read;
    String prn ;
    String val ;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        verifyStoragePermissions(this);
        if(isExternalStorageWritable()){
            Toast.makeText(getApplicationContext(), " Writeable",
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getApplicationContext(), " Not Writable",
                    Toast.LENGTH_SHORT).show();
        }
        */
        q = new LinkedList<Float>();
        q.offer((float) 1.0);
        baseval=(float) 0.0;
        setbaseval=false;
        basevalcount=0;
        one=false;
        zero=false;
        read=false;
        prn="" ;
        val="";


        sm=(SensorManager)getSystemService(SENSOR_SERVICE);
        proxSensor=sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        proxText=(TextView)findViewById(R.id.proximitytextView);
        showVal= (TextView) findViewById(R.id.textViewList);
        showbin= (TextView) findViewById(R.id.textViewList1);
        but= (Button) findViewById(R.id.button3);
        but.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        showVal.setText("");
                        showbin.setText("");
                        val="";
                    }
                }
        );


        if(proxSensor == null) {
           Toast.makeText(getApplicationContext(), "No Sensor Found",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(), proxSensor.getName() + " Found",
                    Toast.LENGTH_SHORT).show();
            float max = proxSensor.getMaximumRange();
            sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_FASTEST);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        //sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sm.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        long timeInMillis = (new Date()).getTime()
                + (sensorEvent.timestamp - System.nanoTime()) / 1000000L;
        long diff=timeInMillis-gv;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_LIGHT) {
            float currentReading = sensorEvent.values[0];
            proxText.setText(String.valueOf(currentReading));
            //showVal.append(String.valueOf(currentReading) + "  " + timeInMillis + "  " + diff/1000 +"\n");
            Log.i("info",String.valueOf(currentReading) + "  " + timeInMillis + "  " + diff/1000 +"\n");
            //write(diff/1000 + "  "  +String.valueOf(currentReading) + "  " + timeInMillis + "\n");
            q.offer(currentReading);

        }
        if(first==false){
            gv=timeInMillis;
            first=true;
        }

        if(q.peek()!=null ) {

            float reading = (float) q.poll();
            if (reading > ambience) {
                read = true;
            } else {
                read = false;
            }

            if (one == true && read == false && zero == false) {
                //Log.i("info", "  1");
                val = val + "1";
                one = false;
                zero = false;
                showbin.append("1" + " ");
            } else if (one == false && read == false && zero == true) {
                //Log.i("info", "  0");
                val = val + "0";
                one = false;
                zero = false;
                showbin.append("0" + " ");
            } else if (one == false && read == true && zero == false) {
                one = true;
            } else if (one == true && read == true && zero == false) {
                one = false;
                zero = true;
            }
        }
        if(val!=null && val.length()==8 ){
            byte[] bval = new BigInteger(val, 2).toByteArray();
            try {
                prn = new String(bval, 0, bval.length, "ASCII");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //Log.i("info", prn);
            showVal.append(prn);
            val="";
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }



    public void write(String message) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "sensor-data.txt");
        try {
            //create a filewriter and set append modus to true
            FileWriter fw = new FileWriter(file, true);
            fw.append(message);
            fw.close();

        } catch (IOException e) {
            Log.w("ExternalStorage", "Error writing " + file, e);
        }


    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }




    // Storage Permissions
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
