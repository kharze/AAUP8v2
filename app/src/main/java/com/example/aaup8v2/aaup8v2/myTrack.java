package com.example.aaup8v2.aaup8v2;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TrackSimple;

/**
 * Created by Claus on 4/7/2016.
 *
 * Class of Tracks, contains id, name, and duration of a track
 */
public class myTrack {
    public String id;
    public String name;
    public String artist;
    public long duration_ms;

    //public String getId(){return id;}

    public myTrack(String i, String n, long d){
        this.id = i;
        this.name = n;
        this.duration_ms = d;
    }

    public void setArtist(String a){this.artist = a;}

    public void setMyTrack(TrackSimple track){
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    }

    public void setMyTrack(Track track){
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    }
}
