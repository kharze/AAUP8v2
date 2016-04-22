package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aaup8v2.aaup8v2.MusicPlayer;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;


public class QueueFragment extends Fragment {
    List<HashMap<String,String>> elementList = new ArrayList<>();
    ListView mlistView; // The view for this fragment
    public List<QueueElement> mQueueElementList = new ArrayList<>();
    MusicPlayer musicPlayer = new MusicPlayer();

    // Icons used for the ListView
    private int like = R.drawable.ic_action_like;
    private int dontlike = R.drawable.ic_action_dontlike;
    private int likeActive = R.drawable.ic_action_like_active;
    private int dontlikeActive = R.drawable.ic_action_dontlike_active;
    private int flag = R.drawable.ic_home; //Should be changed at some point

    private SimpleAdapter queueAdapter; //Adapter for the list view

    private OnFragmentInteractionListener mListener;

    private Gson gson;
    private SharedPreferences mPrefs;

    public QueueFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        Context context = getContext();

        mPrefs = context.getSharedPreferences("Queue", 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialize adapter for the list
        String[]    from    = { "flag",         "txt",      "cur",      "upVote",       "downVote",     "downCount",    "upCount" };
        int[]       to      = { R.id.flag,      R.id.txt,   R.id.cur,   R.id.upVote,    R.id.downVote,  R.id.downCount, R.id.upCount};
        queueAdapter = new SimpleAdapter(getActivity().getBaseContext(), elementList, R.layout.queue_listview_element,from,to );

        //Specifies the ListView
        View v = inflater.inflate(R.layout.fragment_queue, container, false);
        mlistView = (ListView)v.findViewById(R.id.queue_list);

        mlistView.setAdapter(queueAdapter);

        // Inflate the layout for this fragment
        return v;
    }

    public void onResume(){
        super.onResume();

        String listJSon = mPrefs.getString("mQueueElementList", "");
        Type mClass = new TypeToken<List<QueueElement>>(){}.getType();
        mQueueElementList = gson.fromJson(listJSon, mClass);

        //Resets the playqueue after resuming
        if(mQueueElementList != null){
            for(int i=0;i < mQueueElementList.size();i++) {
                QueueElement element = mQueueElementList.get(i);
                // add track to adapter
                addToAdapter(element);
            }
            sortQueue();
        }
    }

