package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;
import kaaes.spotify.webapi.android.models.AudioFeaturesTracks;
import kaaes.spotify.webapi.android.models.Pager;

/**
 * Created by Flaka_000 on 14-04-2016.
 */
public class AsyncGetTracksAudioFeatures extends AsyncTask<String, Void, AudioFeaturesTracks > {

    public interface AsyncResponse{
        void processFinish(AudioFeaturesTracks output);
    }

    public AsyncResponse delegate = null;

    public AsyncGetTracksAudioFeatures(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPreExecute(){
        if(MainActivity.mWifiDirectActivity.worker != null)
            MainActivity.mWifiDirectActivity.worker.interrupt();
    }

    @Override
    protected void onProgressUpdate(Void... values){
    }

    @Override
    protected AudioFeaturesTracks doInBackground(String... id) {
        try {
            AudioFeaturesTracks tracksFeatures = null;
            String query = "";

            //Limited to a hundred ids, so we go through the provided strings, making an API call
            //for every hundred ids
            for (int i = 0; i < id.length; i++){
                query += id[i];
                if ((i+1)%100 == 0){
                    tracksFeatures.audio_features.addAll(MainActivity.mSpotifyAccess.mService.getTracksAudioFeatures(query).audio_features);
                    query = "";
                }
            }

            //Get the track features of the remaining tracks
            if (!query.isEmpty()){
                tracksFeatures.audio_features.addAll(MainActivity.mSpotifyAccess.mService.getTracksAudioFeatures(query).audio_features);
            }

            return tracksFeatures;
        } catch (Exception e) {
            e.getCause();
            return null;
        }
    }

    @Override
    protected void onPostExecute(AudioFeaturesTracks af){
        try {
            delegate.processFinish(af);
            if(MainActivity.mWifiDirectActivity.info.isGroupOwner)
                MainActivity.mWifiDirectActivity.receiveHostSpawn();
            else
                MainActivity.mWifiDirectActivity.receiveDataSpawn();
        }
        catch(Exception e){
            e.getCause();
        }
    }
}