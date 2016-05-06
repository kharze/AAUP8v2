package com.example.aaup8v2.aaup8v2.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistTracksRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.ThreadResponseInterface;
import com.example.aaup8v2.aaup8v2.fragments.models.ExpandableListAdapters;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayListFragment extends Fragment{
    public ExpandableListView expListView;
    public List<String> playlistName;
    public List<List<myTrack>> listDataChild;
    public ExpandableListAdapters listAdapter;
    Activity activity;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {}

    public PlayListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        //Made to only update the playlist once in a runtime.
        if(listDataChild == null || playlistName == null){
            listDataChild = new ArrayList<>();
            playlistName = new ArrayList<>();
            getPlaylists();
        }

        final View v = inflater.inflate(R.layout.fragment_play_list, container,false);

        listAdapter = new ExpandableListAdapters(getContext(), playlistName, listDataChild);

        expListView = (ExpandableListView) v.findViewById(R.id.expand_list);

        expListView.setAdapter(listAdapter);

        // Inflate the layout for this fragment
        return v;
    }

    public void getPlaylists(){
        GetPlaylistsRunnable runnable = new GetPlaylistsRunnable(MainActivity.me.id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistSimple>>() {
            @Override
            public void processFinish(Pager<PlaylistSimple> output) {
                final List<List<myTrack>> tracksLists = new ArrayList<>();
                for (int i = 0; i < output.items.size(); i++){
                    playlistName.add(output.items.get(i).name);

                    new GetPlaylistTracksRunnable(output.items.get(i).owner.id, output.items.get(i).id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistTrack>>() {
                        @Override
                        public void processFinish(Pager<PlaylistTrack> tracks) {

                                final List<myTrack> aList = new ArrayList<>();
                                for(int i=0;i < tracks.items.size(); i++) {
                                    myTrack track = new myTrack(tracks.items.get(i));
                                    aList.add(track);
                                }
                                tracksLists.add(aList);
                        }
                    }).run();
                }

                for(int i = 0; i < playlistName.size(); i++){
                    listDataChild.add(tracksLists.get(i));
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(listAdapter != null)
                            listAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        Thread worker = new Thread(runnable);
        worker.setName("Playlist builder");
        worker.start();
    }

    public void click_playlist_add_track(int groupPosition, int childPosition){
        Gson gson = new Gson();

        if(MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner){
            MainActivity.mQueueFragment.addTrack(listDataChild.get(groupPosition).get(childPosition));
            String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
            MainActivity.mWifiDirectActivity.sendDataToPeers(WifiDirectActivity.TRACK_ADDED, queueList);
            if(listAdapter != null)
                listAdapter.notifyDataSetChanged();
        }
        else if (MainActivity.mWifiDirectActivity.info != null){
            String track = gson.toJson(listDataChild.get(groupPosition).get(childPosition));

            MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.TRACK_ADDED, track, MainActivity.mQueueFragment.myIP);
        }
        else{ //in case we aren't connected to a network, we just add it as a jukebox.
            MainActivity.mQueueFragment.addTrack(listDataChild.get(groupPosition).get(childPosition));
            if(listAdapter != null)
                listAdapter.notifyDataSetChanged();
        }
    }
}
