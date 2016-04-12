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

import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.myTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;


public class QueueFragment extends Fragment {
    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView mlist; // The view for this fragment
    List<QueueElement> mQueueElement = new ArrayList<>();
    List<myTrack> mTracks = new ArrayList<>(); // A list of all the tracks

    int like = R.drawable.ic_action_like;
    int dontlike = R.drawable.ic_action_dontlike;
    int flag = R.drawable.ic_home;
    SimpleAdapter adapter;
    //int flag2 = R.drawable.ic_star;
    //int flag3 = R.drawable.ic_cancel;

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

                    // add track to list
                    myTrack track = new myTrack();
                    track.setMyTrack(p);
                    QueueElement element = new QueueElement();
                    element.track = track;
                    mQueueElement.add(element);
                }
                showQueue();
            }
        }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV");

        View v = inflater.inflate(R.layout.fragment_queue, container,false);
        mlist = (ListView)v.findViewById(R.id.queue_list);

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

    // function for showing all the tracks in the queue on the list
    public void showQueue(){
        // adds all elements to a HashMap
        aList = new ArrayList<>();
        for(int i = 0; mQueueElement.size() > i; i++){
            HashMap<String, String> hm = new HashMap<>();
            QueueElement element = mQueueElement.get(i);
            hm.put("txt", element.track.name);
            hm.put("cur", "Artist : " + element.track.artist);
            hm.put("flag", Integer.toString(flag));
            hm.put("upVote", Integer.toString(like));
            hm.put("downVote", Integer.toString(dontlike));
            hm.put("downCount", Integer.toString(element.downVotes));
            hm.put("upCount", Integer.toString(element.upVotes));

            aList.add(hm);
        }

        // Create a simple adapter for the queue
        String[] from = { "flag","txt","cur", "upVote", "downVote", "downCount", "upCount" };
        int[] to = { R.id.flag,R.id.txt,R.id.cur, R.id.upVote, R.id.downVote, R.id.downCount, R.id.upCount};
        adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.queue_listview_element,from,to );

        // Assign adapter to ListView
        mlist.setAdapter(adapter);
    }

    public void click_down_vote(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;
        if(!mQueueElement.get(trackChosenOnList).downvoteFlag)
        {
            mQueueElement.get(trackChosenOnList).downvoteFlag = true;
            mQueueElement.get(trackChosenOnList).downVotes += 1;
            if ( mQueueElement.get(trackChosenOnList).upvoteFlag)
            {
                mQueueElement.get(trackChosenOnList).upvoteFlag = false;
                mQueueElement.get(trackChosenOnList).upVotes -= 1;
            }
        }
        else
        {
            mQueueElement.get(trackChosenOnList).downvoteFlag = false;
            mQueueElement.get(trackChosenOnList).downVotes -= 1;
        }
        ((HashMap<String, String>) listVoteInView.getAdapter().getItem(trackChosenOnList)).put("upCount", Integer.toString(mQueueElement.get(trackChosenOnList).upVotes));
        ((HashMap<String, String>) listVoteInView.getAdapter().getItem(trackChosenOnList)).put("downCount", Integer.toString(mQueueElement.get(trackChosenOnList).downVotes));
        adapter.notifyDataSetChanged();
    }

    public void click_up_vote(View view){
        // These two lines are used to find out which line of the list the button is in.
        ListView listVoteInView = (ListView)view.getParent().getParent();
        int bIndex = listVoteInView.indexOfChild((View)view.getParent());
        int trackChosenOnList = listVoteInView.getFirstVisiblePosition() + bIndex;
        if(!mQueueElement.get(trackChosenOnList).upvoteFlag)
        {
            mQueueElement.get(trackChosenOnList).upvoteFlag = true;
            mQueueElement.get(trackChosenOnList).upVotes += 1;
            if ( mQueueElement.get(trackChosenOnList).downvoteFlag)
            {
                mQueueElement.get(trackChosenOnList).downvoteFlag = false;
                mQueueElement.get(trackChosenOnList).downVotes -= 1;
            }

        }
        else
        {
            mQueueElement.get(trackChosenOnList).upvoteFlag = false;
            mQueueElement.get(trackChosenOnList).upVotes -= 1;
        }
        ((HashMap<String, String>) listVoteInView.getAdapter().getItem(trackChosenOnList)).put("upCount", Integer.toString(mQueueElement.get(trackChosenOnList).upVotes));
        ((HashMap<String, String>) listVoteInView.getAdapter().getItem(trackChosenOnList)).put("downCount", Integer.toString(mQueueElement.get(trackChosenOnList).downVotes));
        adapter.notifyDataSetChanged();
    }
}
