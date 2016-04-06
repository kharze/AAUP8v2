package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Track;

public class asyncGetTrack extends AsyncTask<String, Void, Track> {

    public interface AsyncResponse {
        void processFinish(Track output);
    }

    public AsyncResponse delegate = null;

    public asyncGetTrack(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Track doInBackground(String... id) {
        Track mTrack = MainActivity.mSpotifyAccess.mService.getTrack(id[0]);
        return mTrack;
    }

    @Override
    protected void onPostExecute(Track t){
        try {
            delegate.processFinish(t);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
