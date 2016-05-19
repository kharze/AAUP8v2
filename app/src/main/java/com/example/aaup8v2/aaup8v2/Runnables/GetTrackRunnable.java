package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a Track given a trackId.
 */
public class GetTrackRunnable extends ThreadResponseInterface<Track> implements Runnable {
    String trackId;

    public GetTrackRunnable(String trackId, ThreadResponse<Track> delegate){
        this.trackId = trackId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getTrack(trackId)); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
