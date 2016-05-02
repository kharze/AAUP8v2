package com.example.aaup8v2.aaup8v2.fragments.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.myTrack;

import java.util.List;

/**
 * Created by Sean Skov Them on 02-05-2016.
 */
public class SearchListAdapter extends ArrayAdapter<myTrack> {

    public SearchListAdapter(Context context, int resource, List<myTrack> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.listview_search_layout, null);
        }

        myTrack element = getItem(position);

        if (element != null) {
            // Creates all the views
            TextView trackName = (TextView) v.findViewById(R.id.trackName);
            TextView artistName = (TextView) v.findViewById(R.id.artist);
            ImageView addTrack = (ImageView) v.findViewById(R.id.add_track);

            if (trackName != null)
                trackName.setText(element.name);

            if (artistName != null)
                artistName.setText("Artist: " + element.artist);

            if(addTrack != null){
                boolean inList = false;

                for(int j = 0; MainActivity.mQueueFragment.mQueueElementList.size() > j; j++){
                    if(MainActivity.mQueueFragment.mQueueElementList.get(j).track.id.equals(element.id)){
                        inList = true;
                        break;
                    }
                }
                if(!inList) {
                    int add_track = R.drawable.ic_playlist_add;
                    addTrack.setImageResource(add_track);
                    addTrack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Track added to queue", Toast.LENGTH_SHORT).show();
                            MainActivity.mSearchFragment.click_search_add_track(position);
                        }
                    });
                } else {
                    int add_track_check = R.drawable.ic_playlist_add_check;
                    addTrack.setImageResource(add_track_check);
                    addTrack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getContext(), "Track already on queue", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        }

        return v;
    }
}
