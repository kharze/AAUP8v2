package com.example.aaup8v2.aaup8v2;

import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.SavedTrack;
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

    //empty constructor
    public myTrack(){};

    public myTrack(TrackSimple track){
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    };
    public myTrack(Track track){
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    };
    public myTrack(PlaylistTrack playlistTrack){
        Track track = playlistTrack.track;
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    };
    public myTrack(SavedTrack savedTrack){
        Track track = savedTrack.track;
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    };


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
    public void setMyTrack(PlaylistTrack playlistTrack){
        Track track = playlistTrack.track;
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    }
    public void setMyTrack(SavedTrack savedTrack){
        Track track = savedTrack.track;
        this.id = track.id;
        this.name = track.name;
        this.artist = track.artists.get(0).name;
        this.duration_ms = track.duration_ms;
    }
}
