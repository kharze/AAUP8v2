package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a number of artists given a searchString.
 */
public class SearchArtistsRunnable extends ThreadResponseInterface<ArtistsPager> implements Runnable {
    String searchTerm;

    public SearchArtistsRunnable(String searchTerm, ThreadResponse<ArtistsPager> delegate){
        this.searchTerm = searchTerm;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.searchArtists(searchTerm)); }
        catch (Exception e){ delegate.processFinish(new ArtistsPager()); }
    }
}
