package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Album;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetAlbumRunnable extends ThreadResponseInterface<Album> implements Runnable {
    private String id;

    public GetAlbumRunnable(String id, ThreadResponse<Album> delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getAlbum(id)); }
        catch (Exception e){ delegate.processFinish(null); }
    }

}
