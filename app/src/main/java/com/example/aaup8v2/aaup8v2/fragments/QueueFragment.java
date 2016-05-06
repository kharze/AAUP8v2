package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.aaup8v2.aaup8v2.FindIP;
import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.fragments.models.QueueListAdapter;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;


public class QueueFragment extends Fragment {
    //List<HashMap<String,String>> elementList = new ArrayList<>();
    ListView mlistView; // The view for this fragment
    public List<QueueElement> mQueueElementList = new ArrayList<>();

    public String myIP = FindIP.getIPAddress(true); //The ip of the device

    public QueueListAdapter queueAdapter; // Our custom adapter for the queue

    private OnFragmentInteractionListener mListener; //Is is needed to be here, get an error if removed.


    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Inflates the view
        View v = inflater.inflate(R.layout.fragment_queue, container, false);
        mlistView = (ListView)v.findViewById(R.id.queue_list);

        //Instantiate the adapter
        queueAdapter = new QueueListAdapter(getContext(), R.layout.queue_listview_element, mQueueElementList);

        //Sets the adapter for the view
        mlistView.setAdapter(queueAdapter);

        // Inflate the layout for this fragment
        return v;
    }

    public void onResume(){
        super.onResume();

        //Resets the playqueue after resuming
        if(mQueueElementList != null)
            sortQueue(); // TODO: 06-05-2016 Find out if this is even needed 
        else
            mQueueElementList = new ArrayList<>();
    }

    public void onPause() {
        super.onPause();
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

    public void addTrack(myTrack track){
        QueueElement element = new QueueElement();
        element.track = track;

        //Safety measure
        if(mQueueElementList == null)
            mQueueElementList = new ArrayList<>();

        mQueueElementList.add(element);
        if(queueAdapter != null)
            queueAdapter.notifyDataSetChanged();

        applyWeight();
    }
    public void addTrack(Track track){
        myTrack mytrack = new myTrack(track);
        addTrack(mytrack);
    }
    public void addTrack(TrackSimple track){
        myTrack mytrack = new myTrack(track);
        addTrack(mytrack);
    }
    public void addTrack(PlaylistTrack track){
        myTrack mytrack = new myTrack(track);
        addTrack(mytrack);
    }

    public void sortQueue(){
        // Comparator for the sorting
        Comparator<QueueElement> compareWeight = new Comparator<QueueElement>() {
            @Override
            public int compare(QueueElement lhs, QueueElement rhs) {
                return (int)(rhs.weight - lhs.weight);
            }
        };
        Collections.sort(mQueueElementList,compareWeight);
        
        if(queueAdapter != null)
            queueAdapter.notifyDataSetChanged();
    }

    public String nextSong(){ // TODO: 06-05-2016 Broadcast the track, so that peers can add the track to their playbar. 
        String trackId = mQueueElementList.get(0).track.id;
        String artist = "Artist: " + mQueueElementList.get(0).track.artist;

        MainActivity.playedArtist.setText(artist);
        MainActivity.playedName.setText(mQueueElementList.get(0).track.name);
        deleteTrack(0);
        return trackId;
    }

    public void deleteTrack(int i){
        mQueueElementList.remove(i);
        if(queueAdapter != null)
            queueAdapter.notifyDataSetChanged();
    }

    public void click_down_vote(int position){
        //Change the value of the up/down votes depending if the button has already been pressed.
        //Change the icon for the button.
        if(!mQueueElementList.get(position).downvoteList.contains(myIP))
        {
            mQueueElementList.get(position).weight -= 1;
            mQueueElementList.get(position).downvoteList.add(myIP);

            if (mQueueElementList.get(position).upvoteList.contains(myIP))
            {
                mQueueElementList.get(position).weight -= 1;
                mQueueElementList.get(position).upvoteList.remove(myIP);
            }
        }
        else
        {
            mQueueElementList.get(position).weight += 1;
            mQueueElementList.get(position).downvoteList.remove(myIP);
        }

        Gson gson = new Gson();

        if (MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner){
            voteThreshold(position);
            sortQueue();

            String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
            MainActivity.mWifiDirectActivity.sendDataToPeers(WifiDirectActivity.DOWN_VOTE, queueList);
        }
        else if(MainActivity.mWifiDirectActivity.info != null){
            MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.DOWN_VOTE,Integer.toString(position),myIP);
        } else {
            sortQueue();
        }
        if(queueAdapter != null)
            queueAdapter.notifyDataSetChanged();
    }

    public void click_up_vote(int position){
        //Change the value of the up/down votes depending if the button has already been pressed.
        //Change the icon for the button.
        if(!mQueueElementList.get(position).upvoteList.contains(myIP))
        {
            mQueueElementList.get(position).weight += 1;
            mQueueElementList.get(position).upvoteList.add(myIP);

            if (mQueueElementList.get(position).downvoteList.contains(myIP))
            {
                mQueueElementList.get(position).weight += 1;
                mQueueElementList.get(position).downvoteList.remove(myIP);
            }
        }
        else
        {
            mQueueElementList.get(position).weight -= 1;
            mQueueElementList.get(position).upvoteList.remove(myIP);
        }

        Gson gson = new Gson();

        if (MainActivity.mWifiDirectActivity.info != null && MainActivity.mWifiDirectActivity.info.isGroupOwner){
            sortQueue();

            String queueList = gson.toJson(MainActivity.mQueueFragment.mQueueElementList);
            MainActivity.mWifiDirectActivity.sendDataToPeers(WifiDirectActivity.UP_VOTE, queueList);
        }
        else if(MainActivity.mWifiDirectActivity.info != null){
            MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.UP_VOTE,Integer.toString(position),myIP);
        } else {
            sortQueue();
        }
        if(queueAdapter != null)
            queueAdapter.notifyDataSetChanged();
    }

    // TODO: 06-05-2016 Make this more dynamic. 
    int numberOfPeers = MainActivity.mWifiDirectActivity.ipsOnNetwork.size() + 1;
    double threshold = numberOfPeers * 0.66; //Setting the threshold limit

    public void applyWeight(){
        //Applies a basic weight to each track represented on the playlist
        mQueueElementList.get(mQueueElementList.size()-1).weight = threshold;
    }

    public void voteThreshold(int downVotedTrack) {
        //If track weight gets below the set threshold it will be removed from the list
        if(((threshold + mQueueElementList.get(downVotedTrack).upvoteList.size() - mQueueElementList.get(downVotedTrack).downvoteList.size()) <= 0) || (MainActivity.mWifiDirectActivity.ipsOnNetwork.size() + 1 == 1))
        {
            deleteTrack(downVotedTrack);
        }
    }

    public void trackWeightIncrease(){
        //Increases the weight of the individual tracks on the playlist everytime a track has been played
        for(int i = 0; i <  mQueueElementList.size(); i++) {
            mQueueElementList.get(i).weight *= 1.1;

            if(i == 0){
                mQueueElementList.get(i).weight *= 50;
            }
            if(i == 1){
                mQueueElementList.get(i).weight *= 40;
            }
            if(i == 2){
                mQueueElementList.get(i).weight *= 30;
            }
        }
    }
}
