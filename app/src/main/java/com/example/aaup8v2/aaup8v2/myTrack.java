package com.example.aaup8v2.aaup8v2;

/**
 * Created by Claus on 4/7/2016.
 *
 * Class of Tracks, contains id, name, and duration of a track
 */
public class myTrack {
    public String id;
    public String name;
    public long duration_ms;

    //public String getId(){return id;}

    public myTrack(String i, String n, long d){
        this.id = i;
        this.name = n;
        this.duration_ms = d;
    }

    //@Override
    /*public int compareTo(myTrack t) {
        if (id.compareTo(t.id) > 0) {
            return 1;
        }
        else if (id.compareTo(t.id) < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }*/
}
