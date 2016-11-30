package com.sam.saurabh.andproximity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivityStart extends AppCompatActivity {

    Button bsender,breceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_start);

        bsender = (Button) findViewById(R.id.buttonSender);
        breceiver = (Button) findViewById(R.id.buttonReceiver);

        bsender.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent inent = new Intent(v.getContext(), MainActivitySender.class);
                        startActivity(inent);
                    }
                }
        );
        breceiver.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        Intent inent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(inent);
                    }
                }
        );
    }
}
