package com.example.aaup8v2.aaup8v2.wifidirect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.DataSetObserver;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncDataTransfer;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGatherNetworkDevices;
import com.example.aaup8v2.aaup8v2.wifidirect.DeviceListFragment.DeviceActionListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static android.app.PendingIntent.getActivity;

/**
 * An activity that uses WiFi Direct APIs to discover and connect with available
 * devices. WiFi Direct APIs are asynchronous and rely on callback mechanism
 * using interfaces to notify the application of operation success or failure.
 * The application should also register a BroadcastReceiver for notification of
 * WiFi state related events.
 */
public class WifiDirectActivity extends Activity implements ChannelListener, DeviceActionListener, WifiP2pManager.ConnectionInfoListener {
    public static final String TAG = "wifidirectdemo";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;
    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    private WifiP2pInfo info;
    ProgressDialog progressDialog = null;
    private List<WifiP2pDevice> peersCollection = new ArrayList();
    //Collection<WifiP2pDevice> peersCollection;
    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView list;
    List<String> ipsOnNetwork = new ArrayList<>();
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

                                while(!peersCollection.isEmpty()){
                                    peersCollection.remove(0);
                                }

                                peersCollection.addAll(peers.getDeviceList());

                                for(int i = 0; i < peersCollection.size(); i++){
                                    HashMap<String, String> hm = new HashMap<String,String>();

                                    String s = peersCollection.get(i).deviceName;
                                    hm.put("txt", s);
                                    //hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                                    //hm.put("flag", Integer.toString(flags[5]));
                                    aList.add(hm);
                                }

                                /*for(Iterator<WifiP2pDevice> i = peersCollection.iterator(); i.hasNext();){
                                    HashMap<String, String> hm = new HashMap<String,String>();

                                    String s = i.next().deviceName;
                                    hm.put("txt", s);
                                    //hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                                    //hm.put("flag", Integer.toString(flags[5]));
                                    aList.add(hm);
                                }*/

                                String[] from = { "flag","txt","cur" };

                                int[] to = { R.id.flag,R.id.txt,R.id.cur,R.id.textView};
                                SimpleAdapter a = new SimpleAdapter(view.getContext(),aList,R.layout.listview_layout_p2p,from,to);

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
        //this.getView().setVisibility(View.VISIBLE);
        // The owner IP is now known.
        //TextView view = (TextView) mContentView.findViewById(R.id.group_owner);
        //view.setText(getResources().getString(R.string.group_owner_text)
        // + ((info.isGroupOwner == true) ? getResources().getString(R.string.yes)
        // : getResources().getString(R.string.no)));
        // InetAddress from WifiP2pInfo struct.
        //view = (TextView) mContentView.findViewById(R.id.device_info);
        //view.setText("Group Owner IP - " + info.groupOwnerAddress.getHostAddress());
        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            //new asyncDataTransfer(this).execute();
            new asyncGatherNetworkDevices(new asyncGatherNetworkDevices.AsyncResponse() {
                @Override
                public void processFinish(String output) {
                    if (!ipsOnNetwork.contains(output)){
                        ipsOnNetwork.add(output);
                    }
                }
            }).execute();
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            //mContentView.findViewById(R.id.btn_start_client).setVisibility(View.VISIBLE);
            //((TextView) mContentView.findViewById(R.id.status_text)).setText(getResources()
            // .getString(R.string.client_text));
        }
        // hide the connect button
        //mContentView.findViewById(R.id.btn_connect).setVisibility(View.GONE);
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
        final DeviceDetailFragment fragment = (DeviceDetailFragment) getFragmentManager()
                .findFragmentById(R.id.frag_detail);
        fragment.resetViews();
        manager.removeGroup(channel, new ActionListener() {
            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "Disconnect failed. Reason :" + reasonCode);
            }

            @Override
            public void onSuccess() {
                fragment.getView().setVisibility(View.GONE);
            }
        });
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
        int index = 0;


        for(int i = 0; i < peersCollection.size(); i++){

            if (index == bIndex)
            {
                //String s = i.next().deviceName;
                WifiP2pDevice dev = peersCollection.get(i);
                WifiP2pConfig conf = new WifiP2pConfig();
                conf.deviceAddress =  peersCollection.get(i).deviceAddress;
                conf.wps.setup = WpsInfo.PBC;
                connect(conf);
                break;
            }
            index++;
        }
    }

    public void sendInfo(View view){


        ListView listviewconteasdnt = (ListView)view.getParent().getParent().getParent();
        int bIndex = listviewconteasdnt.indexOfChild((View) view.getParent().getParent());
        int index = 0;

        for(int i = 0; i < peersCollection.size(); i++){

            if (index == bIndex)
            {
                Intent serviceIntent = new Intent(this, IPTransferService.class);
                serviceIntent.setAction(IPTransferService.ACTION_SEND_FILE);
                serviceIntent.putExtra(IPTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(IPTransferService.EXTRAS_GROUP_OWNER_PORT, 8888);
                startService(serviceIntent);

                /*Intent serviceIntent = new Intent(this, DataTransferService.class);
                serviceIntent.setAction(DataTransferService.ACTION_SEND_FILE);
                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(DataTransferService.EXTRAS_GROUP_OWNER_PORT, 8988);
                startService(serviceIntent);
                break;*/
            }
            index++;
        }
    }
}
