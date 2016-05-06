package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Albums;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetAlbumsRunnable extends ThreadResponseInterface<Albums> implements Runnable {
    private String artistId;

    public GetAlbumsRunnable(String artistId, ThreadResponse<Albums> delegate) {
        this.artistId = artistId;
        this.delegate = delegate;
    }

    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getAlbums(artistId)); }
        catch (Exception e) { delegate.processFinish(new Albums()); }
    }
}
