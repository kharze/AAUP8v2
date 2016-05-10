package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;


public class DisconnectFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public DisconnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_disconnect, container,false);
        Button disconnectNetworkButton = (Button)v.findViewById(R.id.btn_disconnect_from_network);

        //Instantiate the connection buttons
        MainActivity.hostButton = (Button)v.findViewById(R.id.btn_host);
        MainActivity.connectButton = (Button)v.findViewById(R.id.btn_connectView);
        MainActivity.disconnectButton = disconnectNetworkButton;

        if(MainActivity.mWifiDirectActivity.info == null)
            MainActivity.toggleConnectionButtons(true);
        else
            MainActivity.toggleConnectionButtons(false);

        disconnectNetworkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Disconnecting", Toast.LENGTH_SHORT).show();
                if (MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner) {
                    MainActivity.mWifiDirectActivity.sendDataToPeers(WifiDirectActivity.DISCONNECT, "");
                } else if (MainActivity.mWifiDirectActivity.info != null) {
                    MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.DISCONNECT, "", MainActivity.mQueueFragment.myIP);
                }
                Toast.makeText(getContext(), "Disconnected", Toast.LENGTH_SHORT).show();
                MainActivity.toggleConnectionButtons(true);
                if(MainActivity.hasPremium)
                    MainActivity.initializePeer(true);
            }
        });

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
