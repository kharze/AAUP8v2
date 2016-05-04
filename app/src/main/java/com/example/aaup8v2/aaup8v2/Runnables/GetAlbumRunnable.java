package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Album;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetAlbumRunnable implements Runnable {
    private String id;

    public interface ThreadResponse {
        void processFinish(Album output);
    }
    public ThreadResponse delegate = null;

    public GetAlbumRunnable(String id, ThreadResponse delegate) {
        this.id = id;
        this.delegate = delegate;
    }

    public void run() {
        Album result = MainActivity.mSpotifyAccess.mService.getAlbum(id);

        delegate.processFinish(result);
    }
}