    public void onPause() {
        super.onPause();

        SharedPreferences.Editor ed = mPrefs.edit();
        String listJSon = gson.toJson(mQueueElementList);
        ed.putString("mQueueElementList", listJSon);
        ed.commit();
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

    public void addTrack(myTrack track){
        QueueElement element = new QueueElement();
        element.track = track;

        //Safety measure
        if(mQueueElementList == null){
            mQueueElementList = new ArrayList<>();
        }
        mQueueElementList.add(element);

        //adds the track to the adapter
        addToAdapter(element);
        //queueAdapter.notifyDataSetChanged();
        applyWeight();
    }
    public void addTrack(Track track){
        myTrack mytrack = new myTrack();
        mytrack.setMyTrack(track);

        addTrack(mytrack);
    }
    public void addTrack(TrackSimple track){
        myTrack mytrack = new myTrack();
        mytrack.setMyTrack(track);

        addTrack(mytrack);
    }
    public void addTrack(PlaylistTrack track){
        myTrack mytrack = new myTrack();
        mytrack.setMyTrack(track);

        addTrack(mytrack);
    }

    public void sortQueue(){

        // Comparator for the sorting
        Comparator<QueueElement> compareRank = new Comparator<QueueElement>() {
            @Override
            public int compare(QueueElement lhs, QueueElement rhs) {
                return (rhs.rank - lhs.rank);
            }
        };

        Collections.sort(mQueueElementList,compareRank);

        //Change the view to fit the new sorted list.
        //Would be nice if we had a custom adapter that could use the other list.
        for(int i = 0; mQueueElementList.size() > i; i++){
            elementList.get(i).put("txt", mQueueElementList.get(i).track.name);
            elementList.get(i).put("cur", "Artist : " + mQueueElementList.get(i).track.artist);
            elementList.get(i).put("flag", Integer.toString(flag));
            if(mQueueElementList.get(i).upvoteFlag){
                elementList.get(i).put("upVote", Integer.toString(likeActive));
            }else{
                elementList.get(i).put("upVote", Integer.toString(like));
            }
            if(mQueueElementList.get(i).downvoteFlag){
                elementList.get(i).put("downVote", Integer.toString(dontlikeActive));
            }else{
                elementList.get(i).put("downVote", Integer.toString(dontlike));
            }
            elementList.get(i).put("downCount", Integer.toString(mQueueElementList.get(i).downVotes));
            elementList.get(i).put("upCount", Integer.toString(mQueueElementList.get(i).upVotes));
        }
        queueAdapter.notifyDataSetChanged();
    }

    public String nextSong(){
        String trackId = mQueueElementList.get(0).track.id;
        deleteTrack(0);
        return trackId;
    }

    public void deleteTrack(int i){
        mQueueElementList.remove(i);
        elementList.remove(i);
        //queueAdapter.notifyDataSetChanged();
    }

    public void addToAdapter(QueueElement element){

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("txt", element.track.name);
        hMap.put("cur", "Artist : " + element.track.artist);
        hMap.put("flag", Integer.toString(flag));
        if(element.upvoteFlag) {
            hMap.put("upVote", Integer.toString(likeActive));
        }else{
            hMap.put("upVote", Integer.toString(like));
        }
        if(element.downvoteFlag){
            hMap.put("downVote", Integer.toString(dontlikeActive));
        }else{
            hMap.put("downVote", Integer.toString(dontlike));
        }
        hMap.put("downCount", Integer.toString(element.downVotes));
        hMap.put("upCount", Integer.toString(element.upVotes));

        elementList.add(hMap);
    }

    public void click_down_vote(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;

        //Change the value of the up/down votes depending if the button has already been pressed.
        //Change the icon for the button.
        if(!mQueueElementList.get(trackChosenOnList).downvoteFlag)
        {
            elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlikeActive));
            mQueueElementList.get(trackChosenOnList).downvoteFlag = true;
            mQueueElementList.get(trackChosenOnList).downVotes += 1;
            mQueueElementList.get(trackChosenOnList).rank -= 1;
            if ( mQueueElementList.get(trackChosenOnList).upvoteFlag)
            {
                elementList.get(trackChosenOnList).put("upVote", Integer.toString(like));
                mQueueElementList.get(trackChosenOnList).upvoteFlag = false;
                mQueueElementList.get(trackChosenOnList).upVotes -= 1;
                mQueueElementList.get(trackChosenOnList).rank -= 1;
            }
        }
        else
        {
            elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlike));
            mQueueElementList.get(trackChosenOnList).downvoteFlag = false;
            mQueueElementList.get(trackChosenOnList).downVotes -= 1;
            mQueueElementList.get(trackChosenOnList).rank += 1;
        }

        trackWeight(trackChosenOnList);

        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upVotes));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downVotes));
        queueAdapter.notifyDataSetChanged(); //Informs the adapter that it has been changed (Updates view)
        voteThreshold(trackChosenOnList);
        sortQueue();
    }

    public void click_up_vote(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;

        //Change the value of the up/down votes depending if the button has already been pressed.
        //Change the icon for the button.
        if(!mQueueElementList.get(trackChosenOnList).upvoteFlag)
        {
            elementList.get(trackChosenOnList).put("upVote", Integer.toString(likeActive));
            mQueueElementList.get(trackChosenOnList).upvoteFlag = true;
            mQueueElementList.get(trackChosenOnList).upVotes += 1;
            mQueueElementList.get(trackChosenOnList).rank += 1;
            if ( mQueueElementList.get(trackChosenOnList).downvoteFlag)
            {
                elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlike));
                mQueueElementList.get(trackChosenOnList).downvoteFlag = false;
                mQueueElementList.get(trackChosenOnList).downVotes -= 1;
                mQueueElementList.get(trackChosenOnList).rank += 1;
            }
        }
        else
        {
            mQueueElementList.get(trackChosenOnList).upvoteFlag = false;
            mQueueElementList.get(trackChosenOnList).upVotes -= 1;
            elementList.get(trackChosenOnList).put("upVote", Integer.toString(like));
            mQueueElementList.get(trackChosenOnList).rank -= 1;

        }

        trackWeight(trackChosenOnList);

        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upVotes));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downVotes));
        queueAdapter.notifyDataSetChanged(); //Informs the adapter that it has been changed (Updates view)
        sortQueue();
    }

    int numberOfPeers = 6;  //Number of people on the network, needs to be replaced

    public void applyWeight(){
        //Applies a basic weight to each track represented on the playlist
        double threshold = numberOfPeers * 0.66; //Setting the threshold limit
        mQueueElementList.get(mQueueElementList.size()-1).weight = threshold;
    }

    public void trackWeight(int trackWeightChange) {
        //Adds the up- and downvotes made by the users to the weight given to the tracks
        mQueueElementList.get(trackWeightChange).weight += mQueueElementList.get(trackWeightChange).rank;
    }

    public void voteThreshold(int downVotedTrack) {
        //If track weight gets below the set threshold it will be removed from the list
        if(mQueueElementList.get(downVotedTrack).weight <= 0)
        {
            deleteTrack(downVotedTrack);
        }
    }

    public void trackWeightIncrease(){
        //Increases the weight of the individual tracks on the playlist everytime a track has been played
        int i = 0;
        int listCount = mQueueElementList.size();

        while(i <  listCount) {
            mQueueElementList.get(i).weight *= 1.1;

            if(i == 1){
                mQueueElementList.get(i).weight *= 50;
            }
            if(i == 2){
                mQueueElementList.get(i).weight *= 40;
            }
            if(i == 3){
                mQueueElementList.get(i).weight *= 30;
            }
            i++;
        }
    }
}
