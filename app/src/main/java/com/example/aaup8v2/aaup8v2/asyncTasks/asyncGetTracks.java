package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Tracks;

public class asyncGetTracks extends AsyncTask<String, Void, Tracks> {

    public interface AsyncResponse {
        void processFinish(Tracks output);
    }

    public AsyncResponse delegate = null;

    public asyncGetTracks(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Tracks doInBackground(String... id) {
        try {
            Tracks tracks = null;
            int offset = 0;
            Tracks temp;

            do{
                Map<String, Object> options = new HashMap<>();
                options.put(MainActivity.mSpotifyAccess.mService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getTracks(id[0], options);
                if (tracks == null){
                    tracks = temp;
                }
                else {
                    for(int i = 0; i < temp.tracks.size(); i++){
                        tracks.tracks.add(temp.tracks.get(i));
                    }
                }
                offset += 100;
            }while ((tracks.tracks.size() % 100) == 0 && temp.tracks.size() != 0);
            return tracks;
        } catch (Exception e) {
            e.getCause();
            return null;
        }

        //return MainActivity.mSpotifyAccess.mService.getTracks(id[0]);
    }

    @Override
    protected void onPostExecute(Tracks t){
        try {
            delegate.processFinish(t);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
