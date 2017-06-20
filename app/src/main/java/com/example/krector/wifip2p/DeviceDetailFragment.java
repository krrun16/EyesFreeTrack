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
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.krector.UartInterfaceActivity;
import com.example.krector.ble.BleDevicesScanner;
import com.example.krector.ble.BleManager;
import com.example.krector.ble.BleUtils;

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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
public class DeviceDetailFragment extends Fragment implements ConnectionInfoListener, BleManager.BleManagerListener, BleUtils.ResetBluetoothAdapterListener{

    public static final UUID UUID_TX = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID UUID_SERVICE = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    protected static final String leftHaptic = "D0:E9:F6:51:FB:84";
    protected static final String rightHaptic = "FC:AB:EC:4E:01:29";
    public static Boolean rightServicesFound = false;
    public static Boolean leftServicesFound = false;
    public static BluetoothGatt leftGatt;
    public static BluetoothGatt rightGatt;
    public static BluetoothAdapter mAdapter;

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
    private static BleUtils.ResetBluetoothAdapterListener aListener;
    private static BleDevicesScanner mScanner;
    private static ArrayList<BluetoothDeviceData> mScannedDevices;

    static BleManager mBluetoothManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final boolean wasBluetoothEnabled = manageBluetoothAvailability();
        final boolean areLocationServicesReadyForScanning = manageLocationServiceAvailabilityForScanning();

        // Reset bluetooth
        if (wasBluetoothEnabled && areLocationServicesReadyForScanning) {
            BleUtils.resetBluetoothAdapter(baseContext, aListener);
        }
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
        if(fileStream == null){
            fileStream  = getOutputLogFileStream();
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
        aListener = this;
        baseContext = getActivity().getBaseContext();
        mBluetoothManager = BleManager.getInstance(baseContext);
        mBluetoothManager.setBleListener(this);
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
                ActivityCompat.requestPermissions(getActivity(), new String[]
                                {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_ACCESS_FINE_LOCATION);

            }
        });

        mContentView.findViewById(R.id.btn_disconnect).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ((DeviceListFragment.DeviceActionListener) getActivity()).disconnect();
