package com.example.aaup8v2.aaup8v2.wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class DataTransferService extends IntentService {
    private static final int SOCKET_TIMEOUT = 1000;
    public static final String ACTION_SEND_DATA = "com.example.aaup8v2.aaup8v2.wifidirect.SEND_DATA";
    public static final String EXTRAS_PEER_ADDRESS = "go_host";
    public static final String EXTRAS_PEER_PORT = "go_port";
    public static final String EXTRAS_DATA = "data_to_send";
    public static final String EXTRAS_TYPE = "data_type";
    private static HashMap<String, Long> timeOutFailed = new HashMap<>();
    public DataTransferService(String name) {
        super(name);
    }
    public DataTransferService() { super("DataTransferService");}
    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_SEND_DATA)) {
            String peer = intent.getExtras().getString(EXTRAS_PEER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_PEER_PORT);
            String data = intent.getExtras().getString(EXTRAS_DATA);
            String dataType = intent.getExtras().getString(EXTRAS_TYPE);
            try {
                Log.d(WifiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                // Attempt to connect to the listening socket at the peer.
                socket.connect((new InetSocketAddress(peer, port)), SOCKET_TIMEOUT);
                Log.d(WifiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                oos.writeObject(dataType);
                oos.writeObject(data);
                Log.d(WifiDirectActivity.TAG, "Client: Data written");
                // We have been connected to the peer, so we remove the flag
                timeOutFailed.remove(peer);
            } catch (SocketTimeoutException e){
                // If the host fails to connect to the peer, we flag the peer as potentially disconnected
                if(!timeOutFailed.containsKey(peer))
                    timeOutFailed.put(peer, (System.currentTimeMillis()/1000));
                else if((timeOutFailed.get(peer)+300) < (System.currentTimeMillis()/1000)){
                    MainActivity.mWifiDirectActivity.ipsOnNetwork.remove(peer);
                }
            } catch (IOException e) {
                Log.e(WifiDirectActivity.TAG, e.getMessage());
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
                // remove peer from our list of ips, and if we are the last one left, close the network
                // should be removed
                if(dataType != null && dataType.equals(WifiDirectActivity.DISCONNECT)) {
                    MainActivity.mWifiDirectActivity.ipsOnNetwork.remove(peer);
                    if(MainActivity.mWifiDirectActivity.ipsOnNetwork.isEmpty())
                        MainActivity.mWifiDirectActivity.disconnect();
                }
            }
        }
    }
}
