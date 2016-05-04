package com.example.aaup8v2.aaup8v2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistTracksRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.ThreadResponseInterface;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.fragments.models.ExpandableListAdapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayListFragment extends Fragment{
    public ExpandableListView expListView;
    //List<List<HashMap<String, String>>> tracksLists = new ArrayList<>();
    //List<List<String>> tracksLists = new ArrayList<>();
    public List<String> playlistName;
    //HashMap<String, List<HashMap<String, String>>> listDataChild;
    public HashMap<String, List<String>> listDataChild;
    ExpandableListAdapters listAdapter;

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
        //Made to only update the playlist once in a runtime.
        if(listDataChild == null || playlistName == null){
            listDataChild = new HashMap<>();
            playlistName = new ArrayList<>();
            getPlaylists();
        }

        final View v = inflater.inflate(R.layout.fragment_play_list, container,false);

        listAdapter = new ExpandableListAdapters(getContext(), playlistName, listDataChild);

        expListView = (ExpandableListView) v.findViewById(R.id.expand_list);

        expListView.setAdapter(listAdapter);

        createListeners();

        // Inflate the layout for this fragment
        return v;
    }

    public void createListeners(){
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
                Toast.makeText(getContext().getApplicationContext(),
                        playlistName.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getContext().getApplicationContext(),
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
                Toast.makeText(getContext().getApplicationContext(),
                        playlistName.get(groupPosition)
                                + " : "
                                +listDataChild.get(
                                playlistName.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();
                return false;
            }
        });
    }

    public void prepareListData(List<List<String>> tracksLists){
        //listDataChild = new HashMap<>();

        for(int i = 0; i < playlistName.size(); i++){
            listDataChild.put(playlistName.get(i), tracksLists.get(i));
        }

        /**
        playlistName = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        playlistName.add("Top 250");
        playlistName.add("Now Showing");
        playlistName.add("Coming Soon..");

        // Adding child data
        List<String> top250 = new ArrayList<String>();
        top250.add("The Shawshank Redemption");
        top250.add("The Godfather");
        top250.add("The Godfather: Part II");
        top250.add("Pulp Fiction");
        top250.add("The Good, the Bad and the Ugly");
        top250.add("The Dark Knight");
        top250.add("12 Angry Men");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("The Conjuring");
        nowShowing.add("Despicable Me 2");
        nowShowing.add("Turbo");
        nowShowing.add("Grown Ups 2");
        nowShowing.add("Red 2");
        nowShowing.add("The Wolverine");

        List<String> comingSoon = new ArrayList<String>();
        comingSoon.add("2 Guns");
        comingSoon.add("The Smurfs 2");
        comingSoon.add("The Spectacular Now");
        comingSoon.add("The Canyons");
        comingSoon.add("Europa Report");

        listDataChild.put(playlistName.get(0), top250); // Header, Child data
        listDataChild.put(playlistName.get(1), nowShowing);
        listDataChild.put(playlistName.get(2), comingSoon);
        **/

    }

    public void getPlaylists(){
//        try {
//            new asyncGetPlaylists(new asyncGetPlaylists.AsyncResponse() {
//                @Override
//                public void processFinish(Pager<PlaylistSimple> output) {
//                    for (int i = 0; i < output.items.size(); i++){
//                        playlistIds.add(output.items.get(i).id);
//                        playlistName.add(output.items.get(i).name);
//
//                        getPlaylistTracks(output.items.get(i).owner.id, output.items.get(i).id);
//                    }
//                    prepareListData();
//                    createListeners();
//                    listAdapter.notifyDataSetChanged();
//                }
//            }).execute("aaup8");
//        } catch (Exception e) {
//            e.getMessage();
//        }

        GetPlaylistsRunnable runnable = new GetPlaylistsRunnable(MainActivity.me.id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistSimple>>() {
            @Override
            public void processFinish(Pager<PlaylistSimple> output) {
                List<String> playlistIds = new ArrayList<>();
                final List<List<String>> tracksLists = new ArrayList<>();
                for (int i = 0; i < output.items.size(); i++){
                    playlistIds.add(output.items.get(i).id);
                    playlistName.add(output.items.get(i).name);

                    new GetPlaylistTracksRunnable(output.items.get(i).owner.id, output.items.get(i).id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistTrack>>() {
                        Pager<PlaylistTrack> tracks = new Pager();

                        @Override
                        public void processFinish(Pager<PlaylistTrack> output) {
                            tracks = output;

                                final List<String> aList = new ArrayList<>();
                                for(int i=0;i < tracks.items.size(); i++) {

                                    PlaylistTrack p = tracks.items.get(i);
                                    String s = p.track.name;

                                    aList.add(s);
                                }
                                tracksLists.add(aList);
                        }
                    }).run();
                    //getPlaylistTracks(output.items.get(i).owner.id, output.items.get(i).id);
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prepareListData(tracksLists);
                        //createListeners();
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

    public void getPlaylistTracks(String userId, String playlistId){
        Pager tracks = new Pager();
        try {
            tracks = new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
                @Override
                public void processFinish(Pager output){}
            }).execute(userId, playlistId).get();

            final List<String> aList = new ArrayList<>();
            for(int i=0;i < tracks.items.size(); i++) {
                //HashMap<String, String> hm = new HashMap<String,String>();
                //List<String> hm = new ArrayList<>();

                PlaylistTrack p = (PlaylistTrack) tracks.items.get(i);
                String s = p.track.name;
                //hm.add(s);
                //hm.put("txt", s);
                //hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                //hm.put("flag", Integer.toString(flags[5]));
                aList.add(s);
            }
            //tracksLists.add(aList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
