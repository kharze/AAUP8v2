package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;

/**
 * Created by MSI on 06-05-2016.
 */
public class MySavedTracksRunnable extends ThreadResponseInterface<Pager<SavedTrack>> implements Runnable {


    public MySavedTracksRunnable(ThreadResponse<Pager<SavedTrack>> delegate){
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try{ delegate.processFinish(MainActivity.mSpotifyAccess.mService.getMySavedTracks()); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
