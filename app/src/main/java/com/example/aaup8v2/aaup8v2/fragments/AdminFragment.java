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
import android.widget.Spinner;

import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;


public class AdminFragment extends Fragment {
    Spinner spinner;
    List<HashMap<String,String>> aList = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

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





        new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){

            @Override
            public void processFinish(Pager output){

                for(int i=0;i < output.items.size();i++){
                    HashMap<String, String> hm = new HashMap<>();

                    PlaylistTrack p = (PlaylistTrack) output.items.get(i);
                    String s = p.track.name;
                    hm.put("txt", s);
                    hm.put("cur", "Artist : " + p.track.artists.get(0).name);
                    aList.add(hm);
                }

                String[] from = { "flag","txt","cur" };

                int[] to = { R.id.flag,R.id.txt,R.id.cur,R.id.textView};
                SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_layout,from,to );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

               // Apply the adapter to the spinner
                spinner.setAdapter(adapter);
            }
        }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV");



        View v = inflater.inflate(R.layout.fragment_admin, container,false);
        spinner = (Spinner) v.findViewById(R.id.playlist_spinner);


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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
