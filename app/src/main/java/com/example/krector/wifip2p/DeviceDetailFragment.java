/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.krector.wifip2p;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener, GoogleApiClient.OnConnectionFailedListener{

    protected static final int CHOOSE_FILE_RESULT_CODE = 20;
    private static final int kActivityRequestCode_EnableBluetooth = 19;

    private View mContentView = null;
    private WifiP2pDevice device;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    protected static FileOutputStream fileStream;

    protected static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 16;
    protected static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 32;

    private static Context baseContext;

    static String hapticNodeIdRight = "fbe39044";
    static String hapticNodeIdLeft = "3aab567d";
    static GoogleApiClient mGoogleApiClient;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage((FragmentActivity)getActivity() /* FragmentActivity */,
                        this /* OnConnectionFailedListener */)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                writeToFile("hello world!");
                break;
            }
            case MY_PERMISSION_ACCESS_FINE_LOCATION:{
                writeToFile("Location access granted");
                break;
            }
        }
    }

    protected static void writeToFile(String text) {
        if(fileStream == null && text.equals("start")){
            fileStream  = getOutputLogFileStream();
        }else if(fileStream == null){
            return;
        }
        try {
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            fileStream.write((ts+"_"+text+'\n').getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(text.equals("stop")){
            try{
                fileStream.close();
                fileStream = null;
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        baseContext = getActivity().getBaseContext();
        mContentView = inflater.inflate(R.layout.device_detail, null);
        mContentView.findViewById(R.id.btn_connect).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiP2pConfig config = new WifiP2pConfig();
                config.groupOwnerIntent = 0;
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                progressDialog = ProgressDialog.show(getActivity(), "Press back to cancel",
                        "Connecting to :" + device.deviceAddress, true, true
//                        new DialogInterface.OnCancelListener() {
//
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                ((DeviceActionListener) getActivity()).cancelDisconnect();
//                            }
//                        }
                );
                ((DeviceListFragment.DeviceActionListener) getActivity()).connect(config);

                ActivityCompat.requestPermissions(getActivity(), new String[]
                                {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
//                ActivityCompat.requestPermissions(getActivity(), new String[]
//                                {Manifest.permission.ACCESS_FINE_LOCATION},
//                        MY_PERMISSION_ACCESS_FINE_LOCATION);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
                    }
                });

        mContentView.findViewById(R.id.btn_start_mainscreen).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // Allow user to pick an image from Gallery or other
                        // registered apps
                        /*Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, CHOOSE_FILE_RESULT_CODE);*/
                        Intent intent = new Intent(getActivity(), MainScreen.class);
                        intent.putExtra("hostaddress", info.groupOwnerAddress.getHostAddress());
                        startActivity(intent);
                    }
                });

        return mContentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==19){
            return;
        }
        // User has picked an image. Transfer it to group owner i.e peer using
        // FileTransferService.
        Uri uri = data.getData();
        TextView statusText = (TextView) mContentView.findViewById(R.id.status_text);
        statusText.setText("Sending: " + uri);
        Log.d(WiFiDirectActivity.TAG, "Intent----------- " + uri);
        Intent serviceIntent = new Intent(getActivity(), FileTransferService.class);
        serviceIntent.setAction(FileTransferService.ACTION_SEND_FILE);
        serviceIntent.putExtra(FileTransferService.EXTRAS_FILE_PATH, uri.toString());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;
        this.getView().setVisibility(View.VISIBLE);

        // The owner IP is now known.
        TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(getResources().getString(R.string.group_owner_text)
                + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
                        : getResources().getString(R.string.no)));

        // InetAddress from WifiP2pInfo struct.
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            new FileServerAsyncTask(getActivity(), mContentView.findViewById(R.id.status_text), null)
                    .execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView.findViewById(R.id.btn_start_mainscreen).setVisibility(View.VISIBLE);
            ((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
                    .getString(R.string.client_text));
        }

        // hide the connect button
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    public void showDetails(WifiP2pDevice device) {
        this.device = device;
        this.getView().setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(device.deviceAddress);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(device.toString());

    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    public void resetViews() {
        mContentView.findViewById(R.id.btn_connect).setVisibility(View.VISIBLE);
        TextView view = (TextView) mContentView.findViewById(R.id.device_address);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.device_info);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.group_owner);
        view.setText(R.string.empty);
        view = (TextView) mContentView.findViewById(R.id.status_text);
        view.setText(R.string.empty);
        mContentView.findViewById(R.id.btn_start_mainscreen).setVisibility(View.GONE);
        this.getView().setVisibility(View.GONE);
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    public static class FileServerAsyncTask extends AsyncTask<Void, Void, String> {

        private TextView statusText;
        private Context context;
        private static MediaPlayer mp;
        private boolean cameraStarted;
        /**
         * @param statusText
         */
        public FileServerAsyncTask(Context context, View statusText, MediaPlayer mp) {
            this.statusText = (TextView) statusText;
            this.context = context;
            this.mp = mp;
            this.cameraStarted = false;
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WiFiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WiFiDirectActivity.TAG, "Server: connection done");

                Log.d(WiFiDirectActivity.TAG, "server: copying string");
                InputStream inputstream = client.getInputStream();
                String theString = IOUtils.toString(inputstream, "UTF-8");
                serverSocket.close();
                return theString;
            } catch (IOException e) {
                Log.e(WiFiDirectActivity.TAG, e.getMessage());
                return null;
            }
        }

        protected void playMedia(int track, boolean loop) {
            stopMedia();
            mp = MediaPlayer.create(context, track);
//            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer) {
//                    stopMedia();
//                }
//            });
            mp.setLooping(loop);
            mp.seekTo(0);
            mp.start();
        }
        protected void stopMedia(){
            if(mp!=null) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                mp.release();
                mp = null;
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            final Handler handler = new Handler();
            statusText.setText("String copied - " + result);
            writeToFile(result);
            switch(result){

                case "voice_left_90":
                    playMedia(R.raw.v_left_90, false);
                    break;

                case "voice_left_45":
                    playMedia(R.raw.v_left_45, false);
                    break;

                case "voice_left_on":
                    playMedia(R.raw.correct_left, true);
                    break;

                case "voice_left_off":
                    stopMedia();
                    break;

                case "voice_right_90":
                    playMedia(R.raw.v_right_90, false);
                    break;

                case "voice_right_45":
                    playMedia(R.raw.v_right_45, false);
                    break;

                case "voice_right_on":
                    playMedia(R.raw.correct_right, true);
                    break;

                case "voice_right_off":
                    stopMedia();
                    break;

                case "heartbeat_left_90":
                    playMedia(R.raw.h_left_90, false);
                    break;

                case "heartbeat_left_45":
                    playMedia(R.raw.h_left_45, false);
                    break;

                case "heartbeat_left_on":
                    playMedia(R.raw.h_left_90, true);
                    break;

                case "heartbeat_left_off":
                    stopMedia();
                    break;

                case "heartbeat_right_90":
                    playMedia(R.raw.h_right_90, false);
                    break;

                case "heartbeat_right_45":
                    playMedia(R.raw.h_right_45, false);
                    break;

                case "heartbeat_right_on":
                    playMedia(R.raw.h_right_90, true);
                    break;

                case "heartbeat_right_off":
                    stopMedia();
                    break;

                case "ConnectHaptic":
                    startHaptic();
                    break;

                case "haptic_left_90":
                    requestHaptic("two","left");
                    break;

                case "haptic_left_45":
                    requestHaptic("one","left");
                    break;

                case "haptic_right_90":
                    requestHaptic("two","right");
                    break;

                case "haptic_right_45":
                    requestHaptic("one","right");
                    break;

                case "haptic_left_on":
                    //TODO: figure out looping haptics
//                    sendBluetoothMessage("left","!L11");
                    break;

                case "haptic_left_off":
//                    sendBluetoothMessage("left","!L10");
                    break;

                case "haptic_right_on":
//                    sendBluetoothMessage("right","!L11");
                    break;

                case "haptic_right_off":
//                    sendBluetoothMessage("right","!L10");
                    break;

                case "stop":
                    if(SubjectCamera.isRecording) {
                        playMedia(R.raw.stop, false);
                        SubjectCamera.endRecording();
                        this.cameraStarted = false;
                    }
                    break;

                case "start":
                    if(!SubjectCamera.isRecording) {
                        playMedia(R.raw.start, false);
                        Intent intent = new Intent((Activity) context, SubjectCamera.class);
                        ((Activity) context).startActivityForResult(intent, 1);
                        this.cameraStarted = true;
                    }
                    break;
            }
            new FileServerAsyncTask(context, statusText, mp).execute();
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            //statusText.setText("Opening a server socket");
        }

    }

    public static boolean copyFile(InputStream inputStream, OutputStream out) {
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(WiFiDirectActivity.TAG, e.toString());
            return false;
        }
        return true;
    }

    private static FileOutputStream getOutputLogFileStream() {
        if (ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.

            File logStorageDir = new File(Environment.getExternalStorageDirectory(), "EyesFreeTrack");
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (!logStorageDir.exists()) {
                if (!logStorageDir.mkdirs()) {
                    Log.d("EyesFreeTrack", "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            Long tsLong = System.currentTimeMillis() / 1000;
            String ts = tsLong.toString();
            File logFile = new File(logStorageDir.getPath() + File.separator +
                    "LOG_" + ts + ".txt");


            FileOutputStream logStream = null;
            try {
                logStream = new FileOutputStream(logFile);
                logStream.write("Log File Initialized\n".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return logStream;
        }
        return null;
    }

    static void startHaptic(){
        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).setResultCallback(
                new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult result) {
                        // Use result
                        for (Node node : result.getNodes()) {
                            if (node.isNearby() && node.getDisplayName().equals("G Watch D8B7")){
                                hapticNodeIdRight = node.getId();
                            }else if(node.isNearby() && node.getDisplayName().equals("Moto 360 A09B")){
                                hapticNodeIdLeft = node.getId();
                            }
                        }
                        requestHaptic("one","right");
                        requestHaptic("one","left");
                    }
                }
        );
    }

    static void requestHaptic(String hapticMessage, String watch) {
        try{
            byte[] bytes = hapticMessage.getBytes("UTF-8");
            if (hapticNodeIdRight != null && watch.equals("right")) {
                Wearable.MessageApi.sendMessage(mGoogleApiClient, hapticNodeIdRight,"/haptic_message", bytes);
            } else if(hapticNodeIdLeft != null && watch.equals("left")){
                Wearable.MessageApi.sendMessage(mGoogleApiClient, hapticNodeIdLeft,"/haptic_message", bytes);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("sd","Connection Failed");
    }
}


