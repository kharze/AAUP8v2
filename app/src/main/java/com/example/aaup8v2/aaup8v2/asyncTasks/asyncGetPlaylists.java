package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Flaka_000 on 07-04-2016.
 */
public class asyncGetPlaylists extends AsyncTask<String, Void, Pager<PlaylistSimple>> {

    public interface AsyncResponse{
        void processFinish(Pager<PlaylistSimple> output);
    }

    public AsyncResponse delegate = null;

    public asyncGetPlaylists(AsyncResponse delegate) { this.delegate = delegate; }

    @Override
    protected void onProgressUpdate(Void... values) {}

    @Override
    protected Pager<PlaylistSimple> doInBackground(String... id) {
        try {
            Pager<PlaylistSimple> playlists = null;
            int offset = 0;
            Pager<PlaylistSimple> temp;

            do{
                Map<String, Object> options = new HashMap<>();
                options.put(MainActivity.mSpotifyAccess.mService.OFFSET, offset);
                temp = MainActivity.mSpotifyAccess.mService.getPlaylists(id[0], options);
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
            return playlists;
        } catch (Exception e) {
            e.getCause();
            return null;
        }

        /*try{
            return MainActivity.mSpotifyAccess.mService.getPlaylists(id[0]);
        } catch(Exception e) {
            return null;
        }*/
    }

    @Override
    protected void onPostExecute(Pager<PlaylistSimple> p){
        try {
            delegate.processFinish(p);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
