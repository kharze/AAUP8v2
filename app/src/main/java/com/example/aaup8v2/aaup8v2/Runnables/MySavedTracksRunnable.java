package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.SavedTrack;

/**
 * Created by Sean Skov Them on 06-05-2016.
 * Returns all saved tracks for the user.
 */
public class MySavedTracksRunnable extends ThreadResponseInterface<Pager<SavedTrack>> implements Runnable {

    public MySavedTracksRunnable(ThreadResponse<Pager<SavedTrack>> delegate){
        this.delegate = delegate;
    }

    @Override
    public void run() {
        int limit = 50;
        int offset = 0;
        HashMap<String, Object> options = new HashMap<>();
        options.put(SpotifyService.LIMIT, limit);

        Pager<SavedTrack> result = new Pager<>();
        result.items = new ArrayList<>();
        Pager<SavedTrack> temp = new Pager<>();
        temp.items = new ArrayList<>();
        try {
            do {
                options.put(SpotifyService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getMySavedTracks(options);

                result.items.addAll(temp.items);

                offset += limit;
            } while (temp.items.size() != 0);

            delegate.processFinish(result);
        } catch (Exception e){
            delegate.processFinish(null);
        }
    }
}
