package com.example.aaup8v2.aaup8v2.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.fragments.models.SearchListAdapter;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;


public class SearchFragment extends Fragment{

    private List<myTrack> mTracklist = new ArrayList<>();
    public SearchListAdapter searchAdapter;

    // Values used for the search
    private String searchTerm;
    private int preLast;
    private int offset = 0;
    private int limit = 50;
    private HashMap<String, Object> options = new HashMap<>();
    private Runnable searchRunnable;
    private Thread worker;

    // Views
    private Button searchButton;
    private EditText mText;
    private Activity activity;
    private ListView searchResultsList;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Sets views.
        View v = inflater.inflate(R.layout.fragment_search, container,false);
        searchResultsList = (ListView) v.findViewById(R.id.Search_Results);
        searchButton = (Button) v.findViewById(R.id.searchMusicButton);
        mText = (EditText) v.findViewById(R.id.Search_Text);

        activity = getActivity();

        searchAdapter = new SearchListAdapter(getContext(), R.layout.listview_search_layout, mTracklist);
        searchResultsList.setAdapter(searchAdapter);

        setSearchRunnable();
        worker = new Thread(searchRunnable);
        worker.setName("Search Thread");

        setListeners();

        return v; // Inflate the layout for this fragment
    }

    private void setListeners() {
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
                    searchTerm = searchString;
                    startSearchThread();
                } else
                    Toast.makeText(getContext(),"Search input too short", Toast.LENGTH_SHORT).show();
            }
        });

        // Sets so that search start when pressing done on the keyboard.
        mText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchButton.performClick();
                    return true;
                }
                return false;
            }
        });

        //Sets a listener for the scroll that detects when the list is scrolled to the bottom.
        searchResultsList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, final int firstVisibleItem, final int visibleItemCount, final int totalItemCount) {
                final int lastItem = firstVisibleItem + visibleItemCount;
                if(lastItem == totalItemCount) {
                    if (preLast != lastItem) { //to avoid multiple calls for last item
                        Log.d("Last", "Last");
                        preLast = lastItem;

                        //Thread searchThread = new Thread(searchRunnable);
                        //searchThread.setName("Seach Thread");
                        if(!worker.isAlive()) {
                            worker = new Thread(searchRunnable);
                            worker.start();
                        }
                    }
                }
            }
        });
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

    public void startSearchThread(){
        // Resets the search
        mTracklist.clear();
        searchAdapter.notifyDataSetChanged();
        preLast = 0;
        offset = 0;

        if(!worker.isAlive()) {
            worker = new Thread(searchRunnable);
            worker.start();
        }
    }

    private void setSearchRunnable() { //Runnable used to search for tracks. Both for the search butten and scroll listener.
        searchRunnable = new Runnable() {
            @Override
            public void run() {
                options.put(SpotifyService.LIMIT, limit);
                options.put(SpotifyService.OFFSET, offset);
                TracksPager result = MainActivity.mSpotifyAccess.mService.searchTracks(searchTerm, options);
                offset += limit;
                for(int i = 0; result.tracks.items.size() > i; i++){
                    myTrack track = new myTrack(result.tracks.items.get(i));
                    mTracklist.add(track);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchAdapter.notifyDataSetChanged();
                        if(mTracklist.isEmpty()){
                            Toast.makeText(getContext(), "Search did not find anything", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
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

            MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.TRACK_ADDED, track);
            }
        else{ //in case we aren't connected to a network, we just add it as a jukebox.
            MainActivity.mQueueFragment.addTrack(mTracklist.get(position));
            if(searchAdapter != null)
                searchAdapter.notifyDataSetChanged();
        }
    }
}
