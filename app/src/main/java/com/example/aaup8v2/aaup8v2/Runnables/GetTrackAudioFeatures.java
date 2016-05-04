package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;

/**
 * Created by MSI on 04-05-2016.
 */
public class GetTrackAudioFeatures extends ThreadResponseInterface<AudioFeaturesTrack> implements Runnable {
    String id;

    public GetTrackAudioFeatures(String id, ThreadResponse<AudioFeaturesTrack> delegate){
        this.id = id;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getTrackAudioFeatures(id)); }
        catch (Exception e){ delegate.processFinish(null); }
    }
}
