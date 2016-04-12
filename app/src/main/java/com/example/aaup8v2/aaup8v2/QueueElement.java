package com.example.aaup8v2.aaup8v2;

/**
 * Created by MSI on 12-04-2016.
 */

// class for storing the elements of the play queue, and their ranking/votes;
public class QueueElement {
    public myTrack track;
    public int upVotes;
    public int downVotes;
    public int rank;

    // makes sure the values are always 0 at creation;
    public void onCreate(){
        this.track = null;
        this.upVotes = 0;
        this.downVotes = 0;
        this.rank = 0;
    }
}