package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by Flaka_000 on 07-04-2016.
 */
public class asyncGetPlaylists extends AsyncTask<String, Void, Pager<PlaylistSimple>> {

    public interface AsyncResponse{
        void processFinish(Pager<PlaylistSimple> output);
    }

    public AsyncResponse delegate = null;

    public asyncGetPlaylists(AsyncResponse delegate) { this.delegate = delegate; }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected Pager<PlaylistSimple> doInBackground(String... id) {
        try{
            return MainActivity.mSpotifyAccess.mService.getPlaylists(id[0]);
        } catch(Exception e) {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Pager<PlaylistSimple> p){
        try {
            delegate.processFinish(p);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
