package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetArtistTopTrackRunnable extends ThreadResponseInterface<Tracks> implements Runnable {
    String artistId;

    public GetArtistTopTrackRunnable(String artistId, ThreadResponse<Tracks> delegate){
        this.artistId = artistId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getArtistTopTrack(artistId, MainActivity.me.country)); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