//                        Intent intent = new Intent(getActivity(), SubjectCamera.class);
////                        intent.putExtra("hostaddress", info.groupOwnerAddress.getHostAddress());
//                        getActivity().startActivityForResult(intent, 1);
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
        private MediaPlayer mp;
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

        protected void playMedia(int track) {
            mp = MediaPlayer.create(context, track);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
                }
            });
            mp.setLooping(false);
            mp.seekTo(0);
            mp.start();
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(String result) {
            statusText.setText("String copied - " + result);
            writeToFile(result);
            switch(result){

                case "voice1":
                    playMedia(R.raw.v_left_90);
                    break;

                //case "voice2":
                //    playMedia(R.raw.vtwo);
                //    break;

                case "voice3":
                    playMedia(R.raw.v_left_45);
                    break;

                //case "voice4":
                //    playMedia(R.raw.vfour);
                //    break;

//                case "voice5":
//                    playMedia(R.raw.vfive);
//                    break;

                //case "voice6":
                //    playMedia(R.raw.vsix);
                //    break;

                case "voice7":
                    playMedia(R.raw.v_right_45);
                    break;

                //case "voice8":
                //    playMedia(R.raw.veight);
                //    break;

                case "voice9":
                    playMedia(R.raw.v_right_90);
                    break;

                case "heartbeat1":
                    playMedia(R.raw.h_left_90);
                    break;

                //case "heartbeat2":
                //    playMedia(R.raw.htwo);
                //    break;

                case "heartbeat3":
                    playMedia(R.raw.h_left_45);
                    break;

                //case "heartbeat4":
                //    playMedia(R.raw.hfour);
                //    break;

                //case "heartbeat5":
                //    playMedia(R.raw.hfive);
                //    break;

                //case "heartbeat6":
                //    playMedia(R.raw.hsix);
                //    break;

                case "heartbeat7":
                    playMedia(R.raw.h_right_45);
                    break;

                //case "heartbeat8":
                //    playMedia(R.raw.height);
                //    break;

                case "heartbeat9":
                    playMedia(R.raw.h_right_90);
                    break;

                case "ConnectHaptic":
                    connectBluetooth();
                    break;

                case "stop":
                    if(SubjectCamera.isRecording) {
                        playMedia(R.raw.stop);
                        SubjectCamera.endRecording();
                        this.cameraStarted = false;
                    }
                    break;

                case "start":
                    if(!SubjectCamera.isRecording) {
                        playMedia(R.raw.start);
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
    private static void connectBluetooth() {
        if(rightGatt!=null){
            sendBluetoothMessage("adf","asdf");
            return;
        }
        mAdapter = BleUtils.getBluetoothAdapter(baseContext);
        Boolean check = mAdapter.isEnabled();
        if (BleUtils.getBleStatus(baseContext) != BleUtils.STATUS_BLE_ENABLED) {
            Log.w("EyesFreeTrack", "startScan: BluetoothAdapter not initialized or unspecified address.");
        } else {
            mScanner = new BleDevicesScanner(mAdapter, null, new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    //final String deviceName = device.getName();
                    //Log.d(TAG, "Discovered device: " + (deviceName != null ? deviceName : "<unknown>"));

                    BluetoothDeviceData previouslyScannedDeviceData = null;
                    if (mScannedDevices == null)
                        mScannedDevices = new ArrayList<>();       // Safeguard

                    Boolean newDevice = true;
                    // Check that the device was not previously found
                    for (BluetoothDeviceData deviceData : mScannedDevices) {
                        if (deviceData.device.getAddress().equals(device.getAddress())) {
                            previouslyScannedDeviceData = deviceData;
                            newDevice = false;
                            break;
                        }
                    }

                    BluetoothDeviceData deviceData;
                    if (previouslyScannedDeviceData == null) {
                        // Add it to the mScannedDevice list
                        deviceData = new BluetoothDeviceData();
                        mScannedDevices.add(deviceData);
                    } else {
                        deviceData = previouslyScannedDeviceData;
                    }

                    deviceData.device = device;
                    deviceData.rssi = rssi;
                    deviceData.scanRecord = scanRecord;
                    decodeScanRecords(deviceData);

                    // Update device data
                    if(newDevice){
                        checkForHaptic();
                    }
                }
            });

            // Start scanning
            mScanner.start();
        }
    }

    private static void stopScanning() {
        // Stop scanning
        if (mScanner != null) {
            mScanner.stop();
            mScanner = null;
        }

        checkForHaptic();
    }

    private static void checkForHaptic(){
        Log.e("EyesFreeTrack","Just need to pause");
        if(rightGatt!=null){
            sendBluetoothMessage("test","test");
            return;
        }
        if(mScannedDevices.size()>2){
            Boolean left = false, right = false;
            for(BluetoothDeviceData device : mScannedDevices){
                String name = device.device.getAddress();
                right = right||device.device.getAddress().equals(rightHaptic);
                left = left||device.device.getAddress().equals(leftHaptic);
            }
//            D0:E9:F6:51:FB:84
//            FC:AB:EC:4E:01:29
            if(left&&right){
//                mBluetoothManager.connect(baseContext, leftHaptic);
//                mBluetoothManager.connect(baseContext, rightHaptic);
                if(mAdapter!=null){
                    rightGatt = mBluetoothManager.retrieveGatt(rightHaptic, baseContext, false);
                    leftGatt = mBluetoothManager.retrieveGatt(leftHaptic, baseContext, false);
                }
                stopScanning();
            }
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt) {
        if(gatt==leftGatt){
            leftServicesFound = true;
        }else if(gatt==rightGatt){
            rightServicesFound = true;
        }
        sendBluetoothMessage("test","test");
    }

    @Override
    public void onConnecting() {

    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onDataAvailable(BluetoothGattDescriptor descriptor) {

    }

    @Override
    public void onDataAvailable(BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void resetBluetoothCompleted() {

    }

    @Override
    public void onReadRemoteRssi(int rssi) {

    }

    public boolean manageBluetoothAvailability(){
        boolean isEnabled = true;
        int permissionCheck = ContextCompat.checkSelfPermission(baseContext,
                Manifest.permission.BLUETOOTH);

        // Check Bluetooth HW status
        int errorMessageId = 0;
        final int bleStatus = BleUtils.getBleStatus(baseContext);
        switch (bleStatus) {
            case BleUtils.STATUS_BLE_NOT_AVAILABLE:
                isEnabled = false;
                break;
            case BleUtils.STATUS_BLUETOOTH_NOT_AVAILABLE: {
                isEnabled = false;      // it was already off
                break;
            }
            case BleUtils.STATUS_BLUETOOTH_DISABLED: {
                isEnabled = false;      // it was already off
                // if no enabled, launch settings dialog to enable it (user should always be prompted before automatically enabling bluetooth)
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                this.startActivityForResult(enableBtIntent, kActivityRequestCode_EnableBluetooth);
                // execution will continue at onActivityResult()
                break;
            }
        }
        if (errorMessageId != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(baseContext);
            AlertDialog dialog = builder.setMessage(errorMessageId)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        }

        return isEnabled;
    }
    private static boolean manageLocationServiceAvailabilityForScanning() {

        boolean areLocationServiceReady = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {        // Location services are only needed to be enabled from Android 6.0
            int locationMode = Settings.Secure.LOCATION_MODE_OFF;
            try {
                locationMode = Settings.Secure.getInt(baseContext.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            areLocationServiceReady = locationMode != Settings.Secure.LOCATION_MODE_OFF;

            if (!areLocationServiceReady) {
                //Locations don't work
            }
        }

        return areLocationServiceReady;
    }

    // region Helpers
    private static class BluetoothDeviceData {
        BluetoothDevice device;
        public int rssi;
        byte[] scanRecord;
        private String advertisedName;           // Advertised name
        private String cachedNiceName;
        private String cachedName;

        // Decoded scan record (update R.array.scan_devicetypes if this list is modified)
        static final int kType_Unknown = 0;
        static final int kType_Uart = 1;
        static final int kType_Beacon = 2;
        static final int kType_UriBeacon = 3;

        public int type;
        int txPower;
        ArrayList<UUID> uuids;

        String getName() {
            if (cachedName == null) {
                cachedName = device.getName();
                if (cachedName == null) {
                    cachedName = advertisedName;      // Try to get a name (but it seems that if device.getName() is null, this is also null)
                }
            }

            return cachedName;
        }

        String getNiceName() {
            if (cachedNiceName == null) {
                cachedNiceName = getName();
                if (cachedNiceName == null) {
                    cachedNiceName = device.getAddress();
                }
            }

            return cachedNiceName;
        }
    }
    //endregion
    private static void decodeScanRecords(BluetoothDeviceData deviceData) {
        // based on http://stackoverflow.com/questions/24003777/read-advertisement-packet-in-android
        final byte[] scanRecord = deviceData.scanRecord;

        ArrayList<UUID> uuids = new ArrayList<>();
        byte[] advertisedData = Arrays.copyOf(scanRecord, scanRecord.length);
        int offset = 0;
        deviceData.type = BluetoothDeviceData.kType_Unknown;

        // Check if is an iBeacon ( 0x02, 0x0x1, a flag byte, 0x1A, 0xFF, manufacturer (2bytes), 0x02, 0x15)
        final boolean isBeacon = advertisedData[0] == 0x02 && advertisedData[1] == 0x01 && advertisedData[3] == 0x1A && advertisedData[4] == (byte) 0xFF && advertisedData[7] == 0x02 && advertisedData[8] == 0x15;

        // Check if is an URIBeacon
        final byte[] kUriBeaconPrefix = {0x03, 0x03, (byte) 0xD8, (byte) 0xFE};
        final boolean isUriBeacon = Arrays.equals(Arrays.copyOf(scanRecord, kUriBeaconPrefix.length), kUriBeaconPrefix) && advertisedData[5] == 0x16 && advertisedData[6] == kUriBeaconPrefix[2] && advertisedData[7] == kUriBeaconPrefix[3];

        if (isBeacon) {
            deviceData.type = BluetoothDeviceData.kType_Beacon;

            // Read uuid
            offset = 9;
            UUID uuid = BleUtils.getUuidFromByteArrayBigEndian(Arrays.copyOfRange(scanRecord, offset, offset + 16));
            uuids.add(uuid);
            offset += 16;

            // Skip major minor
            offset += 2 * 2;   // major, minor

            // Read txpower
            final int txPower = advertisedData[offset++];
            deviceData.txPower = txPower;
        } else if (isUriBeacon) {
            deviceData.type = BluetoothDeviceData.kType_UriBeacon;

            // Read txpower
            final int txPower = advertisedData[9];
            deviceData.txPower = txPower;
        } else {
            // Read standard advertising packet
            while (offset < advertisedData.length - 2) {
                // Length
                int len = advertisedData[offset++];
                if (len == 0) break;

                // Type
                int type = advertisedData[offset++];
                if (type == 0) break;

                // Data
//            Log.d(TAG, "record -> lenght: " + length + " type:" + type + " data" + data);

                switch (type) {
                    case 0x02:          // Partial list of 16-bit UUIDs
                    case 0x03: {        // Complete list of 16-bit UUIDs
                        while (len > 1) {
                            int uuid16 = advertisedData[offset++] & 0xFF;
                            uuid16 |= (advertisedData[offset++] << 8);
                            len -= 2;
                            uuids.add(UUID.fromString(String.format("%08x-0000-1000-8000-00805f9b34fb", uuid16)));
                        }
                        break;
                    }

                    case 0x06:          // Partial list of 128-bit UUIDs
                    case 0x07: {        // Complete list of 128-bit UUIDs
                        while (len >= 16) {
                            try {
                                // Wrap the advertised bits and order them.
                                UUID uuid = BleUtils.getUuidFromByteArraLittleEndian(Arrays.copyOfRange(advertisedData, offset, offset + 16));
                                uuids.add(uuid);

                            } catch (IndexOutOfBoundsException e) {
                                Log.e("EyesFreeTrack", "BlueToothDeviceFilter.parseUUID: " + e.toString());
                            } finally {
                                // Move the offset to read the next uuid.
                                offset += 16;
                                len -= 16;
                            }
                        }
                        break;
                    }

                    case 0x09: {
                        byte[] nameBytes = new byte[len - 1];
                        for (int i = 0; i < len - 1; i++) {
                            nameBytes[i] = advertisedData[offset++];
                        }

                        String name = null;
                        try {
                            name = new String(nameBytes, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        deviceData.advertisedName = name;
                        break;
                    }

                    case 0x0A: {        // TX Power
                        final int txPower = advertisedData[offset++];
                        deviceData.txPower = txPower;
                        break;
                    }

                    default: {
                        offset += (len - 1);
                        break;
                    }
                }
            }

            // Check if Uart is contained in the uuids
            boolean isUart = false;
            for (UUID uuid : uuids) {
                if (uuid.toString().equalsIgnoreCase(UartInterfaceActivity.UUID_SERVICE)) {
                    isUart = true;
                    break;
                }
            }
            if (isUart) {
                deviceData.type = BluetoothDeviceData.kType_Uart;
            }
        }

        deviceData.uuids = uuids;
    }

    public static void sendBluetoothMessage(String bandName, String message){
        if(rightServicesFound){
            BluetoothGattService rightService = rightGatt.getService(UUID_SERVICE);
            String data = "!B11";
            ByteBuffer buffer = ByteBuffer.allocate(data.length()).order(java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.put(data.getBytes());
            byte[] data2 = buffer.array();
            byte checksum = 0;
            for (byte aData : data2) {
                checksum += aData;
            }
            checksum = (byte) (~checksum);
            byte dataCrc[] = new byte[data2.length + 1];
            System.arraycopy(data2, 0, dataCrc, 0, data2.length);
            dataCrc[data2.length] = checksum;


//            byte[] data = "!B11".getBytes(Charset.forName("UTF-8"));
            for (int i = 0; i < dataCrc.length; i += 20) {
//                final byte[] chunk = Arrays.copyOfRange(dataCrc, i, Math.min(i + 20, dataCrc.length));
//                BluetoothGattCharacteristic characteristic = rightService.getCharacteristic(UUID_TX);
//                characteristic.setValue(chunk);
//                Boolean check = leftGatt.writeCharacteristic(characteristic);
//                Log.e("adsf","asf");
                mBluetoothManager.altWriteService(rightGatt, rightService, UUID_TX.toString(), dataCrc);
                Log.e("written","written");
            }
        }
    }
}


