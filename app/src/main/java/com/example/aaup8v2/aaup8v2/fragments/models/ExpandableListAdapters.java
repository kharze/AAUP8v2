package com.example.aaup8v2.aaup8v2.fragments.models;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.myTrack;

import java.util.List;

/**
 * Created by lasse on 28-04-2016.
 */
public class ExpandableListAdapters extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private List<List<myTrack>> _listDataChild;

    public ExpandableListAdapters(Context context, List<String> listDataHeader, List<List<myTrack>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public myTrack getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(groupPosition).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview_layout, null);
        }

        TextView trackName = (TextView) convertView.findViewById(R.id.trackName);
        TextView artistName = (TextView) convertView.findViewById(R.id.artist);
        ImageView addTrack = (ImageView) convertView.findViewById(R.id.add_track);

        final myTrack track = getChild(groupPosition, childPosition);

        if(trackName != null) {
            final String nameText = track.name;
            trackName.setText(nameText);
        }

        if(artistName != null) {
            final String artistText = "Artist: " + track.artist;
            artistName.setText(artistText);
        }

        if(addTrack != null){
            boolean inList = false;

            for(int j = 0; MainActivity.mQueueFragment.mQueueElementList.size() > j; j++){
                if(MainActivity.mQueueFragment.mQueueElementList.get(j).track.id.equals(track.id)){
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
                        Toast.makeText(_context, "Track added to queue", Toast.LENGTH_SHORT).show();
                        MainActivity.mPlaylistFragment.click_playlist_add_track(groupPosition, childPosition);
                    }
                });
            } else {
                int add_track_check = R.drawable.ic_playlist_add_check;
                addTrack.setImageResource(add_track_check);
                addTrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(_context, "Track already on queue", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listview_group, null);
        }

        String headerTitle = (String) getGroup(groupPosition);

        TextView playlistName = (TextView) convertView.findViewById(R.id.playlist_name);

        if (playlistName != null) {
            playlistName.setTypeface(null, Typeface.BOLD);
            playlistName.setText(headerTitle);

            if(isExpanded){
                int expandedColor = _context.getResources().getColor(R.color.colorAccent);
                playlistName.setBackgroundColor(expandedColor);
            }
            else
                playlistName.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) { return true; }
}
