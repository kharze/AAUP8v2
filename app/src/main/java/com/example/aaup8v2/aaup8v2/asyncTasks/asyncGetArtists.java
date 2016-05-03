package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artists;

public class asyncGetArtists extends AsyncTask<String, Void, Artists> {

    public interface AsyncResponse {
        void processFinish(Artists output);
    }

    public AsyncResponse delegate = null;

    public asyncGetArtists(AsyncResponse delegate){
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
    protected Artists doInBackground(String... id) {
        try {
            return MainActivity.mSpotifyAccess.mService.getArtists(id[0]);
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Artists p){
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
