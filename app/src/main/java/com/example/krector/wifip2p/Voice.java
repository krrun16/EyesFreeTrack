package com.example.krector.wifip2p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.id.message;

public class Voice extends AppCompatActivity implements OnClickListener {

    private String hostaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_voice);

        Button voiceButton1 = (Button) findViewById(R.id.voice1);
        //Button voiceButton2 = (Button) findViewById(R.id.voice2);
        Button voiceButton3 = (Button) findViewById(R.id.voice3);
        //Button voiceButton4 = (Button) findViewById(R.id.voice4);
        Button voiceButton5 = (Button) findViewById(R.id.voice5);
        //Button voiceButton6 = (Button) findViewById(R.id.voice6);
        Button voiceButton7 = (Button) findViewById(R.id.voice7);
        //Button voiceButton8 = (Button) findViewById(R.id.voice8);
        Button voiceButton9 = (Button) findViewById(R.id.voice9);
        Button stopButton = (Button) findViewById(R.id.voicestop);
        Button voicestartbutton = (Button) findViewById(R.id.voicestart);

        voiceButton1.setOnClickListener(this);
        //voiceButton2.setOnClickListener(this);
        voiceButton3.setOnClickListener(this);
        //voiceButton4.setOnClickListener(this);
        voiceButton5.setOnClickListener(this);
        //voiceButton6.setOnClickListener(this);
        voiceButton7.setOnClickListener(this);
        //voiceButton8.setOnClickListener(this);
        voiceButton9.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        voicestartbutton.setOnClickListener(this);

    }

    protected void writeToFile(String text) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(DeviceDetailFragment.file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                text = text + "\n";
                fos.write(text.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendTextMessage(String message) {
        //writeToFile(System.currentTimeMillis() + ":" + message);
        Intent serviceIntent = new Intent(this, TextTransferService.class);
        serviceIntent.setAction(TextTransferService.ACTION_SEND_TEXT);
        serviceIntent.putExtra("message", message);
        serviceIntent.putExtra(TextTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                hostaddress);
        serviceIntent.putExtra(TextTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        this.startService(serviceIntent);
    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.voice1:
                sendTextMessage("voice9");
                break;

            //case R.id.voice2:
            //    sendTextMessage("voice8");
            //    break;

            case R.id.voice3:
                sendTextMessage("voice7");
                break;

            //case R.id.voice4:
            //    sendTextMessage("voice6");
            //    break;

            case R.id.voice5:
                writeToFile(System.currentTimeMillis() + ":" + "parallel");
                break;

            //case R.id.voice6:
            //    sendTextMessage("voice4");
            //    break;

            case R.id.voice7:
                sendTextMessage("voice3");
                break;

            //case R.id.voice8:
            //    sendTextMessage("voice2");
            //    break;

            case R.id.voice9:
                sendTextMessage("voice1");
                break;

            case R.id.voicestop:
                sendTextMessage("stop");
                break;

            case R.id.voicestart:
                sendTextMessage("start");
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}