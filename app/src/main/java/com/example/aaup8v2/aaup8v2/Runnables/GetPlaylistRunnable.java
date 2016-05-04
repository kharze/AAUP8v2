package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Playlist;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetPlaylistRunnable extends ThreadResponseInterface<Playlist> implements Runnable {
    String userID;
    String playlistID;

    public GetPlaylistRunnable(String userID, String playlistID, ThreadResponse<Playlist> delegate){
        this.userID = userID;
        this.playlistID = playlistID;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getPlaylist(userID, playlistID)); }
        catch (Exception e) { delegate.processFinish(null); }
    }
}
