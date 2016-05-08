package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Albums;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetAlbumsRunnable extends ThreadResponseInterface<Albums> implements Runnable {
    private List<String> artistIds;

    public GetAlbumsRunnable(List<String> artistIds, ThreadResponse<Albums> delegate) {
        this.artistIds = artistIds;
        this.delegate = delegate;
    }

    public void run() {
        Albums albums = new Albums();
        albums.albums = new ArrayList<>();
        do {
            String albumIds = null;
            int counter = 0;
            do {
                if (albumIds == null) {
                    albumIds = artistIds.get(0);
                    artistIds.remove(0);
                } else {
                    albumIds += "," + artistIds.get(0);
                    artistIds.remove(0);
                }
                counter++;
            } while (counter < 50 && !artistIds.isEmpty());

            albums.albums.addAll(MainActivity.mSpotifyAccess.mService.getAlbums(albumIds).albums);
        } while (!artistIds.isEmpty());

        delegate.processFinish(albums);

    }
}
