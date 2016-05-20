package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artists;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a number of artist that are related to an artist, given an id.
 */
public class GetRelatedArtistsRunnable extends ThreadResponseInterface<Artists> implements Runnable {
    String artistId;

    public GetRelatedArtistsRunnable(String artistId, ThreadResponse<Artists> delegate){
        this.artistId = artistId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getRelatedArtists(artistId)); }
        catch (Exception e) { delegate.processFinish(null); }
    }
}
