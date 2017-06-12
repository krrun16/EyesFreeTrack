/*
package com.example.krector.wifip2p;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static android.R.attr.port;

*/
/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 *//*

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    String host;
    int port = 8888;
    int len;
    Socket socket = new Socket();
    byte buf[]  = new byte[1024];

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
        MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Log.d("p2p", "Wifi P2P is enabled");
                mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("peers", "successful discovery!");
                    }

                    @Override
                    public void onFailure(int reasonCode) {
                        Log.d("peers", "failed discovery!");
                    }
                });
            } else {
                // Wi-Fi P2P is not enabled
                Log.d("p2p", "Wifi P2P is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            Log.d("broadcaster", "devices connected!");
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

        }
    }


    public void sendText(String host, int port) {
        try {
            */
/**
             * Create a client socket with the host,
             * port, and timeout information.
             *//*

            if(!socket.isBound()) {
                socket.bind(null);
            }
            socket.connect((new InetSocketAddress(host, port)), 500);

            */
/**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             *//*

            String textData = "text";
            int mArraySize = 1024;
            byte[] data = new byte[mArraySize];
            data = textData.getBytes();
            OutputStream outputStream = socket.getOutputStream();
            int count = data.length;
            outputStream.write(data, 0, count);
            outputStream.close();
        } catch (Exception e) {
            //catch logic
            e.printStackTrace();
        }

        */
/**
         * Clean up any open sockets when done
         * transferring or if an exception occurred.
         *//*
 finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
    }

    private byte[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
                            return inetAddress.getAddress();
                        }
                        //return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        } catch (NullPointerException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        }
        return null;
    }

    private String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i=0; i<ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i]&0xFF;
        }
        return ipAddrStr;
    }




    private PeerListListener peerListListener = new PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {
            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);

                Log.d("phones", "multiple peers");

                if (peers.size() != 0) {
                    // Picking the first device found on the network.
                    final WifiP2pDevice device = peers.get(0);

                    WifiP2pConfig config = new WifiP2pConfig();
                    config.deviceAddress = device.deviceAddress;
                    Log.d("address", device.deviceAddress);
                    config.wps.setup = WpsInfo.PBC;

                    mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                        @Override
                        public void onSuccess() {
                            // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
                            Log.d("devices", "connected to device!");
                            sendText(, 8888);
                        }

                        @Override
                        public void onFailure(int reason) {
                            Log.d("devices", "Connect failed. Retry.");
                        }
                    });
                }
            }

            if (peers.size() == 0) {
                Log.d("peers", "No devices found");
                return;
            }
        }
    };
}*/
