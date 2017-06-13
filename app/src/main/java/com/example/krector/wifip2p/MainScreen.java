package com.example.krector.wifip2p;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public void voiceButtonPressed(View view)
    {
        Intent voiceButton = new Intent(MainScreen.this, Voice.class);
        voiceButton.putExtra("hostaddress", hostaddress);
        startActivity(voiceButton);
    }

    public void heartbeatButtonPressed(View view)
    {
        Intent heartbeatButton = new Intent(MainScreen.this, Heartbeat.class);
        heartbeatButton.putExtra("hostaddress", hostaddress);
        startActivity(heartbeatButton);
    }

    public void hapticButtonPressed(View view)
    {
        Intent hapticButton = new Intent(MainScreen.this, Haptic.class);
        hapticButton.putExtra("hostaddress", hostaddress);
        startActivity(hapticButton);
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
}