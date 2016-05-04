package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetArtistTopTrackRunnable extends ThreadResponseInterface<Tracks> implements Runnable {
    String id;

    public GetArtistTopTrackRunnable(String id, ThreadResponse<Tracks> delegate){
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtistTopTrack(id, MainActivity.me.country)); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
