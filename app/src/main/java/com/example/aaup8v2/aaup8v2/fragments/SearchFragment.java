package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncSearchMusic;
import com.example.aaup8v2.aaup8v2.fragments.models.SearchListAdapter;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment{

    private List<myTrack> mTracklist = new ArrayList<>();
    private SearchListAdapter searchAdapter;

    ListView Search_Results;
    private EditText mText;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_search, container,false);
        Search_Results = (ListView)v.findViewById(R.id.Search_Results);
        Button searchButton = (Button)v.findViewById(R.id.searchMusicButton);
        mText = (EditText) v.findViewById(R.id.Search_Text);

        //listener for the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchString;
                searchString = mText.getText().toString();

                //Ensure searchString has at least 3 characters
                if(searchString.length() >= 3) {
                    //Hides the keyboard when search is pressed
                    InputMethodManager im = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Toast.makeText(getContext(), "Search started", Toast.LENGTH_SHORT).show();
                    MainActivity.mSearchFragment.SearchForMusic(searchString);
                } else
                    Toast.makeText(getContext(),"Search input too short", Toast.LENGTH_SHORT).show();
            }
        });

        searchAdapter = new SearchListAdapter(getContext(), R.layout.listview_search_layout, mTracklist);
        Search_Results.setAdapter(searchAdapter); // Assign adapter to ListView

        return v; // Inflate the layout for this fragment
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

    public void SearchForMusic(String searchString){
        new asyncSearchMusic(new asyncSearchMusic.AsyncResponse() {
            @Override
            public void processFinish(List output) {

                if(output.getClass() == mTracklist.getClass()) {
                    mTracklist.clear(); //Clears the whole list
                    mTracklist.addAll(output);
                }

                if(searchAdapter != null)
                    searchAdapter.notifyDataSetChanged();

            }
        }).execute(searchString);
    }


    public void click_search_add_track(int position){
        Gson gson = new Gson();

        if(MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner){
            MainActivity.mQueueFragment.addTrack(mTracklist.get(position));
            String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
            MainActivity.mWifiDirectActivity.sendDataToPeers(WifiDirectActivity.TRACK_ADDED, queueList);
            if(searchAdapter != null)
                searchAdapter.notifyDataSetChanged();
        }
        else if (MainActivity.mWifiDirectActivity.info != null){
            String track = gson.toJson(mTracklist.get(position));

            MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.TRACK_ADDED, track, MainActivity.mQueueFragment.myIP);
            }
        else{ //in case we aren't connected to a network, we just add it as a jukebox.
            MainActivity.mQueueFragment.addTrack(mTracklist.get(position));
            if(searchAdapter != null)
                searchAdapter.notifyDataSetChanged();
        }
    }
}
