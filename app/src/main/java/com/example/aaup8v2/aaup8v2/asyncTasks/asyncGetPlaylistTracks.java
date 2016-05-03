package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

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
    protected void onPreExecute(){
        if(MainActivity.mWifiDirectActivity.worker != null)
            MainActivity.mWifiDirectActivity.worker.interrupt();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Pager doInBackground(String... id) {
        try {
            Pager tracks = null;
            int offset = 0;
            Pager temp;
            /**
            Map<String, Object> options = new HashMap<>();
            options.put(MainActivity.mSpotifyAccess.mService.OFFSET, offset);
            tracks = MainActivity.mSpotifyAccess.mService.getPlaylistTracks(id[0], id[1], options);
             **/
            do{
                Map<String, Object> options = new HashMap<>();
                options.put(MainActivity.mSpotifyAccess.mService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getPlaylistTracks(id[0], id[1], options);
                if (tracks == null){
                    tracks = temp;
                }
                else {
                    for(int i = 0; i < temp.items.size(); i++){
                        tracks.items.add(temp.items.get(i));
                    }
                }
                offset += 100;
            }while ((tracks.items.size() % 100) == 0 && temp.items.size() != 0);
            return tracks;
        }
        catch (Exception e)
        {
            e.getCause();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Pager p){
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
