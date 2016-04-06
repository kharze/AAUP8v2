package com.example.aaup8v2.aaup8v2.recommender_pearson;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by lasse on 21-03-2016.
 */

public class PearsonRecommend{
    public void myMethod(){
        Pager tracksPager= null;
        Track track;
        Track t = null;
        Artists mArtists = null;
        List artistsList = new ArrayList();
        try{
            //t = MainActivity.mSpotifyAccess.new asyncGetTrack().execute("3Y3gChJoEUTqrtuqJCIPQk").get();
            //tracksPager = MainActivity.mSpotifyAccess.new asyncGetPlaylistTracks().execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV").get();
        }catch (Exception e){
        }

        List<PlaylistTrack> tracksList = tracksPager.items;
        for(int j=0; j < tracksList.size(); j++){
            track = tracksList.get(j).track;
            for(int i=0; i < track.artists.size(); i++){
                artistsList.add(track.artists.get(i).id);
            }
        }
        List genres = new ArrayList();
        for(int i = 0; i < artistsList.size(); i++) {
        }
        try
        {
            //mArtists = MainActivity.mSpotifyAccess.new asyncGetArtists().execute("2qPIOBAKYc1SQI1QHDV4EV").get();
        }catch (Exception e){

        }

        int temp = mArtists.artists.size();
        for (Artist artist : mArtists.artists) {

        }
        for (int i = 0; i < mArtists.artists.size(); i ++){

        }

        int o = 10;
    }

}

