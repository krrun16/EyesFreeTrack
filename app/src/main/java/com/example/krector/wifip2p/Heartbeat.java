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

public class Heartbeat extends AppCompatActivity implements OnClickListener {

    private String hostaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_heartbeat);

        Button heartbeatButton1 = (Button) findViewById(R.id.heartbeat1);
        //Button heartbeatButton2 = (Button) findViewById(R.id.heartbeat2);
        Button heartbeatButton3 = (Button) findViewById(R.id.heartbeat3);
        //Button heartbeatButton4 = (Button) findViewById(R.id.heartbeat4);
//        Button heartbeatButton5 = (Button) findViewById(R.id.heartbeat5);
        //Button heartbeatButton6 = (Button) findViewById(R.id.heartbeat6);
        Button heartbeatButton7 = (Button) findViewById(R.id.heartbeat7);
        //Button heartbeatButton8 = (Button) findViewById(R.id.heartbeat8);
        Button heartbeatButton9 = (Button) findViewById(R.id.heartbeat9);
        Button stopButton = (Button) findViewById(R.id.heartbeatstop);
        Button heartbeatstartbutton = (Button) findViewById(R.id.heartbeatstart);

        heartbeatButton1.setOnClickListener(this);
        //heartbeatButton2.setOnClickListener(this);
        heartbeatButton3.setOnClickListener(this);
        //heartbeatButton4.setOnClickListener(this);
//        heartbeatButton5.setOnClickListener(this);
        //heartbeatButton6.setOnClickListener(this);
        heartbeatButton7.setOnClickListener(this);
        //heartbeatButton8.setOnClickListener(this);
        heartbeatButton9.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        heartbeatstartbutton.setOnClickListener(this);

    }

    public void sendTextMessage(String message) {
//        writeToFile(System.currentTimeMillis() + ":" + message);
        Intent serviceIntent = new Intent(this, TextTransferService.class);
        serviceIntent.setAction(TextTransferService.ACTION_SEND_TEXT);
        serviceIntent.putExtra("message", message);
        serviceIntent.putExtra(TextTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                hostaddress);
        serviceIntent.putExtra(TextTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        this.startService(serviceIntent);
    }

//    protected void writeToFile(String text) {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                == PackageManager.PERMISSION_GRANTED) {
//
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(DeviceDetailFragment.file, true);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                text = text + "\n";
//                fos.write(text.getBytes());
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.heartbeat1:
                sendTextMessage("heartbeat1");
                break;

            //case R.id.heartbeat2:
            //    sendTextMessage("heartbeat2");
            //    break;

            case R.id.heartbeat3:
                sendTextMessage("heartbeat3");
                break;

            //case R.id.heartbeat4:
            //    sendTextMessage("heartbeat4");
            //    break;

//            case R.id.heartbeat5:
//                writeToFile(System.currentTimeMillis() + ":" + "parallel");
//                break;

            //case R.id.heartbeat6:
            //    sendTextMessage("heartbeat6");
            //    break;

            case R.id.heartbeat7:
                sendTextMessage("heartbeat7");
                break;

            //case R.id.heartbeat8:
            //    sendTextMessage("heartbeat8");
            //    break;

            case R.id.heartbeat9:
                sendTextMessage("heartbeat9");
                break;

            case R.id.heartbeatstop:
                sendTextMessage("stop");
                break;

            case R.id.heartbeatstart:
                sendTextMessage("start");
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}