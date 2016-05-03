package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by lasse on 07-04-2016.
 */
public class asyncGetArtistTopTrack extends AsyncTask<String, Void, Tracks>{
    public interface AsyncResponse {
        void processFinish(Tracks output);
    }

    public AsyncResponse delegate = null;

    public asyncGetArtistTopTrack(AsyncResponse delegate){
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
    protected Tracks doInBackground(String... id) {
        try{
            Tracks mTracks = MainActivity.mSpotifyAccess.mService.getArtistTopTrack(id[0], "DK");
            return mTracks;

        }catch (Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(Tracks t){
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
