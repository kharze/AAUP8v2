package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artists;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetArtistsRunnable extends ThreadResponseInterface<Artists> implements Runnable {
    String id;

    public GetArtistsRunnable(String id, ThreadResponse<Artists> delegate){
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtists(id)); }
        catch (Exception e) { delegate.processFinish(null); }
    }
}
