package com.example.aaup8v2.aaup8v2.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylists;
import com.example.aaup8v2.aaup8v2.fragments.models.ExpandableListAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayListFragment extends Fragment{
    ExpandableListAdapters listAdapter;
    public ExpandableListView expListView;
    //List<List<HashMap<String, String>>> tracksLists = new ArrayList<>();
    List<List<String>> tracksLists = new ArrayList<>();
    public List<String> playlistName = new ArrayList<>();
    //HashMap<String, List<HashMap<String, String>>> listDataChild;
    public HashMap<String, List<String>> listDataChild;
    List<String> playlistIds = new ArrayList<>();
    private Context context;

    int[] flags = new int[]{
            R.drawable.ic_home,
            R.drawable.ic_cancel,
            R.drawable.ic_menu_camera,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home,
            R.drawable.ic_home
    };

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

        getPlaylists();

        final View v = inflater.inflate(R.layout.fragment_play_list, container,false);

        expListView = (ExpandableListView) v.findViewById(R.id.expand_list);

        prepareListData();

        listAdapter = new ExpandableListAdapters(this.getContext(), playlistName, listDataChild);

        expListView.setAdapter(listAdapter);

       expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
       expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(context.getApplicationContext(),
                       playlistName.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
       expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(context.getApplicationContext(),
                       playlistName.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();

            }
        });

        // Listview on child click listener
       expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(
                        context.getApplicationContext(),
                       playlistName.get(groupPosition)
                                + " : "
                                +listDataChild.get(
                               playlistName.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void prepareListData(){
        listDataChild = new HashMap<>();

        for(int i = 0; i < playlistName.size(); i++){
            listDataChild.put(playlistName.get(i), tracksLists.get(i));
        }
    }

    public void getPlaylists(){
        try {
            new asyncGetPlaylists(new asyncGetPlaylists.AsyncResponse() {
                @Override
                public void processFinish(Pager<PlaylistSimple> output) {
                    for (int i = 0; i < output.items.size(); i++){
                        playlistIds.add(output.items.get(i).id);
                        playlistName.add(output.items.get(i).name);
                        getPlaylistTracks(output.items.get(i).id);
                    }
                    //listAdapter.notifyDataSetChanged();
                }
            }).execute("aaup8");
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void getPlaylistTracks(String playlistId){
        final List<String> aList = new ArrayList<>();
        new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){

            @Override
            public void processFinish(Pager output){

                for(int i=0;i < output.items.size(); i++) {
                    //HashMap<String, String> hm = new HashMap<String,String>();
                    List<String> hm = new ArrayList<>();

                    PlaylistTrack p = (PlaylistTrack) output.items.get(i);
                    String s = p.track.name;
                    hm.add(s);
                    //hm.put("txt", s);
                    //hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                    //hm.put("flag", Integer.toString(flags[5]));
                    aList.add(s);
                }
                tracksLists.add(aList);
                listAdapter.notifyDataSetChanged();
            }
        }).execute("aaup8", playlistId);
    }

    public interface OnFragmentInteractionListener {
    }
}
