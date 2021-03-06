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
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.Recommender.RecommenderArtist;
import com.example.aaup8v2.aaup8v2.Recommender.RecommenderGenre;
import com.example.aaup8v2.aaup8v2.fragments.models.WifitDirectListAdapter;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.DeviceListFragment.DeviceActionListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
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
    public static final String DISCONNECT_SUCCESS = "disconnect_success";
    public static final String RECOMMENDER = "recommender";
    public static final String NEXT_SONG = "next_song";

    public static final String TAG = "wifidirect";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    public WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    private List<WifiP2pDevice> peersCollection = new ArrayList<>();

    public static  volatile ServerSocket serverSocket;
    private boolean interupt = false;

    ListView list;
    public List<String> ipsOnNetwork = new ArrayList<>();
    public Thread worker;
    WifitDirectListAdapter deviceAdapter;

    public Type mClassStringList = new TypeToken<List<String>>(){
    }.getType();

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
        this.info = MainActivity.mWifiDirectActivity.info;
        MainActivity.mWifiDirectActivity = this;

        // add intent values to be matched.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        // create wifip2pmanager and set the channel
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        deviceAdapter = new WifitDirectListAdapter(this,R.layout.listview_layout_p2p,peersCollection);

        // Assign adapter to ListView
        list = (ListView) findViewById(R.id.listviewPeers);
        list.setAdapter(deviceAdapter);

        // If trying to host, create a network with this device as groupowner, else search for other networks
        if(MainActivity.isHost){
            manager.createGroup(channel, new ActionListener() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getApplicationContext(), "Now hosting a group", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int reason) {
                    Toast.makeText(getApplicationContext(), "Failed to create Group", Toast.LENGTH_SHORT).show();
                }
            });
        }else
            discoverPeers();
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
        if (fragmentList != null) {
            fragmentList.clearPeers();
        }
    }

    // Starts a search for active networks, and lists them for the user to select from.
    public void discoverPeers(){
        // clear the list of networks
        peersCollection.clear();
        deviceAdapter.notifyDataSetChanged();

        // prompt user to turn on p2p if it is not enabled on the device
        if (!isWifiP2pEnabled) {
            Toast.makeText(WifiDirectActivity.this, R.string.p2p_off_warning,
                    Toast.LENGTH_SHORT).show();
        }
        final DeviceListFragment fragment = (DeviceListFragment) getFragmentManager()
                .findFragmentById(R.id.frag_list);
        fragment.onInitiateDiscovery();

        // start discovery of networks, returns with either success of failure
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

                            // making sure the list is empty
                            peersCollection.clear();

                            // adding all found devices to a list
                            List<WifiP2pDevice> list = new ArrayList<>();
                            list.addAll(peers.getDeviceList());

                            // Search the list for group owners, as these equal active networks
                            for(int i = 0; list.size() > i; i++){
                                if(list.get(i).isGroupOwner())
                                    peersCollection.add(list.get(i));
                            }
                            MainActivity.mWifiDirectActivity.deviceAdapter.notifyDataSetChanged();
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

    // refresh the network list
    public void refresh(View view){
        discoverPeers();
    }

    // Called when we have either created a network/group or when we successfully connect to a
    // network. This is where we call functions specifically for the host and peers.
    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {
        // Close any open progress dialogs.
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        // saving the info so we can access it elsewhere
        this.info = info;

        // if we are a group owner/host we start the receiving tread for the host, get the
        // recommender data and set the different button visibilities as needed.
        if (info.groupFormed && info.isGroupOwner) {
            receiveHostSpawn();
            MainActivity.mRecommend.extractUserInfo(MainActivity.mRecommend.userRecommendations);
            MainActivity.initializePeer(true);

            MainActivity.toggleConnectionButtons(false);
        } else if (info.groupFormed) {
            // we are a peer, so we send recommender data to host, set button visibility, and
            // start the receiving thread for peers.

            //Send track information to host for recommendation
            MainActivity.mRecommend.sendToHost();

            MainActivity.initializePeer(false);

            receiveDataSpawn();

            MainActivity.toggleConnectionButtons(false);
        }
        // closing the activity and returning to where we came in.
        finish();
    }

    // this is where we start the receiving thread for the host.
    public void receiveHostSpawn(){
        // checking whether the thread exists or is terminated so we don't cause crashes
        if (worker == null || !worker.isAlive()) {
            interupt = false;
            worker = new Thread(new Runnable(){

                // this runs on the UI thread so no heavy computation should be done here
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

                            // add the ip to our list of ips if it is not already there, and
                            // send the queue to the peer we just "met"
                            if (!ipsOnNetwork.contains(sender)) {
                                ipsOnNetwork.add(sender);
                                //when user first joins the network, they receive the queue back, peer-side handling as if track was added
                                if(MainActivity.mQueueFragment.mQueueElementList != null) {
                                    String queueListJoin = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
                                    sendDataToPeers(WifiDirectActivity.TRACK_ADDED, queueListJoin);
                                }
                            }

                            // depending on the type of data we receive, we handle it differently.
                            switch (type) {
                                case IP_SENT:
                                    break;
                                case UP_VOTE:
                                    // make the upvote for the peer and send out the resulting queue to everyone.
                                    List<String> dataList = gson.fromJson(data,mClassStringList);
                                    int position = MainActivity.mQueueFragment.checkPosition(Integer.parseInt(dataList.get(0)), dataList.get(1));
                                    if(position != -1) {
                                        MainActivity.mQueueFragment.upVoteAssist(position, sender);
                                        MainActivity.mQueueFragment.sortQueue();
                                    }
                                    String queueListUp = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
                                    sendDataToPeers(WifiDirectActivity.UP_VOTE, queueListUp);
                                    break;
                                case DOWN_VOTE:
                                    // make the downvote for the peer and send out the resulting queue to everyone.
                                    List<String> dataList2 = gson.fromJson(data,mClassStringList);
                                    int position2 = MainActivity.mQueueFragment.checkPosition(Integer.parseInt(dataList2.get(0)), dataList2.get(1));
                                    if(position2 != -1) {
                                        MainActivity.mQueueFragment.downVoteAssist(position2, sender);
                                        MainActivity.mQueueFragment.voteThreshold(position2);
                                        MainActivity.mQueueFragment.sortQueue();
                                    }
                                    String queueListDown = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
                                    sendDataToPeers(WifiDirectActivity.DOWN_VOTE, queueListDown);
                                    break;
                                case TRACK_ADDED:
                                    // add track to queue and send the queue to everyone.
                                    // if the track is already on the list due to synchronisation errors we don't add it
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
                                    // remove the peer from our internal list so they don't receive
                                    // anymore updates, and tell them they have disconnected.
                                    if(ipsOnNetwork.contains(sender))
                                        ipsOnNetwork.remove(sender);

                                    sendDataToPeer(DISCONNECT_SUCCESS, "", sender);

                                    break;
                                case RECOMMENDER:
                                    // receive and handle recommender data from peers
                                    Type recValPair = new TypeToken<Pair<List<RecommenderArtist>, List<RecommenderGenre>>>(){}.getType();
                                    Pair<List<RecommenderArtist>, List<RecommenderGenre>> userArtistGenres = gson.fromJson(data, recValPair);
                                    MainActivity.mRecommend.extractUserInfo(userArtistGenres);
                                    //MainActivity.mRecommend.getArtists();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }

                // constantly opens a receiving data socket and awaits a connection
                @Override
                public void run()
                {
                    Log.d(TAG, "Thread run()");
                    while (!interupt) {
                        serverSocket = null;
                        try {
                            serverSocket = new ServerSocket(8888);
                            Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                            Socket client = serverSocket.accept();
                            Log.d(WifiDirectActivity.TAG, "Server: connection done");
                            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                            Object type = objectInputStream.readObject();
                            Object object = objectInputStream.readObject();

                            // Finds the IP of the sender.
                            InetAddress senderAddress = client.getInetAddress();
                            String sender = senderAddress.toString().substring(1);

                            List<String> data = new ArrayList<>();
                            data.add((String) type);
                            data.add((String)object);
                            data.add(sender);
                            // send the received data to the UI thread
                            updateUI(data);

                        }catch (ClosedByInterruptException e){
                            e.getCause();
                        }catch (IOException | ClassNotFoundException e) {
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

    // this is where we start the receiving thread for the peer.
    public void receiveDataSpawn(){
        // checking whether the thread exists or is terminated so we don't cause crashes
        if(worker == null || !worker.isAlive()) {
            interupt = false;
            worker = new Thread(new Runnable() {

                // this runs on the UI thread so no heavy computation should be done here
                private void updateUI(final List<String> output) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String type = output.get(0);
                            String data = output.get(1);

                            Gson gson = new Gson();

                            Type mClassMyTrack = new TypeToken<myTrack>(){
                            }.getType();

                            Type mClassQueue = new TypeToken<List<QueueElement>>() {
                            }.getType();

                            switch (type) {
                                case UP_VOTE:
                                    // update queue
                                    List<QueueElement> newQueueListUp = gson.fromJson(data, mClassQueue);
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll(newQueueListUp);
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    break;
                                case DOWN_VOTE:
                                    // update queue
                                    List<QueueElement> newQueueListDown = gson.fromJson(data, mClassQueue);
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll(newQueueListDown);
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    break;
                                case TRACK_ADDED:
                                    // update queue, search, and playlist
                                    List<QueueElement> newQueueListAdd = gson.fromJson(data, mClassQueue);
                                    MainActivity.mQueueFragment.mQueueElementList.clear();
                                    MainActivity.mQueueFragment.mQueueElementList.addAll(newQueueListAdd);
                                    if(MainActivity.mQueueFragment.queueAdapter != null)
                                        MainActivity.mQueueFragment.queueAdapter.notifyDataSetChanged();
                                    if(MainActivity.mSearchFragment.searchAdapter != null)
                                        MainActivity.mSearchFragment.searchAdapter.notifyDataSetChanged();
                                    if(MainActivity.mPlaylistFragment.listAdapter != null)
                                        MainActivity.mPlaylistFragment.listAdapter.notifyDataSetChanged();
                                    break;
                                case NEXT_SONG:
                                    // new song started, so updating the playbar info
                                    myTrack track = gson.fromJson(data,mClassMyTrack);
                                    String artists = "Artists: ";
                                    for(int i = 0; track.artists.size() > i; i++) {
                                        artists += track.artists.get(i).name;
                                        if(track.artists.size() != (i+1))
                                            artists += "; ";
                                    }

                                    MainActivity.playedArtist.setText(artists);
                                    MainActivity.playedName.setText(track.name);
                                    MainActivity.mQueueFragment.deleteTrack(0);
                                    break;
                                case DISCONNECT:
                                    //Notify user the host left and handle it, makes sure everything is reset
                                    Toast.makeText(getApplicationContext(), "Host left network", Toast.LENGTH_LONG).show();
                                    disconnect();
                                    info = null;
                                    MainActivity.toggleConnectionButtons(true);
                                    MainActivity.cleanUp();
                                    break;
                                case DISCONNECT_SUCCESS:
                                    // host has recognized our disconnect so we are free to disconnect
                                    disconnect();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }

                // constantly opens a receiving data socket and awaits a connection
                @Override
                public void run() {
                    Log.d(TAG, "Thread run()");
                    while (!interupt) {
                        serverSocket = null;
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
                            // send the received data to the UI thread
                            updateUI(data);

                            objectInputStream.close();


                        }catch (ClosedByInterruptException e) {
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

    // disconnect from network and reset data as needed
    @Override
    public void disconnect() {
        if(manager != null) {
            manager.removeGroup(channel, new ActionListener() {
                @Override
                public void onFailure(int reasonCode) {
                    Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
                }

                @Override
                public void onSuccess() {
                    Log.d(TAG, "Disconnect succesfull");
                    info = null;
                    MainActivity.toggleConnectionButtons(true);
                }
            });
        }
        if(serverSocket != null) {
            Thread disconnect = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        interupt = true;
                        serverSocket.close();
                    } catch (IOException e) {
                        e.getCause();
                    }
                }
            });
            disconnect.setName("DisconnectThread");
            disconnect.start();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
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
    //@Override
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

    public void on_listViewClick(View view) {
        // These two lines are used to find out which line of the list the button is in.
        ListView listviewcontent = (ListView)view.getParent().getParent().getParent();
        int bIndex = listviewcontent.indexOfChild((View) view.getParent().getParent());

        // connect to the network at this index in the list
        WifiP2pConfig conf = new WifiP2pConfig();
        conf.deviceAddress =  peersCollection.get(bIndex).deviceAddress;
        conf.wps.setup = WpsInfo.PBC;
        connect(conf);
    }

    // starts a serviceIntent and stores the data to be send to the host.
    public void sendDataToHost(String type, String data){
        Intent serviceIntent = new Intent(this, HostTransferService.class);
        serviceIntent.setAction(HostTransferService.ACTION_SEND_DATA);
        serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                info.groupOwnerAddress.getHostAddress());
        serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_PORT, 8888);
        serviceIntent.putExtra(HostTransferService.EXTRAS_DATA, data);
        serviceIntent.putExtra(HostTransferService.EXTRAS_TYPE, type);
        startService(serviceIntent);
    }

    // for each ip on the network we prepare to send the data
    public void sendDataToPeers(String type, String data){
        if(type.equals(DISCONNECT) && ipsOnNetwork.isEmpty()) {
            disconnect();
        } else {
            for (int j = 0; j < ipsOnNetwork.size(); j++) {
                sendDataToPeer(type, data, ipsOnNetwork.get(j));
            }
        }
    }

    // start an intentservice and store the data to be send to each peer
    public void sendDataToPeer(String type, String data, String ip){
        Intent dataIntent = new Intent(this, DataTransferService.class);
        dataIntent.setAction(DataTransferService.ACTION_SEND_DATA);
        dataIntent.putExtra(DataTransferService.EXTRAS_PEER_ADDRESS, ip);
        dataIntent.putExtra(DataTransferService.EXTRAS_PEER_PORT, 8988);
        dataIntent.putExtra(DataTransferService.EXTRAS_DATA, data);
        dataIntent.putExtra(DataTransferService.EXTRAS_TYPE, type);
        startService(dataIntent);
    }
}


