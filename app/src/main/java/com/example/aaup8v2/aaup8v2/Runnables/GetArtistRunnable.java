package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetArtistRunnable extends ThreadResponseInterface<Artist> implements Runnable {
    String artistId;

    public GetArtistRunnable(String artistId, ThreadResponse<Artist> delegate){
        this.artistId = artistId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtist(artistId)); }
        catch (Exception e) { delegate.processFinish(new Artist()); }
    }
}
