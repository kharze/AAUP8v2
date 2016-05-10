package com.example.aaup8v2.aaup8v2.fragments.models;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.aaup8v2.aaup8v2.R;

import java.util.List;

/**
 * Created by Mike on 10-05-2016.
 */
public class WifitDirectListAdapter extends ArrayAdapter<WifiP2pDevice>{

    public WifitDirectListAdapter(Context context, int resource, List<WifiP2pDevice> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_layout_p2p, null);
        }

        WifiP2pDevice element = getItem(position);

        if (element != null) {
            // Creates all the views
            TextView deviceName  = (TextView)  v.findViewById(R.id.deviceName);
            Button btnConnect = (Button) v.findViewById(R.id.buttonDeviceConnect);

            deviceName.setText(element.deviceName);
        }
        return v;
    }
}

