package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.SpotifyAccess;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylist;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

public class PlayListFragment extends Fragment {
    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView list;

    String[] mNames = { "Paradise city", "Sweet Child o' Mine", "Nothing else Matters", "Levels", "Sweet Lovin'",
            "Der er noget galt i Danmark", "Lazaro", "Spændt op til lir", "Last Christmas", "Go West", "Work",
            "Yesterday", "Gi' mig et smil" };

    String[] mArtist = { "Guns'n Roses", "Guns'n Roses", "Metallica", "Avicii", "Sigala",
            "John Mogensen", "Pavarotti", "Jokeren", "Wham", "Pet shop boys", "Rihanna",
            "Beatles", "Wafande" };

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

    private OnFragmentInteractionListener mListener;

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

        new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){

            @Override
            public void processFinish(Pager output){

                for(int i=0;i < output.items.size();i++){
                    HashMap<String, String> hm = new HashMap<String,String>();

                    PlaylistTrack p = (PlaylistTrack) output.items.get(i);
                    String s = p.track.name;
                    hm.put("txt", s);
                    hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                    hm.put("flag", Integer.toString(flags[5]));
                    aList.add(hm);
                }

                String[] from = { "flag","txt","cur" };

                int[] to = { R.id.flag,R.id.txt,R.id.cur,R.id.textView};
                SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_layout,from,to );


                // Assign adapter to ListView
                list.setAdapter(adapter);
            }
        }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV");

        View v = inflater.inflate(R.layout.fragment_play_list, container,false);
        list = (ListView)v.findViewById(R.id.list);

        // Inflate the layout for this fragment
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
