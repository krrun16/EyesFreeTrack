package com.example.krector.wifip2p;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by krector on 4/3/2017.
 */

public class TextTransferService extends IntentService {

    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_TEXT = "com.example.krector.wifip2p.SEND_TEXT";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";

    public TextTransferService(String name) {
        super(name);
    }

    public TextTransferService() {
        super("TextTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("onHandleIntent", System.currentTimeMillis()+"");
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_TEXT)) {
            String message = intent.getExtras().getString("message");
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            Socket socket = new Socket();
            try {

                Log.d(WiFiDirectActivity.TAG, "NULL Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WiFiDirectActivity.TAG, "NULL Client socket - " + socket.isConnected());

                Log.d("getting stream", System.currentTimeMillis()+"");
                OutputStream stream = socket.getOutputStream();
                Log.d("getting resolver", System.currentTimeMillis()+"");
                ContentResolver cr = context.getContentResolver();
                Log.d("getting is", System.currentTimeMillis()+"");
                InputStream is = new ByteArrayInputStream(message.getBytes("UTF-8"));
                Log.d("copy", System.currentTimeMillis()+"");
                DeviceDetailFragment.copyFile(is, stream);
                Log.d("flush", System.currentTimeMillis()+"");
                Log.d(WiFiDirectActivity.TAG, "Client: Data written");
                Log.d("data written", System.currentTimeMillis()+"");
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, "TTS exception:"+e.getMessage());
            } finally {
                if (socket != null) {

                    if (socket.isConnected()) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            // Give up
                            e.printStackTrace();
                        }
                    }

                }
            }

        }
    }
}
