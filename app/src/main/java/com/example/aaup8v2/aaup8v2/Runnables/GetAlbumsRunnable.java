package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Albums;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetAlbumsRunnable extends ThreadResponseInterface<Albums> implements Runnable {
    private String id;

    public GetAlbumsRunnable(String id, ThreadResponse<Albums> delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getAlbums(id)); }
        catch (Exception e) { delegate.processFinish(null); }
    }
}
