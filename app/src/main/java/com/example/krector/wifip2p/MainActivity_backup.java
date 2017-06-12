/*
package com.example.krector.wifip2p;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    WifiP2pManager mManager;
    Channel mChannel;
    BroadcastReceiver mReceiver;
    private final IntentFilter mIntentFilter = new IntentFilter();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
    }

    */
/* register the broadcast receiver with the intent values to be matched *//*

    @Override
    protected void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(mReceiver, mIntentFilter);
    }
    */
/* unregister the broadcast receiver *//*

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }


    public static class FileServerAsyncTask extends AsyncTask<Void, Void, Void> {

        private Context context;
        private TextView statusText;

        public FileServerAsyncTask(Context context, View statusText) {
            this.context = context;
            this.statusText = (TextView) statusText;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                */
/**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 *//*

                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();

                */
/**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 *//*


                InputStream inputstream = client.getInputStream();
                Log.d("stream", inputstream.toString());
                serverSocket.close();
                return null;

            } catch (IOException e) {
                Log.e("server socket", e.getMessage());
                return null;
            }
        }
    }
}


*/
