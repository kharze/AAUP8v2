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
    protected void onPreExecute(){
        if(MainActivity.mWifiDirectActivity.worker != null)
            MainActivity.mWifiDirectActivity.worker.interrupt();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Track doInBackground(String... id) {
        return MainActivity.mSpotifyAccess.mService.getTrack(id[0]);
    }

    @Override
    protected void onPostExecute(Track t){
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
