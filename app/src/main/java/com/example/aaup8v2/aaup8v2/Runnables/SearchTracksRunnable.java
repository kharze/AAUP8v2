package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class SearchTracksRunnable extends ThreadResponseInterface<TracksPager> implements Runnable {
    String searchTerm;

    public SearchTracksRunnable(String searchTerm, ThreadResponse<TracksPager> delegate){
        this.searchTerm = searchTerm;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.searchTracks(searchTerm)); }
        catch (Exception e){ delegate.processFinish(new TracksPager()); }
    }
}
