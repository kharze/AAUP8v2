package com.example.aaup8v2.aaup8v2.Runnables;

import android.util.Log;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Sean Skov Them on 06-05-2016.
 */
public class MySavedTracksRunnable extends ThreadResponseInterface<Pager<SavedTrack>> implements Runnable {

    public MySavedTracksRunnable(ThreadResponse<Pager<SavedTrack>> delegate){
        this.delegate = delegate;
    }

    @Override
    public void run() { //For some reason this only works with a callback
        MainActivity.mSpotifyAccess.mService.getMySavedTracks(new Callback<Pager<SavedTrack>>() {
            @Override
            public void success(Pager<SavedTrack> savedTrackPager, Response response) {
                delegate.processFinish(savedTrackPager);
            }
            @Override
            public void failure(RetrofitError error) {
                Log.e("TAG", "failure: ", error );
                delegate.processFinish(null);
            }
        });
    }
}
