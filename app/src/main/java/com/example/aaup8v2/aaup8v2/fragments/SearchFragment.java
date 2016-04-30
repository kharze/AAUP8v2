package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.content.Intent;
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
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncSearchMusic;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.DataTransferService;
import com.example.aaup8v2.aaup8v2.wifidirect.HostTransferService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
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

    private List<myTrack> mTracklist;
    private int add_track = R.drawable.ic_playlist_add;
    private int add_track_check = R.drawable.ic_playlist_add_check;
    private SimpleAdapter searchAdapter;

    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView Search_Results;
    private EditText mText;

    private OnFragmentInteractionListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }


    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();

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

        String[] from = {"trackName", "artist", "add"};

        int[] to = {R.id.trackName, R.id.artist, R.id.add_track};
        searchAdapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_search_layout, from, to);


        // Assign adapter to ListView
        Search_Results.setAdapter(searchAdapter);
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

                while(!aList.isEmpty()){
                    aList.remove(0);
                }

                for (int i = 0; i < output.size(); i++) {
                    HashMap<String, String> hm = new HashMap<>();

                    mTracklist = (List<myTrack>)output;
                    myTrack track = (myTrack) output.get(i);
                    String s = track.name;
                    hm.put("trackName", s);
                    hm.put("artist", "Artist : " + track.artist);

                    // Checks if the track is already on the Queue
                    for(int j = 0; MainActivity.mQueueFragment.mQueueElementList.size() > j; j++){
                        if(MainActivity.mQueueFragment.mQueueElementList.get(j).track.id.equals(((myTrack) output.get(i)).id)){
                            hm.put("add", Integer.toString(add_track_check));
                            break;
                        }
                    } //Checks is the track was detected
                    if(!hm.containsValue(Integer.toString(add_track_check)))
                        hm.put("add", Integer.toString(add_track));
                    aList.add(hm);
                }

                searchAdapter.notifyDataSetChanged();
            }
        }).execute(searchString);
    }


    public void click_search_add_track(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent().getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;

        searchAdapter.notifyDataSetChanged();

        Gson gson = new Gson();

        //Check to see if the track is on the queue
        if (!aList.get(trackChosenOnList).containsValue(Integer.toString(add_track_check))){
            //Change the icon for the button (Needs to have a check for the state of the button.)
            aList.get(trackChosenOnList).put("add", Integer.toString(add_track_check));

            QueueElement queueEmelemt = new QueueElement();
            queueEmelemt.track = mTracklist.get(trackChosenOnList);
            if(MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner){
                MainActivity.mQueueFragment.addTrack(mTracklist.get(trackChosenOnList));

                String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);

                for(int j = 0; j < MainActivity.mWifiDirectActivity.ipsOnNetwork.size(); j++){
                    Intent dataIntent = new Intent(MainActivity.mWifiDirectActivity, DataTransferService.class);
                    dataIntent.setAction(DataTransferService.ACTION_SEND_DATA);
                    dataIntent.putExtra(DataTransferService.EXTRAS_PEER_ADDRESS, MainActivity.mWifiDirectActivity.ipsOnNetwork.get(j));
                    dataIntent.putExtra(DataTransferService.EXTRAS_PEER_PORT, 8988);
                    dataIntent.putExtra(DataTransferService.EXTRAS_DATA, queueList);
                    dataIntent.putExtra(DataTransferService.EXTRAS_TYPE, "track_added");
                    getActivity().startService(dataIntent);
                }
            }
            else if (MainActivity.mWifiDirectActivity.info != null){
                String track = gson.toJson(mTracklist.get(trackChosenOnList));

                Intent serviceIntent = new Intent(MainActivity.mWifiDirectActivity, HostTransferService.class);
                serviceIntent.setAction(HostTransferService.ACTION_SEND_DATA);
                serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
                        MainActivity.mWifiDirectActivity.info.groupOwnerAddress.getHostAddress());
                serviceIntent.putExtra(HostTransferService.EXTRAS_GROUP_OWNER_PORT, 8888);
                serviceIntent.putExtra(HostTransferService.EXTRAS_DATA, track);
                serviceIntent.putExtra(HostTransferService.EXTRAS_TYPE, "track_added");
                getActivity().startService(serviceIntent);
            }
            else{ //in case we aren't connected to a network, we just add it as a jukebox.
                MainActivity.mQueueFragment.addTrack(mTracklist.get(trackChosenOnList));
            }
            // A toast for when the track is added to the queue
            Toast.makeText(getContext(),"Track added to queue",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "Track already on the queue", Toast.LENGTH_SHORT).show();
        }
    }

}
