package com.example.aaup8v2.aaup8v2.fragments;

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
import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.myTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;


public class QueueFragment extends Fragment {
    List<HashMap<String,String>> elementList = new ArrayList<>();
    ListView mlistView; // The view for this fragment
    List<QueueElement> mQueueElementList = new ArrayList<>();

    // Icons used for the ListView
    int like = R.drawable.ic_action_like;
    int dontlike = R.drawable.ic_action_dontlike;
    int likeActive = R.drawable.ic_action_like_active;
    int dontlikeActive = R.drawable.ic_action_dontlike_active;
    int flag = R.drawable.ic_home;

    SimpleAdapter adapter; //Adapter for the list view

    private OnFragmentInteractionListener mListener;

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

        // only used for test purpose;
        new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
            @Override
            public void processFinish(Pager output){

                for(int i=0;i < output.items.size();i++){
                    PlaylistTrack p = (PlaylistTrack) output.items.get(i);

                    // add track to list track list and adapter
                    myTrack track = new myTrack();
                    track.setMyTrack(p);
                    addTrack(track);
                }
            }
        }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV");

        //Specifies the ListView
        View v = inflater.inflate(R.layout.fragment_queue, container,false);
        mlistView = (ListView)v.findViewById(R.id.queue_list);

        // Initialize adapter for the list
        String[]    from    = { "flag",         "txt",      "cur",      "upVote",       "downVote",     "downCount",    "upCount" };
        int[]       to      = { R.id.flag,      R.id.txt,   R.id.cur,   R.id.upVote,    R.id.downVote,  R.id.downCount, R.id.upCount};
        adapter = new SimpleAdapter(getActivity().getBaseContext(), elementList, R.layout.queue_listview_element,from,to );
        mlistView.setAdapter(adapter);

        // Inflate the layout for this fragment
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
        mQueueElementList.add(element);

        //adds the track to the adapter
        addToAdapter(element);
        adapter.notifyDataSetChanged();
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
        Boolean change;

        //
        Comparator<QueueElement> compareRank = new Comparator<QueueElement>() {
            @Override
            public int compare(QueueElement lhs, QueueElement rhs) {
                return (rhs.rank - lhs.rank);
            }
        };

        Collections.sort(mQueueElementList,compareRank);


        for(int i = 0; mQueueElementList.size() > i; i++){
            elementList.get(i).put("txt", mQueueElementList.get(i).track.name);
            elementList.get(i).put("cur", "Artist : " + mQueueElementList.get(i).track.artist);
            elementList.get(i).put("flag", Integer.toString(flag));
            if(mQueueElementList.get(i).upvoteFlag == true){
                elementList.get(i).put("upVote", Integer.toString(likeActive));
            }else{
                elementList.get(i).put("upVote", Integer.toString(like));
            }
            if(mQueueElementList.get(i).downvoteFlag == true){
                elementList.get(i).put("downVote", Integer.toString(dontlikeActive));
            }else{
                elementList.get(i).put("downVote", Integer.toString(dontlike));
            }
            elementList.get(i).put("downCount", Integer.toString(mQueueElementList.get(i).downVotes));
            elementList.get(i).put("upCount", Integer.toString(mQueueElementList.get(i).upVotes));
        }
        adapter.notifyDataSetChanged();

    }

    public List<QueueElement> sortQueue(List<QueueElement> queueElementList){

        return queueElementList;
    }

    public void playNextSong(){
        MainActivity.mPlayer.play("spotify:track:"+mQueueElementList.get(0).track.id);
        //Add function to add information to the playbar or make a Player class that does the job
        //If the player is made into a class the code has to be completely changed.
        deleteTrack(0);
    }

    public void deleteTrack(int i){
        mQueueElementList.remove(i);
        elementList.remove(i);
        adapter.notifyDataSetChanged();
    }


    public void addToAdapter(QueueElement element){

        HashMap<String, String> hMap = new HashMap<>();
        hMap.put("txt", element.track.name);
        hMap.put("cur", "Artist : " + element.track.artist);
        hMap.put("flag", Integer.toString(flag));
        hMap.put("upVote", Integer.toString(like));
        hMap.put("downVote", Integer.toString(dontlike));
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
        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upVotes));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downVotes));
        adapter.notifyDataSetChanged(); //Informs the adapter that it has been changed (Updates view)
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
        //Updates the upvote/downvote value in the view.
        elementList.get(trackChosenOnList).put("upCount", Integer.toString(mQueueElementList.get(trackChosenOnList).upVotes));
        elementList.get(trackChosenOnList).put("downCount", Integer.toString(mQueueElementList.get(trackChosenOnList).downVotes));
        adapter.notifyDataSetChanged(); //Informs the adapter that it has been changed (Updates view)
        sortQueue();
    }
}
