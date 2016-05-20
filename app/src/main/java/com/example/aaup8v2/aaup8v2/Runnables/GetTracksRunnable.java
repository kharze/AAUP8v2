package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a number of tracks given a list of trackIds.
 */
public class GetTracksRunnable extends ThreadResponseInterface<Tracks> implements Runnable {
    List<String> trackIds;

    public GetTracksRunnable(List<String> trackIds, ThreadResponse<Tracks> delegate){
        this.trackIds = trackIds;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {

            Tracks result = new Tracks();
            result.tracks = new ArrayList<>();
            do {
                String requestTrackIds = null;
                int counter = 0;
                do {
                    if (requestTrackIds == null) {
                        requestTrackIds = trackIds.get(0);
                        this.trackIds.remove(0);
                    } else {
                        requestTrackIds += "," + trackIds.get(0);
                        trackIds.remove(0);
                    }
                    counter++;
                } while (counter < 50 && !trackIds.isEmpty());

                result.tracks.addAll(MainActivity.mSpotifyAccess.mService.getTracks(requestTrackIds).tracks);
            } while (!trackIds.isEmpty());

            delegate.processFinish(result);

        } catch (Exception e) {
            delegate.processFinish(null);
        }
    }
}
