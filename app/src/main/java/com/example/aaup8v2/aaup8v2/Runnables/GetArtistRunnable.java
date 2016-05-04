package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetArtistRunnable extends ThreadResponseInterface<Artist> implements Runnable {
    String id;

    public GetArtistRunnable(String id, ThreadResponse<Artist> delegate){
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtist(id)); }
        catch (Exception e) { delegate.processFinish(null); }
    }
}
