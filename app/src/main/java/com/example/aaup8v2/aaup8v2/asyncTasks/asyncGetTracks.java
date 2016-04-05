package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

public class asyncGetTracks extends AsyncTask<String, Void, Tracks> {

    public interface AsyncResponse {
        void processFinish(Tracks output);
    }

    public AsyncResponse delegate = null;

    public asyncGetTracks(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Tracks doInBackground(String... id) {
        Tracks mTracks = MainActivity.mSpotifyAccess.mService.getTracks(id[0]);
        return mTracks;
    }

    @Override
    protected void onPostExecute(Tracks t){
        try {
            delegate.processFinish(t);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
