package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Albums;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetAlbumsRunnable extends ThreadResponseInterface<Albums> implements Runnable {
    private List<String> albumIds;

    public GetAlbumsRunnable(List<String> albumIds, ThreadResponse<Albums> delegate) {
        this.albumIds = albumIds;
        this.delegate = delegate;
    }

    public void run() {
        Albums albums = new Albums();
        albums.albums = new ArrayList<>();
        do {
            String requestAlbumIds = null;
            int counter = 0;
            do {
                if (requestAlbumIds == null) {
                    requestAlbumIds = albumIds.get(0);
                    this.albumIds.remove(0);
                } else {
                    requestAlbumIds += "," + albumIds.get(0);
                    albumIds.remove(0);
                }
                counter++;
            } while (counter < 50 && !albumIds.isEmpty());

            albums.albums.addAll(MainActivity.mSpotifyAccess.mService.getAlbums(requestAlbumIds).albums);
        } while (!albumIds.isEmpty());

        delegate.processFinish(albums);

    }
}
