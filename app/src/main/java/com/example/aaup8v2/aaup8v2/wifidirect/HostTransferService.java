package com.example.aaup8v2.aaup8v2.wifidirect;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class HostTransferService extends IntentService {
    private static final int SOCKET_TIMEOUT = 1000;
    public static final String ACTION_SEND_DATA = "com.example.aaup8v2.aaup8v2.wifidirect.SEND_DATA";
    public static final String EXTRAS_GROUP_OWNER_ADDRESS = "go_host";
    public static final String EXTRAS_GROUP_OWNER_PORT = "go_port";
    public static final String EXTRAS_DATA = "data_to_send";
    public static final String EXTRAS_TYPE = "type_to_send";
    public HostTransferService(String name) {
        super(name);
    }
    public HostTransferService() {
        super("DataTransferService");
    }
    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_SEND_DATA)) {
            String host = intent.getExtras().getString(EXTRAS_GROUP_OWNER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_GROUP_OWNER_PORT);
            String type = intent.getExtras().getString(EXTRAS_TYPE);
            String data = intent.getExtras().getString(EXTRAS_DATA);
            try {
                Log.d(WifiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WifiDirectActivity.TAG, "Client socket - " + socket.isConnected());

                // save our ip address, so we can use it later
                InetAddress senderAddress = socket.getLocalAddress();
                MainActivity.mQueueFragment.myIP = senderAddress.toString().substring(1);

                OutputStream stream = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                oos.writeObject(type);
                oos.writeObject(data);
                oos.close();
                socket.close();

                Log.d(WifiDirectActivity.TAG, "Client: Data sent");
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
                // we have attempted to tell the host we are disconnecting, so lets disconnect
                if(type != null && type.equals(WifiDirectActivity.DISCONNECT))
                    MainActivity.mWifiDirectActivity.disconnect();
            }
        }
    }
}
