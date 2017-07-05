package com.example.krector.wifip2p;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Heartbeat extends AppCompatActivity implements OnClickListener {

    private String hostaddress;
    private Boolean curved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        curved = myIntent.getBooleanExtra("curved",true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_heartbeat);

        Button left90Button = (Button) findViewById(R.id.heartbeat_left_90);
        Button left45Button = (Button) findViewById(R.id.heartbeat_left_45);
        Button right45Button = (Button) findViewById(R.id.heartbeat_right_45);
        Button right90Button = (Button) findViewById(R.id.heartbeat_right_90);
        Button stopButton = (Button) findViewById(R.id.heartbeatstop);
        Button heartbeatstartbutton = (Button) findViewById(R.id.heartbeatstart);

        if(curved){
            left90Button.setText("Turn 90° To Your Left");
            left45Button.setText("Turn 45° To Your Left");
            right90Button.setText("Turn 90° To Your Right");
            right45Button.setText("Turn 45° To Your Right");
        }else{
            left90Button.setText("Turn Off Left Heartbeat");
            left45Button.setText("Correct To The Left");
            right45Button.setText("Correct To The Right");
            right90Button.setText("Turn Off Left Heartbeat");
        }

        left90Button.setOnClickListener(this);
        left45Button.setOnClickListener(this);
        right45Button.setOnClickListener(this);
        right90Button.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        heartbeatstartbutton.setOnClickListener(this);

    }

    public void sendTextMessage(String message) {
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

            case R.id.heartbeat_left_90:
                if(curved){
                    sendTextMessage("heartbeat_left_90");
                }else{
                    sendTextMessage("heartbeat_left_off");
                }
                break;

            case R.id.heartbeat_left_45:
                if(curved) {
                    sendTextMessage("heartbeat_left_45");
                }else{
                    sendTextMessage("heartbeat_left_on");
                }
                break;

            case R.id.heartbeat_right_90:
                if(curved){
                    sendTextMessage("heartbeat_right_90");
                }else{
                    sendTextMessage("heartbeat_right_off");
                }
                break;

            case R.id.heartbeat_right_45:
                if(curved) {
                    sendTextMessage("heartbeat_right_45");
                }else{
                    sendTextMessage("heartbeat_right_on");
                }
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