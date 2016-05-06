package com.example.aaup8v2.aaup8v2;

import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistSimple;
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
    public List<ArtistSimple> artists;
    public long duration_ms;

    //empty constructor
    public myTrack(){}

    public myTrack(TrackSimple track){ setMyTrack(track); }

    public myTrack(Track track){ setMyTrack((TrackSimple)track); }

    public myTrack(PlaylistTrack playlistTrack){ setMyTrack((TrackSimple)playlistTrack.track); }

    public myTrack(SavedTrack savedTrack){ setMyTrack((TrackSimple)savedTrack.track); }

    public void setMyTrack(TrackSimple track){
        this.id = track.id;
        this.name = track.name;
        this.artists = track.artists;
        this.duration_ms = track.duration_ms;
    }

    public void setMyTrack(Track track){ setMyTrack((TrackSimple) track); }

    public void setMyTrack(PlaylistTrack playlistTrack){ setMyTrack((TrackSimple)playlistTrack.track); }

    public void setMyTrack(SavedTrack savedTrack){ setMyTrack((TrackSimple)savedTrack.track); }

}
