package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Album;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetAlbumRunnable extends ThreadResponseInterface<Album> implements Runnable {
    private String albumId;

    public GetAlbumRunnable(String albumId, ThreadResponse<Album> delegate) {
        this.albumId = albumId;
        this.delegate = delegate;
    }

    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getAlbum(albumId)); }
        catch (Exception e){ delegate.processFinish(null); }
    }

}
