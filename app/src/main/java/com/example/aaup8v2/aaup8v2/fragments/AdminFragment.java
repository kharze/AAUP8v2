package com.example.aaup8v2.aaup8v2.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.ThreadResponseInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;


public class AdminFragment extends Fragment {   // TODO: 06-05-2016 Improve this view a lot. Make a custom view for it, and an adapter. 
    ListView list;
    List<HashMap<String,String>> aList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;
    Activity activity;

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

        new Thread(new GetPlaylistsRunnable(MainActivity.me.id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistSimple>>() {
            @Override
            public void processFinish(Pager<PlaylistSimple> output) {
                for(int i=0;i < output.items.size();i++){
                    HashMap<String, String> hm = new HashMap<>();

                    PlaylistSimple p = output.items.get(i);
                    String s = p.name;
                    hm.put("txt", s);
                    aList.add(hm);
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String[] from = { "txt","cur" };

                        int[] to = {R.id.txt,R.id.cur,R.id.textView};
                        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_playlistshostview_layout,from,to );

                        // Apply the adapter to the spinner
                        list.setAdapter(adapter);
                    }
                });

            }
        })).start();

        View v = inflater.inflate(R.layout.fragment_admin, container,false);
        list = (ListView)v.findViewById(R.id.playlist_list);
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
