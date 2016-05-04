package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetTrackRunnable extends ThreadResponseInterface<Track> implements Runnable {
    String id;

    public GetTrackRunnable(String id, ThreadResponse<Track> delegate){
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getTrack(id)); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
