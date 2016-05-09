package com.example.aaup8v2.aaup8v2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean Skov Them on 12-04-2016.
 */

// class for storing the elements of the play queue, and their ranking/votes;
public class QueueElement {
    public myTrack track;
    public double weight;
    public List<String> upvoteList = new ArrayList<>();
    public List<String> downvoteList = new ArrayList<>();

    // makes sure the values are always 0 at creation;
    public void onCreate(){
        this.track = null;
        this.weight = 0;
        this.upvoteList = new ArrayList<>();
        this.downvoteList = new ArrayList<>();
    }
}
