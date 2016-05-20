package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by Sean Skov Them on 04-05-2016.
 * Return all playlist from a user given a userId.
 */
public class GetPlaylistsRunnable extends ThreadResponseInterface<Pager<PlaylistSimple>> implements Runnable {
    String userId;

    public GetPlaylistsRunnable(String userId, ThreadResponse<Pager<PlaylistSimple>> delegate){
        this.userId = userId;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            Pager<PlaylistSimple> playlists = null;
            int offset = 0;
            Pager<PlaylistSimple> temp;

            do{
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getPlaylists(userId, options);
                if (playlists == null){
                    playlists = temp;
                }
                else {
                    for(int i = 0; i < temp.items.size(); i++){
                        playlists.items.add(temp.items.get(i));
                    }
                }
                offset += 100;
            }while ((playlists.items.size() % 100) == 0 && temp.items.size() != 0);

            delegate.processFinish(playlists);

        } catch (Exception e) {
            delegate.processFinish(null);
        }
    }
}
