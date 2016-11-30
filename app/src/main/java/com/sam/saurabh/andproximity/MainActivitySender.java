package com.sam.saurabh.andproximity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class MainActivitySender extends AppCompatActivity {
    //flag to detect flash is on or off
    private boolean isLighOn = false;

    private CameraManager mCameraManager;
    private String mCameraId;
    private Button button;
    private Button blink;
    private EditText BinaryString;
    private EditText TimeGap;
    private  EditText onTime;
    private RadioGroup rb;
    private RadioButton rbutton;
    Handler handler;


    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_sender);

       // handler = new Handler();
        blink= (Button) findViewById(R.id.buttonBlinklight);
        BinaryString= (EditText) findViewById(R.id.String);
        rb = (RadioGroup) findViewById(R.id.stringtype);

        Context context = this;
        PackageManager pm = context.getPackageManager();

        // does the device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }
        //initializing CameraManager
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }





        blink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int selectedId = rb.getCheckedRadioButtonId();
                rbutton = (RadioButton) findViewById(selectedId);
                String selection = Integer.toString(selectedId);
                rbutton = (RadioButton) findViewById(selectedId);
                String rvalue = rbutton.getText().toString();
                int count;
                String rawstr= BinaryString.getText().toString();

                String str = new String();

                if(rvalue.equals("Binary String")){
                    str=rawstr;
                }
                //converting text to 8 bit binary format
                else if(rvalue.equals("Text String")){
                    byte[] bytes = rawstr.getBytes();
                    BigInteger bi = new BigInteger(bytes);
                    str = bi.toString(2);
                    str="0"+str;
                    Log.i("info",str);
                }

                for(count=0;count<str.length();count++) {

                    if(str.charAt(count)=='1') {
                        turnOnFlashLight(160);
                        turnOffFlashLight(160);

                    }
                    else{
                        turnOnFlashLight(400);
                        turnOffFlashLight(400);
                    }

                }
            }
        });}
    //function for turn on the flash
    public void turnOnFlashLight(long waitt) {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        waittime(waitt);
    }

    //function for turn off the flash
    public void turnOffFlashLight(long waitt) {


        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        waittime(waitt);
    }

    public void waittime(long wt){
        try {
            TimeUnit.MILLISECONDS.sleep(wt);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onPause() {
        super.onPause();

    }
}

