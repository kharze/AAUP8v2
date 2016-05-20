package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a number of tracks given a searchString.
 */
public class SearchTracksRunnable extends ThreadResponseInterface<TracksPager> implements Runnable {
    String searchTerm;
    private int resultLimit;

    public SearchTracksRunnable(String searchTerm, int resultLimit, ThreadResponse<TracksPager> delegate){
        this.searchTerm = searchTerm;
        this.resultLimit = resultLimit;
        this.delegate = delegate;
    }

    @Override
    public void run() {

        Map<String, Object> options = new HashMap<>();
        int offset = 0;
        int limit = 50;
        TracksPager results = null;
        TracksPager temp;

        do{
            options.put(SpotifyService.OFFSET, offset);
            options.put(SpotifyService.LIMIT, limit);
            temp = MainActivity.mSpotifyAccess.mService.searchTracks(searchTerm, options);
            if(results == null){
                results = temp;
            } else {
                results.tracks.items.addAll(temp.tracks.items);
            }
            offset += 50;

        }while(!temp.tracks.items.isEmpty() && offset < resultLimit);

        delegate.processFinish(results);
    }
}
