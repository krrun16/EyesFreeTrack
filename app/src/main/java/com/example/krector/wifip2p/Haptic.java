package com.example.krector.wifip2p;

import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class Haptic extends AppCompatActivity implements OnClickListener{

    private String hostaddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent myIntent = getIntent();
        hostaddress = myIntent.getStringExtra("hostaddress");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_haptic);

        Button hapticButton1 = (Button) findViewById(R.id.haptic1);
        //Button hapticButton2 = (Button) findViewById(R.id.haptic2);
        Button hapticButton3 = (Button) findViewById(R.id.haptic3);
        //Button hapticButton4 = (Button) findViewById(R.id.haptic4);
        Button hapticButton5 = (Button) findViewById(R.id.haptic5);
        //Button hapticButton6 = (Button) findViewById(R.id.haptic6);
        Button hapticButton7 = (Button) findViewById(R.id.haptic7);
        //Button hapticButton8 = (Button) findViewById(R.id.haptic8);
        Button hapticButton9 = (Button) findViewById(R.id.haptic9);
        Button stopButton = (Button) findViewById(R.id.hapticstop);
        Button hapticstartbutton = (Button) findViewById(R.id.hapticstart);

        hapticButton1.setOnClickListener(this);
        //hapticButton2.setOnClickListener(this);
        hapticButton3.setOnClickListener(this);
        //hapticButton4.setOnClickListener(this);
        hapticButton5.setOnClickListener(this);
        //hapticButton6.setOnClickListener(this);
        hapticButton7.setOnClickListener(this);
        //hapticButton8.setOnClickListener(this);
        hapticButton9.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        hapticstartbutton.setOnClickListener(this);

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

            case R.id.haptic1:
                sendTextMessage("haptic1");
                break;

            //case R.id.haptic2:
            //    sendTextMessage("haptic2");
            //    break;

            case R.id.haptic3:
                sendTextMessage("haptic3");
                break;

            //case R.id.haptic4:
            //    sendTextMessage("haptic4");
            //    break;

            case R.id.haptic5:
                sendTextMessage("ConnectHaptic");
                break;

            //case R.id.haptic6:
            //    sendTextMessage("haptic6");
            //    break;

            case R.id.haptic7:
                sendTextMessage("haptic7");
                break;

            //case R.id.haptic8:
            //    sendTextMessage("haptic8");
            //    break;

            case R.id.haptic9:
                sendTextMessage("haptic9");
                break;

            case R.id.hapticstop:
                sendTextMessage("stop");
                break;

            case R.id.hapticstart:
                sendTextMessage("start");
                break;

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
