package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artists;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetArtistsRunnable extends ThreadResponseInterface<Artists> implements Runnable {
    String artistIds;

    public GetArtistsRunnable(String artistIds, ThreadResponse<Artists> delegate){
        this.artistIds = artistIds;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtists(artistIds)); }
        catch (Exception e) { delegate.processFinish(new Artists()); }
    }
}
