package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.AudioFeaturesTrack;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetTrackAudioFeatures extends ThreadResponseInterface<AudioFeaturesTrack> implements Runnable {
    String trackId;

    public GetTrackAudioFeatures(String trackId, ThreadResponse<AudioFeaturesTrack> delegate){
        this.trackId = trackId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try { delegate.processFinish(MainActivity.mSpotifyAccess.mService.getTrackAudioFeatures(trackId)); }
        catch (Exception e){ delegate.processFinish(new AudioFeaturesTrack()); }
    }
}
