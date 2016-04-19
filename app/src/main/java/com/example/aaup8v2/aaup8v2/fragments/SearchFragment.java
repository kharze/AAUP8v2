package com.example.aaup8v2.aaup8v2.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.aaup8v2.aaup8v2.QueueElement;
import com.example.aaup8v2.aaup8v2.R;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncSearchMusic;
import com.example.aaup8v2.aaup8v2.myTrack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<myTrack> mTracklist;
    private int add_track = R.drawable.ic_playlist_add;
    private int add_track_check = R.drawable.ic_playlist_add_check;
    private SimpleAdapter searchAdapter;

    List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();
    ListView Search_Results;

    EditText mText;

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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search,
                container, false);

        //Button button = (Button) view.findViewById(R.id.searchMusicButton);
        //button.setOnClickListener(this);


        View v = inflater.inflate(R.layout.fragment_search, container,false);
        Search_Results = (ListView)v.findViewById(R.id.Search_Results);

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
                    HashMap<String, String> hm = new HashMap<String, String>();

                    mTracklist = (List<myTrack>)output;
                    myTrack track = (myTrack) output.get(i);
                    String s = track.name;
                    hm.put("trackName", s);
                    hm.put("artist", "Artist : " + track.artist);
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

        //MainActivity.mQueueFragment.addTrack(mTracklist.get(trackChosenOnList));

        Context context = getContext();
        SharedPreferences mPrefs = context.getSharedPreferences("Queue", 1);
        Gson gson = new Gson();
        Type c = new TypeToken<List<QueueElement>>(){}.getType();
        String listJSon = mPrefs.getString("mQueueElementList", "");
        List<QueueElement> queueList = gson.fromJson(listJSon, c);
        if(queueList == null){
            queueList = new ArrayList<>();
        }
        QueueElement queueEmelemt = new QueueElement();
        queueEmelemt.track = mTracklist.get(trackChosenOnList);
        queueList.add(queueEmelemt);


        SharedPreferences.Editor ed = mPrefs.edit();
        String listJSon2 = gson.toJson(queueList);
        ed.putString("mQueueElementList", listJSon2);
        ed.commit();
    }

}
