package com.example.aaup8v2.aaup8v2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;


public class PeerToPeer extends AppCompatActivity {

    public static final String TAG = PeerToPeer.class.getSimpleName();
    private IntentFilter mIntentFilter = new IntentFilter();

    private PeerToPeer mActivity = this;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;

    WifiP2pDevice device;
    WifiP2pConfig config = new WifiP2pConfig();
    WifiP2pDeviceList deviceList;

    private List peers = new ArrayList();

    String groupOwnerAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peer_to_peer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //  Indicates a change in the Wi-Fi P2P status.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this, peerListListener, connectionInfoListener);
        registerReceiver(mReceiver, mIntentFilter);
    }

    /* unregister the broadcast receiver */
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            // Out with the old, in with the new.
            if (peerList.getDeviceList().isEmpty()){
                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
                dlgAlert.setMessage("No divices available");
                dlgAlert.setTitle("Peer-To-Peer");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
            peers.clear();
            peers.addAll(peerList.getDeviceList());

        }
    };

    //For group handling
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
        @Override
        public void onConnectionInfoAvailable(final WifiP2pInfo info) {

            // InetAddress from WifiP2pInfo struct.
            //InetAddress groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

            // After the group negotiation, we can determine the group owner.
            if (info.groupFormed && info.isGroupOwner) {
                // Do whatever tasks are specific to the group owner.
                // One common case is creating a server thread and accepting
                // incoming connections.

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
                dlgAlert.setMessage("I'm the owner");
                dlgAlert.setTitle("Peer-To-Peer");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();

            } else if (info.groupFormed) {
                // The other device acts as the client. In this case,
                // you'll want to create a client thread that connects to the group
                // owner.

                AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
                dlgAlert.setMessage("I'm just here for fun");
                dlgAlert.setTitle("Peer-To-Peer");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }
        }
    };


    //Flag to see if Wifi is enabled, used in P2PReceiver.
    static boolean isWifiEnabled;

    static public void setIsWifiP2pEnabled(boolean isEnabled) {
        isWifiEnabled = isEnabled;
    }

    public void search(View view) {
        //Find Peers
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mActivity);
                dlgAlert.setMessage("Discovery Success");
                dlgAlert.setTitle("Peer-To-Peer");
                dlgAlert.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //dismiss the dialog
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the User that something went wrong.
            }
        });
    }

    public void connect(View view) {
        if(peers.size() == 0){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
            dlgAlert.setMessage("Peer list empty because I am shit");
            dlgAlert.setTitle("Peer-To-Peer");
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                        }
                    });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }

        try {
            // Picking the first device found on the network.
            device = (WifiP2pDevice) peers.get(0);

            config = new WifiP2pConfig();
            config.deviceAddress = device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
                    dlgAlert.setMessage("Eureka");
                    dlgAlert.setTitle("Peer-To-Peer");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                }
                            });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(mActivity, "Connect failed. Retry.",
                            Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {

        }
    }

    //Cancel, interupt or remove all connections
    public void interupt(View view){
        //Cancel ongoing connection
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(int reason) {
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mManager.clearServiceRequests(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(int reason) {
                }
            });
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mManager.stopPeerDiscovery(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                }
                @Override
                public void onFailure(int reason) {
                }
            });
        }
    }



    public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;
        private PeerToPeer mActivity;
        private WifiP2pManager.PeerListListener mPeerListListener;
        private WifiP2pManager.ConnectionInfoListener mConnectionInfoListener;

        public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                           PeerToPeer activity, WifiP2pManager.PeerListListener peerListListener,
                                           WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
            super();
            this.mManager = manager;
            this.mChannel = channel;
            this.mActivity = activity;
            this.mPeerListListener = peerListListener;
            this.mConnectionInfoListener = connectionInfoListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    mActivity.setIsWifiP2pEnabled(true);
                } else {
                    mActivity.setIsWifiP2pEnabled(false);
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                if (mManager != null) {
                    mManager.requestPeers(mChannel, mPeerListListener);
                    Log.d(mActivity.TAG, "P2P peers changed");
                }
                else{
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mActivity);
                    dlgAlert.setMessage("Didn't update");
                    dlgAlert.setTitle("Peer-To-Peer");
                    dlgAlert.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //dismiss the dialog
                                }
                            });
                    dlgAlert.setCancelable(true);
                    dlgAlert.create().show();
                }
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                if (mManager == null) {
                    return;
                }

                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

                if (networkInfo.isConnected()) {

                    // We are connected with the other device, request connection
                    // info to find group owner IP

                    mManager.requestConnectionInfo(mChannel, mConnectionInfoListener);
                }
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                // Respond to this device's wifi state changing
            }
        }

    }


    public void receive(View view){
        Context context = this.getApplicationContext();

        new TestConnectionReceive(context).execute();

        //new FileServerAsyncTask(context, mActivity).execute();

        //new FileServerAsyncTask(context);
    }

    public void send(View view){
        Context context = this.getApplicationContext();
        //mManager.requestConnectionInfo(mChannel, connectionInfoListener);

        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        WifiInfo mWifiInfo = wifiManager.getConnectionInfo();
        Log.e("IP in Mask Integer", mWifiInfo.getIpAddress()+"");
        Log.e("IP Address", intToIP(mWifiInfo.getIpAddress()) + "");

        EditText temp = (EditText) findViewById(R.id.editText);
        String some = temp.getText().toString();

        new TestConnectionSend(context, groupOwnerAddress).execute();

        //new FileTransferAsyncTask(context, groupOwnerAddress).execute();

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        //StrictMode.setThreadPolicy(policy);

    }

    public String intToIP(int i) {
        return (( i & 0xFF)+ "."+((i >> 8 ) & 0xFF)+
                "."+((i >> 16 ) & 0xFF)+"."+((i >> 24 ) & 0xFF));
    }


    public static class TestConnectionSend extends AsyncTask{

        private Context context;
        private String host;

        public TestConnectionSend(Context context, String host){
            this.context = context;
            this.host = host;
        }

        @Override
        protected Object doInBackground(Object[] params) {


            int port = 8888;
            host = "127.0.0.1";

            try {
                Socket socket = new Socket();
                socket.setReuseAddress(true);
                socket.connect((new InetSocketAddress(host, port)), 5000);
                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(os);
                oos.writeObject(new String("BROFIST"));
                oos.close();
                os.close();
                socket.close();
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

    }

    public static class TestConnectionReceive extends AsyncTask{

        private Context context;

        public TestConnectionReceive(Context context){
            this.context = context;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            int port = 8888;

            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                Socket client = serverSocket.accept();
                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Object object = objectInputStream.readObject();
                if (object.getClass().equals(String.class) && ((String) object).equals("BROFIST")) {
                    Log.d(TAG, "Client IP address: "+client.getInetAddress());
                }
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    public static class FileTransferAsyncTask extends AsyncTask {

        private Context context;
        private String host;

        public FileTransferAsyncTask(Context context, String host){
            this.context = context;
            this.host = host;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            int port = 8888;
            int len;
            Socket socket = new Socket();
            byte buf[]  = new byte[1024];

            try{
                /**
                 * Create a client socket with the host,
                 * port, and timeout information.
                 */
                socket.bind(null);
                socket.connect((new InetSocketAddress(host, port)), 500);

                /**
                 * Create a byte stream from a JPEG file and pipe it to the output stream
                 * of the socket. This data will be retrieved by the server device.
                 */
                OutputStream outputStream = socket.getOutputStream();
                ContentResolver cr = context.getContentResolver();
                InputStream inputStream = null;
                //inputStream = cr.openInputStream(Uri.parse("storage/emulated/0/Pictures/Screenshots/Screenshot_2015-06-22-12-34-09.png"));
                inputStream = cr.openInputStream(Uri.parse("storage/emulated/0/DCIM/Camera/20160318_090258.jpg"));
                while ((len = inputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                outputStream.close();
                inputStream.close();
                return null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            /**
             * Clean up any open sockets when done
             * transferring or if an exception occurred.
             */
            finally {
                if (socket != null) {
                    if (socket.isConnected()) {
                        try {
                            socket.close();
                            return null;
                        } catch (IOException e) {
                            //catch logic
                            return null;
                        }
                    }
                }
            }
        }
    }


    public static class FileServerAsyncTask extends AsyncTask {

        private Context context;
        private Activity msActivity;

        public FileServerAsyncTask(Context context, Activity activity) {
            this.context = context;
            this.msActivity = activity;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(8888);
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 * Save the input stream from the client as a JPEG file
                 */
                final File f = new File(Environment.getExternalStorageDirectory() + "/"
                        + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
                        + ".jpg");

                File dirs = new File(f.getParent());
                if (!dirs.exists())
                    dirs.mkdirs();
                f.createNewFile();
                InputStream inputstream = client.getInputStream();
                copyFile(inputstream, new FileOutputStream(f));
                serverSocket.close();
                return f.getAbsolutePath();
            } catch (IOException e) {
                Log.e(PeerToPeer.TAG, e.getMessage());
                return null;
            }
        }

        /**
         * Start activity that can handle the JPEG image
         */
        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse("file://" + result), "image/*");
                context.startActivity(intent);
            }
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
            Log.d(PeerToPeer.TAG, e.toString());
            return false;
        }
        return true;
    }
}