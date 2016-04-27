package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;

/**
 * Created by Flaka_000 on 14-04-2016.
 */
public class AsyncGetTrackAudioFeatures extends AsyncTask<String, Void, AudioFeaturesTrack> {

    public interface AsyncResponse{
        void processFinish(AudioFeaturesTrack output);
    }

    public AsyncResponse delegate = null;

    public AsyncGetTrackAudioFeatures(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values){
    }

    @Override
    protected AudioFeaturesTrack doInBackground(String... trackID) {
        try{
            return MainActivity.mSpotifyAccess.mService.getTrackAudioFeatures(trackID[0]);
        }
        catch (Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(AudioFeaturesTrack af){
        try {
            delegate.processFinish(af);
        }
        catch(Exception e){
            e.getCause();
        }
    }
}