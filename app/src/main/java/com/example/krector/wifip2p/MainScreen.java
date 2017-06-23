package com.example.krector.wifip2p;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainScreen extends AppCompatActivity {

    private String hostaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main_screen);
    }
    public void straightVoiceButtonPressed(View view)
    {
        Intent voiceButton = new Intent(MainScreen.this, Voice.class);
        voiceButton.putExtra("hostaddress", hostaddress);
        voiceButton.putExtra("curved",false);
        startActivity(voiceButton);
    }

    public void straightHeartbeatButtonPressed(View view)
    {
        Intent heartbeatButton = new Intent(MainScreen.this, Heartbeat.class);
        heartbeatButton.putExtra("hostaddress", hostaddress);
        heartbeatButton.putExtra("curved",false);
        startActivity(heartbeatButton);
    }

    public void straightHapticButtonPressed(View view)
    {
        Intent hapticButton = new Intent(MainScreen.this, Haptic.class);
        hapticButton.putExtra("hostaddress", hostaddress);
        hapticButton.putExtra("curved",false);
        startActivity(hapticButton);
    }
    public void curvedVoiceButtonPressed(View view)
    {
        Intent voiceButton = new Intent(MainScreen.this, Voice.class);
        voiceButton.putExtra("hostaddress", hostaddress);
        voiceButton.putExtra("curved",true);
        startActivity(voiceButton);
    }

    public void curvedHeartbeatButtonPressed(View view)
    {
        Intent heartbeatButton = new Intent(MainScreen.this, Heartbeat.class);
        heartbeatButton.putExtra("hostaddress", hostaddress);
        heartbeatButton.putExtra("curved",true);
        startActivity(heartbeatButton);
    }

    public void curvedHapticButtonPressed(View view)
    {
        Intent hapticButton = new Intent(MainScreen.this, Haptic.class);
        hapticButton.putExtra("hostaddress", hostaddress);
        hapticButton.putExtra("curved",true);
        startActivity(hapticButton);
    }


}
