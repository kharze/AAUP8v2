package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Pager;

public class asyncGetPlaylistTracks extends AsyncTask<String, Void, Pager> {

    public interface AsyncResponse {
        void processFinish(Pager output);
    }

    public AsyncResponse delegate = null;

    public asyncGetPlaylistTracks(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Pager doInBackground(String... id) {
        try {
            Pager mPlaylistTracks = MainActivity.mSpotifyAccess.mService.getPlaylistTracks(id[0], id[1]);
            return mPlaylistTracks;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Pager p){
        try {
            delegate.processFinish(p);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
