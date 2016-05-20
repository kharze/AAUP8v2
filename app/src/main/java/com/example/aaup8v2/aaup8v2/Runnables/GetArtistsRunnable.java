package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artists;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * This Runnable
 */
public class GetArtistsRunnable extends ThreadResponseInterface<Artists> implements Runnable {
    List<String> artistIds;

    public GetArtistsRunnable(List<String> artistIds, ThreadResponse<Artists> delegate){
        this.artistIds = artistIds;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        Artists result = new Artists();
        result.artists = new ArrayList<>();
        do {
            String requestArtistIds = null;
            int counter = 0;
            do {
                if (requestArtistIds == null) {
                    requestArtistIds = artistIds.get(0);
                    this.artistIds.remove(0);
                } else {
                    requestArtistIds += "," + artistIds.get(0);
                    artistIds.remove(0);
                }
                counter++;
            } while (counter < 50 && !artistIds.isEmpty());

            result.artists.addAll(MainActivity.mSpotifyAccess.mService.getArtists(requestArtistIds).artists);
        } while (!artistIds.isEmpty());

        delegate.processFinish(result);
    }
}
