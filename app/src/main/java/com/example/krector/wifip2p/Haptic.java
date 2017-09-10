package com.example.krector.wifip2p;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import static android.R.attr.button;
import static android.R.attr.host;
import static android.R.attr.port;

public class Haptic extends AppCompatActivity implements OnClickListener{

    private String hostaddress;
    private Boolean curved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        curved = myIntent.getBooleanExtra("curved",true);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_haptic);

        Button left90Button = (Button) findViewById(R.id.haptic_left_90);
        Button left45Button = (Button) findViewById(R.id.haptic_left_45);
        Button connectButton = (Button) findViewById(R.id.haptic_connect);
        Button right45Button = (Button) findViewById(R.id.haptic_right_45);
        Button right90Button = (Button) findViewById(R.id.haptic_right_90);
        Button stopButton = (Button) findViewById(R.id.hapticstop);
        Button hapticstartbutton = (Button) findViewById(R.id.hapticstart);

        if(curved){
            left90Button.setText("Turn 90° To Your Left");
            left45Button.setText("Turn 45° To Your Left");
            right90Button.setText("Turn 90° To Your Right");
            right45Button.setText("Turn 45° To Your Right");
        }else{
            left90Button.setText("Turn Off Left Haptics");
            left45Button.setText("Correct To The Right");
            right45Button.setText("Correct To The Left");
            right90Button.setText("Turn Off Right Haptics");
        }

        left90Button.setOnClickListener(this);
        left45Button.setOnClickListener(this);
        connectButton.setOnClickListener(this);
        right45Button.setOnClickListener(this);
        right90Button.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        hapticstartbutton.setOnClickListener(this);

        left90Button.setBackgroundColor(Color.DKGRAY);
        left45Button.setBackgroundColor(Color.DKGRAY);
        connectButton.setBackgroundColor(Color.DKGRAY);
        right45Button.setBackgroundColor(Color.DKGRAY);
        right90Button.setBackgroundColor(Color.DKGRAY);
        stopButton.setBackgroundColor(Color.DKGRAY);
        hapticstartbutton.setBackgroundColor(Color.DKGRAY);

    }

    public void sendTextMessage(String message) {
        Log.d("sendTextMessage", ""+System.currentTimeMillis());
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
        Log.d("onClick", System.currentTimeMillis()+"");
        Button button = (Button) findViewById(v.getId());
        ColorDrawable buttonColor = (ColorDrawable) button.getBackground();
        if(buttonColor.getColor() == Color.DKGRAY) {
            button.setBackgroundColor(Color.YELLOW);
        } else {
            button.setBackgroundColor(Color.DKGRAY);
        }

        switch(v.getId()){
            case R.id.haptic_left_90:
                if(curved) {
                    sendTextMessage("haptic_left_90");
                }else{
                    sendTextMessage("haptic_left_off");
                }
                break;

            case R.id.haptic_left_45:
                if(curved) {
                    sendTextMessage("haptic_left_45");
                }else{
                    sendTextMessage("haptic_left_on");
                }
                break;

            case R.id.haptic_connect:
                sendTextMessage("ConnectHaptic");
                break;

            case R.id.haptic_right_45:
                if(curved) {
                    sendTextMessage("haptic_right_45");
                }else{
                    sendTextMessage("haptic_right_on");
                }
                break;

            case R.id.haptic_right_90:
                if(curved) {
                    sendTextMessage("haptic_right_90");
                }else{
                    sendTextMessage("haptic_right_off");
                }
                break;

            case R.id.hapticstop:
                sendTextMessage("stop_haptic");
                break;

            case R.id.hapticstart:
                sendTextMessage("start_haptic");
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
