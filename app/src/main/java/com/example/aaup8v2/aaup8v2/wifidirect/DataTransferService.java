package com.example.aaup8v2.aaup8v2.wifidirect;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.myTrack;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
public class DataTransferService extends IntentService {
    private static final int SOCKET_TIMEOUT = 5000;
    public static final String ACTION_SEND_DATA = "com.example.aaup8v2.aaup8v2.wifidirect.SEND_DATA";
    public static final String EXTRAS_FILE_PATH = "file_url";
    public static final String EXTRAS_PEER_ADDRESS = "go_host";
    public static final String EXTRAS_PEER_PORT = "go_port";
    public static final String EXTRAS_DATA = "data_to_send";
    public static final String EXTRAS_TYPE = "data_type";
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
        Context context = getApplicationContext();
        if (intent.getAction().equals(ACTION_SEND_DATA)) {
            String fileUri = intent.getExtras().getString(EXTRAS_FILE_PATH);
            String host = intent.getExtras().getString(EXTRAS_PEER_ADDRESS);
            Socket socket = new Socket();
            int port = intent.getExtras().getInt(EXTRAS_PEER_PORT);
            String data = intent.getExtras().getString(EXTRAS_DATA);
            String dataType = intent.getExtras().getString(EXTRAS_TYPE);
            try {
                Log.d(WifiDirectActivity.TAG, "Opening client socket - ");
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
                Log.d(WifiDirectActivity.TAG, "Client socket - " + socket.isConnected());
                OutputStream stream = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(stream);
                oos.writeObject(dataType);
                oos.writeObject(data);
                /*ContentResolver cr = context.getContentResolver();
                InputStream is = null;
                try {
                    is = cr.openInputStream(Uri.parse(fileUri));
                } catch (FileNotFoundException e) {
                    Log.d(WifiDirectActivity.TAG, e.toString());
                }
                DeviceDetailFragment.copyFile(is, stream);*/
                Log.d(WifiDirectActivity.TAG, "Client: Data written");
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
            }
        }
    }
}
