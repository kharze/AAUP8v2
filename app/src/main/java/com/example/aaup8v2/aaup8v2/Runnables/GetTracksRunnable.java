package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetTracksRunnable extends ThreadResponseInterface<Tracks> implements Runnable {
    String trackIds;

    public GetTracksRunnable(String trackIds, ThreadResponse<Tracks> delegate){
        this.trackIds = trackIds;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            Tracks tracks = null;
            int offset = 0;
            Tracks temp;

            do{
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getTracks(trackIds, options);
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

            delegate.processFinish(tracks);

        } catch (Exception e) {
            delegate.processFinish(new Tracks());
        }
    }
}
