package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.FindIP;
import com.example.aaup8v2.aaup8v2.myTrack;

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

    // Icons used for the ListView
    private int like = R.drawable.ic_action_like;
    private int dontlike = R.drawable.ic_action_dontlike;
    private int likeActive = R.drawable.ic_action_like_active;
    private int dontlikeActive = R.drawable.ic_action_dontlike_active;
    private int flag = R.drawable.ic_home; //Should be changed at some point
    String myIP = FindIP.getIPAddress(true);

    private SimpleAdapter queueAdapter; //Adapter for the list view

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

        // Initialize adapter for the list
        String[]    from    = { "flag",         "txt",      "cur",      "upVote",       "downVote",     "downCount",    "upCount" };
        int[]       to      = { R.id.flag,      R.id.txt,   R.id.cur,   R.id.upVote,    R.id.downVote,  R.id.downCount, R.id.upCount};
        queueAdapter = new SimpleAdapter(getActivity().getBaseContext(), elementList, R.layout.queue_listview_element,from,to );

        //Specifies the ListView
        View v = inflater.inflate(R.layout.fragment_queue, container, false);
        mlistView = (ListView)v.findViewById(R.id.queue_list);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //Makes onClickListener for Elements on the list.
        queueAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if(!view.hasOnClickListeners()) {
                    if (view.getId() == R.id.upVote) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                click_up_vote(v);
                            }
                        });
                    }
                    if (view.getId() == R.id.downVote) {
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                click_down_vote(v);
                            }
                        });

                    }
                }
                return false;
            }
        });
        mlistView.setAdapter(queueAdapter);

        // Inflate the layout for this fragment
        return v;
    }

    public void onResume(){
        super.onResume();

        //Resets the playqueue after resuming
        if(mQueueElementList != null){
            for(int i=0;i < mQueueElementList.size();i++) {
                QueueElement element = mQueueElementList.get(i);
                // add track to adapter
                addToAdapter(element);
            }
            sortQueue();
        }else{
            mQueueElementList = new ArrayList<>();
        }
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
        Comparator<QueueElement> compareWeight = new Comparator<QueueElement>() {
            @Override
            public int compare(QueueElement lhs, QueueElement rhs) {
                return (int)(rhs.weight - lhs.weight);
            }
        };

        Collections.sort(mQueueElementList,compareWeight);

        //Change the view to fit the new sorted list.
        //Would be nice if we had a custom adapter that could use the other list.
        for(int i = 0; mQueueElementList.size() > i; i++){
            elementList.get(i).put("txt", mQueueElementList.get(i).track.name);
            elementList.get(i).put("cur", "Artist : " + mQueueElementList.get(i).track.artist);
            elementList.get(i).put("flag", Integer.toString(flag));
            if(mQueueElementList.get(i).upvoteList.contains(myIP)){
                elementList.get(i).put("upVote", Integer.toString(likeActive));
            }else{
                elementList.get(i).put("upVote", Integer.toString(like));
            }
            if(mQueueElementList.get(i).downvoteList.contains(myIP)){
                elementList.get(i).put("downVote", Integer.toString(dontlikeActive));
            }else{
                elementList.get(i).put("downVote", Integer.toString(dontlike));
            }
            elementList.get(i).put("downCount", Integer.toString(mQueueElementList.get(i).downvoteList.size()));
            elementList.get(i).put("upCount", Integer.toString(mQueueElementList.get(i).upvoteList.size()));
        }
        queueAdapter.notifyDataSetChanged();
    }

    public String nextSong(){
        String trackId = mQueueElementList.get(0).track.id;
        MainActivity.playedArtist.setText("Artist: " + mQueueElementList.get(0).track.artist);
        MainActivity.playedName.setText(mQueueElementList.get(0).track.name);
        deleteTrack(0);
        return trackId;
    }

    public void deleteTrack(int i){
        mQueueElementList.remove(i);
        elementList.remove(i);
        queueAdapter.notifyDataSetChanged();
    }

    public void addToAdapter(QueueElement element){

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("txt", element.track.name);
        hMap.put("cur", "Artist : " + element.track.artist);
        hMap.put("flag", Integer.toString(flag));
        if(element.upvoteList.contains(myIP)) {
            hMap.put("upVote", Integer.toString(likeActive));
        }else{
            hMap.put("upVote", Integer.toString(like));
        }
        if(element.downvoteList.contains(myIP)){
            hMap.put("downVote", Integer.toString(dontlikeActive));
        }else{
            hMap.put("downVote", Integer.toString(dontlike));
        }
        hMap.put("downCount", Integer.toString(element.downvoteList.size()));
        hMap.put("upCount", Integer.toString(element.upvoteList.size()));

        elementList.add(hMap);
    }

    public void click_down_vote(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;

        //Change the value of the up/down votes depending if the button has already been pressed.
        //Change the icon for the button.
        if(!mQueueElementList.get(trackChosenOnList).downvoteList.contains(myIP))
        {
            elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlikeActive));
            mQueueElementList.get(trackChosenOnList).weight -= 1;
            mQueueElementList.get(trackChosenOnList).downvoteList.add(myIP);

            if (mQueueElementList.get(trackChosenOnList).upvoteList.contains(myIP))
            {
                elementList.get(trackChosenOnList).put("upVote", Integer.toString(like));
                mQueueElementList.get(trackChosenOnList).weight -= 1;
                mQueueElementList.get(trackChosenOnList).upvoteList.remove(myIP);
            }
        }
        else
        {
            elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlike));
            mQueueElementList.get(trackChosenOnList).weight += 1;
            mQueueElementList.get(trackChosenOnList).downvoteList.remove(myIP);
        }

        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upvoteList.size()));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downvoteList.size()));
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
        if(!mQueueElementList.get(trackChosenOnList).upvoteList.contains(myIP))
        {
            elementList.get(trackChosenOnList).put("upVote", Integer.toString(likeActive));
            mQueueElementList.get(trackChosenOnList).weight += 1;
            mQueueElementList.get(trackChosenOnList).upvoteList.add(myIP);

            if (mQueueElementList.get(trackChosenOnList).downvoteList.contains(myIP))
            {
                elementList.get(trackChosenOnList).put("downVote", Integer.toString(dontlike));
                mQueueElementList.get(trackChosenOnList).weight += 1;
                mQueueElementList.get(trackChosenOnList).downvoteList.remove(myIP);
            }
        }
        else
        {
            elementList.get(trackChosenOnList).put("upVote", Integer.toString(like));
            mQueueElementList.get(trackChosenOnList).weight -= 1;
            mQueueElementList.get(trackChosenOnList).upvoteList.remove(myIP);
        }

        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upvoteList.size()));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downvoteList.size()));
        queueAdapter.notifyDataSetChanged(); //Informs the adapter that it has been changed (Updates view)
        sortQueue();
    }

    int numberOfPeers = 6;  //Number of people on the network, needs to be replaced
    double threshold = numberOfPeers * 0.66; //Setting the threshold limit

    public void applyWeight(){
        //Applies a basic weight to each track represented on the playlist
        mQueueElementList.get(mQueueElementList.size()-1).weight = threshold;
        double test = mQueueElementList.get(mQueueElementList.size()-1).weight;
    }

    public void voteThreshold(int downVotedTrack) {
        //If track weight gets below the set threshold it will be removed from the list
        if((threshold + mQueueElementList.get(downVotedTrack).upvoteList.size() - mQueueElementList.get(downVotedTrack).downvoteList.size()) <= 0)
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

            if(i == 0){
                mQueueElementList.get(i).weight *= 50;
            }
            if(i == 1){
                mQueueElementList.get(i).weight *= 40;
            }
            if(i == 2){
                mQueueElementList.get(i).weight *= 30;
            }
            double test = mQueueElementList.get(i).weight;
            i++;
        }
    }
}
