package com.example.aaup8v2.aaup8v2.wifidirect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.DeviceListFragment.DeviceActionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WifiDirectActivity extends Activity implements ChannelListener, DeviceActionListener, WifiP2pManager.ConnectionInfoListener {
    public static final String IP_SENT = "ip_sent";
    public static final String TRACK_ADDED = "track_added";
    public static final String UP_VOTE = "up_vote";
    public static final String DOWN_VOTE = "down_vote";
    public static final String DISCONNECT = "disconnect";

    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    public WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    private List<WifiP2pDevice> peersCollection = new ArrayList();
    //Collection<WifiP2pDevice> peersCollection;
    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView list;
    public List<String> ipsOnNetwork = new ArrayList<>();
    public Thread worker;
    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifidirect);
        this.worker = MainActivity.mWifiDirectActivity.worker;
        this.ipsOnNetwork = MainActivity.mWifiDirectActivity.ipsOnNetwork;
        MainActivity.mWifiDirectActivity = this;
        // add necessary intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        //receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        list = (ListView) findViewById(R.id.listviewPeers);

    }
    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    public void resetData() {
        DeviceListFragment fragmentList = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        DeviceDetailFragment fragmentDetails = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
        if (fragmentDetails != null) {
            fragmentDetails.resetViews();
        }
    }
    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_items, menu);
        return true;
    }*/


    public void enableP2P(View view){
        if (manager != null && channel != null) {
            // Since this is the system wireless settings activity, it's
            // not going to send us a result. We will be notified by
            // WiFiDeviceBroadcastReceiver instead.
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        } else {
            Log.e(TAG, "channel or manager is null");
        }
    }

    public void discoverPeers(final View view){
        if (!isWifiP2pEnabled) {
            Toast.makeText(WifiDirectActivity.this, R.string.p2p_off_warning,
                    Toast.LENGTH_SHORT).show();
        }
        final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();
        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(WifiDirectActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();

                // request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (manager != null) {
                    manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {

                        public void onPeersAvailable(WifiP2pDeviceList peers) {
                            Log.d(TAG, String.format("PeerListListener: %d peers available, updating device list", peers.getDeviceList().size()));
                            fragment.onPeersAvailable(peers);

                            // DO WHATEVER YOU WANT HERE
                            // YOU CAN GET ACCESS TO ALL THE DEVICES YOU FOUND FROM peers OBJECT
                            aList.clear();

                            while (!peersCollection.isEmpty()) {
                                peersCollection.remove(0);
                            }

                            peersCollection.addAll(peers.getDeviceList());

                            for (int i = 0; i < peersCollection.size(); i++) {
                                HashMap<String, String> hm = new HashMap<String, String>();

                                String s = peersCollection.get(i).deviceName;
                                hm.put("txt", s);
                                aList.add(hm);
                            }


                            String[] from = {"flag", "txt", "cur"};

                            int[] to = {R.id.flag, R.id.txt, R.id.cur, R.id.textView};
                            SimpleAdapter a = new SimpleAdapter(view.getContext(), aList, R.layout.listview_layout_p2p, from, to);

                            // Assign adapter to ListView
                            list = (ListView) findViewById(R.id.listviewPeers);
                            list.setAdapter(a);

                        }
                    });

                }

            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(WifiDirectActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        this.info = info;

        if (info.groupFormed && info.isGroupOwner) {
            receiveHostSpawn();

            MainActivity.initializePeer(true);
        } else if (info.groupFormed) {
            sendDataToHost("ip_sent", "", MainActivity.mQueueFragment.myIP);

            MainActivity.initializePeer(false);

            receiveDataSpawn();
        }

    }

    public void receiveHostSpawn(){
        if (worker == null || !worker.isAlive()) {
            worker = new Thread(new Runnable(){

                private void updateUI(final List<String> output)
                {
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run()
                        {
                            String type = output.get(0);
                            String data = output.get(1);
                            String sender = output.get(2);
                            Gson gson = new Gson();

                            if (!ipsOnNetwork.contains(sender)) {
                                ipsOnNetwork.add(sender);
                            }

                            switch (type) {
                                case IP_SENT:
                                    break;
                                case UP_VOTE:
                                    MainActivity.mQueueFragment.mQueueElementList.get(Integer.parseInt(data)).upvoteList.add(sender);
                                    MainActivity.mQueueFragment.sortQueue();
                                    String queueListUp = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
                                    sendDataToPeers(WifiDirectActivity.UP_VOTE, queueListUp);
                                    break;
                                case DOWN_VOTE:
                                    MainActivity.mQueueFragment.mQueueElementList.get(Integer.parseInt(data)).downvoteList.add(sender);
                                    MainActivity.mQueueFragment.voteThreshold(Integer.parseInt(data));
                                    MainActivity.mQueueFragment.sortQueue();
                                    String queueListDown = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
                                    sendDataToPeers(WifiDirectActivity.DOWN_VOTE, queueListDown);
                                    break;
                                case TRACK_ADDED:
                                    Type mClass = new TypeToken<myTrack>() {
                                    }.getType();
                                    myTrack track = gson.fromJson(data, mClass);
                                    Boolean inList = false;
                                    for(int i = 0; MainActivity.mQueueFragment.mQueueElementList.size() > i; i++){
                                        if(MainActivity.mQueueFragment.mQueueElementList.get(i).track.id.equals(track.id)){
                                            inList = true;
                                            break;
                                        }
                                    }
                                    if(!inList){
                                        MainActivity.mQueueFragment.addTrack(track);

                                        String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);

                                        sendDataToPeers(TRACK_ADDED, queueList);
                                    }
                                    break;
                                case DISCONNECT:
                                    //Handle disconnect
                                    if(ipsOnNetwork.contains(sender))
                                        ipsOnNetwork.remove(sender);

                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }

                @Override
                public void run()
                {
                    Log.d(TAG, "Thread run()");
                    while (true) {
                        ServerSocket serverSocket = null;
                        try {
                            serverSocket = new ServerSocket(8888);
                            Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                            Socket client = serverSocket.accept();
                            Log.d(WifiDirectActivity.TAG, "Server: connection done");
                            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                            Object type = objectInputStream.readObject();
                            Object object = objectInputStream.readObject();
                            Object sender = objectInputStream.readObject();

                            List<String> data = new ArrayList<>();
                            data.add((String) type);
                            data.add((String)object);
                            data.add((String) sender);
                            updateUI(data);

                        }catch (ClosedByInterruptException e){
                            e.getCause();
                        }catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if(serverSocket != null)
                                    serverSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            });
            worker.setName("HostSpawn");
            worker.start();
        }

    }

    public void receiveDataSpawn(){
        if(worker == null || !worker.isAlive()) {
            worker = new Thread(new Runnable() {

                private void updateUI(final List<String> output) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String type = output.get(0);
                            String data = output.get(1);

                            Gson gson = new Gson();
                            Type mClass = new TypeToken<List<QueueElement>>() {
                            }.getType();

                            switch (type) {
                                case UP_VOTE:
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll((List<QueueElement>)gson.fromJson(data, mClass));
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    break;
                                case DOWN_VOTE:
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll((List<QueueElement>)gson.fromJson(data, mClass));
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    break;
                                case TRACK_ADDED:
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll((List<QueueElement>)gson.fromJson(data, mClass));
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    if(MainActivity.mSearchFragment.searchAdapter != null)
                                        MainActivity.mSearchFragment.searchAdapter.notifyDataSetChanged();
                                    break;
                                case DISCONNECT:
                                    Toast.makeText(getApplicationContext(), "Host left network", Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }


                @Override
                public void run() {
                    Log.d(TAG, "Thread run()");
                    while (true) {
                        ServerSocket serverSocket = null;
                        try {
                            serverSocket = new ServerSocket(8988);
                            Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                            Socket client = serverSocket.accept();
                            Log.d(WifiDirectActivity.TAG, "Server: connection done");

                            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                            Object type = objectInputStream.readObject();
                            Object object = objectInputStream.readObject();

                            List<String> data = new ArrayList<>();
                            data.add((String)type);
                            data.add((String)object);
                            updateUI(data);

                            objectInputStream.close();

                        }catch (ClosedByInterruptException e){
                            e.getCause();
                        }catch (IOException e) {
                            Log.e(WifiDirectActivity.TAG, e.getMessage());
                            //return null;
                        }catch (Exception e){
                            e.getCause();
                        } finally {
                            try {
                                if(serverSocket != null)
                                    serverSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

            });
            worker.setName("DataSpawn");
            worker.start();
        }
    }

    @Override
    public void showDetails(WifiP2pDevice device) {
        DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.showDetails(device);
    }
    @Override
    public void connect(WifiP2pConfig config) {
        manager.connect(channel, config, new ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(WifiDirectActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    public void disconnect() {
        //Add data communication that peer left the network.

        if(manager != null) {
            manager.removeGroup(channel, new ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {

                }
            });
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        /*if(worker != null)
            worker.interrupt();*/
    }
    @Override
    public void onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void cancelDisconnect() {
        /*
         * A cancel abort request by user. Disconnect i.e. removeGroup if
         * already connected. Else, request WifiP2pManager to abort the ongoing
         * request
         */
        if (manager != null) {
            final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            if (fragment.getDevice() == null
                    || fragment.getDevice().status == WifiP2pDevice.CONNECTED) {
                disconnect();
            } else if (fragment.getDevice().status == WifiP2pDevice.AVAILABLE
                    || fragment.getDevice().status == WifiP2pDevice.INVITED) {
                manager.cancelConnect(channel, new ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(WifiDirectActivity.this, "Aborting connection",
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reasonCode) {
                        Toast.makeText(WifiDirectActivity.this,
                                "Connect abort request failed. Reason Code: " + reasonCode,
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void on_listViewClik(View view) {
        // These two lines are used to find out which line of the list the button is in.
        ListView listviewconteasdnt = (ListView)view.getParent().getParent().getParent();
        int bIndex = listviewconteasdnt.indexOfChild((View) view.getParent().getParent());

        WifiP2pDevice dev = peersCollection.get(bIndex);
        WifiP2pConfig conf = new WifiP2pConfig();
        conf.groupOwnerIntent = 15;
        conf.deviceAddress =  peersCollection.get(bIndex).deviceAddress;
        conf.wps.setup = WpsInfo.PBC;
        connect(conf);
    }

    public void sendInfo(View view){


        ListView listviewconteasdnt = (ListView)view.getParent().getParent().getParent();
        int bIndex = listviewconteasdnt.indexOfChild((View) view.getParent().getParent());
        int index = 0;

        for(int i = 0; i < peersCollection.size(); i++){

            if (index == bIndex)
            {
                if (!info.isGroupOwner) {
                    sendDataToHost("", "I sent something", "");
                } else {
                    sendDataToPeers("", "I'm number " + bIndex);
                }

            }
            index++;
        }
    }

    public void sendDataToHost(String type, String data, String ip){
        Intent serviceIntent = new Intent(this, HostTransferService.class);
        serviceIntent.setAction(HostTransferService.ACTION_SEND_DATA);
        serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_PORT, 8888);
        serviceIntent.putExtra(HostTransferService.EXTRAS_DATA, data);
        serviceIntent.putExtra(HostTransferService.EXTRAS_TYPE, type);
        serviceIntent.putExtra(HostTransferService.EXTRAS_SENDER, ip);
        startService(serviceIntent);
    }

    public void sendDataToPeers(String type, String data){
        for(int j = 0; j < ipsOnNetwork.size(); j++){
            Intent dataIntent = new Intent(this, DataTransferService.class);
            dataIntent.setAction(DataTransferService.ACTION_SEND_DATA);
            dataIntent.putExtra(DataTransferService.EXTRAS_PEER_ADDRESS, ipsOnNetwork.get(j));
            dataIntent.putExtra(DataTransferService.EXTRAS_PEER_PORT, 8988);
            dataIntent.putExtra(DataTransferService.EXTRAS_DATA, data);
            dataIntent.putExtra(DataTransferService.EXTRAS_TYPE, type);
            startService(dataIntent);
        }
    }
}


