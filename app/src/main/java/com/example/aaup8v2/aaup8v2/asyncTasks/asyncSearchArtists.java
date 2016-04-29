package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by Claus on 4/5/2016.
 **/
public class asyncSearchArtists extends AsyncTask<String, Void, ArtistsPager> {
    public interface AsyncResponse {
        void processFinish(ArtistsPager output);
    }

    public AsyncResponse delegate = null;

    public asyncSearchArtists(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute(){
        if(MainActivity.mWifiDirectActivity.worker != null)
            MainActivity.mWifiDirectActivity.worker.interrupt();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected ArtistsPager doInBackground(String... id) {
        return MainActivity.mSpotifyAccess.mService.searchArtists(id[0]);
    }

    @Override
    protected void onPostExecute(ArtistsPager t){
        try {
            delegate.processFinish(t);
            if(MainActivity.mWifiDirectActivity.info.isGroupOwner)
                MainActivity.mWifiDirectActivity.receiveHostSpawn();
            else
                MainActivity.mWifiDirectActivity.receiveDataSpawn();
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
