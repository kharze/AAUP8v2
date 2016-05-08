package com.example.aaup8v2.aaup8v2.Runnables;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

/**
 * Created by Sean Skov Them on 04-05-2016.
 */
public class GetPlaylistTracksRunnable extends ThreadResponseInterface<Pager<PlaylistTrack>> implements Runnable {
    String userID;
    String playlistID;

    public GetPlaylistTracksRunnable(String userID, String playlistID, ThreadResponse<Pager<PlaylistTrack>> delegate){
        this.userID = userID;
        this.playlistID = playlistID;
        this.delegate = delegate;
    }

    @Override
    public void run() {
        try {
            Pager<PlaylistTrack> tracks = null;
            int offset = 0;
            Pager<PlaylistTrack> temp;

            do{
                Map<String, Object> options = new HashMap<>();
                options.put(SpotifyService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getPlaylistTracks(userID, playlistID, options);
                if (tracks == null){
                    tracks = temp;
                }
                else {
                    for(int i = 0; i < temp.items.size(); i++){
                        tracks.items.add(temp.items.get(i));
                    }
                }
                offset += 100;
            }while ((tracks.items.size() % 100) == 0 && temp.items.size() != 0);

            delegate.processFinish(tracks);

        } catch (Exception e) {
            delegate.processFinish(new Pager<PlaylistTrack>());
        }
    }
}
