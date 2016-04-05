package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * Created by Claus on 4/5/2016.
 */
public class asyncSearchTracks extends AsyncTask<String, Void, TracksPager> {
    public interface AsyncResponse {
        void processFinish(TracksPager output);
    }

    public AsyncResponse delegate = null;

    public asyncSearchTracks(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected TracksPager doInBackground(String... id) {
        TracksPager mTracks = MainActivity.mSpotifyAccess.mService.searchTracks(id[0]);
        return mTracks;
    }

    @Override
    protected void onPostExecute(TracksPager t){
        try {
            delegate.processFinish(t);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
