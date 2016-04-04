package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.SpotifyAccess;

import java.util.concurrent.ExecutionException;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;

/**
 * Created by lasse on 21-03-2016.
 */

public class PlaylistRecommend extends MainActivity{

    public void formatPlaylist (){
        try {
            Pager tracks = mSpotifyAccess.new asyncGetPlaylistTracks().execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV").get();
            int x;
        } catch (Exception e) {

        }

    }
}

