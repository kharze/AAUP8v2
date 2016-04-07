package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Playlist;

/**
 * Created by Claus on 4/5/2016.
 */
public class asyncGetPlaylist extends AsyncTask<String, Void, Playlist> {

    public interface AsyncResponse {
        void processFinish(Playlist output);
    }

    public AsyncResponse delegate = null;

    public asyncGetPlaylist(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Playlist doInBackground(String... id) {
        try {
            return MainActivity.mSpotifyAccess.mService.getPlaylist(id[0], id[1]);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Playlist p){
        try {
            delegate.processFinish(p);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}