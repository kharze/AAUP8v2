package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artist;

public class asyncGetArtist extends AsyncTask<String, Void, Artist> {

    public interface AsyncResponse {
        void processFinish(Artist output);
    }

    public AsyncResponse delegate = null;

    public asyncGetArtist(AsyncResponse delegate){
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
    protected Artist doInBackground(String... id) {
        try {
            return MainActivity.mSpotifyAccess.mService.getArtist(id[0]);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Artist p){
        try {
            delegate.processFinish(p);
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
