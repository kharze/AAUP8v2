package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Albums;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * Created by Claus on 4/5/2016.
 */
public class asyncSearchMusic extends AsyncTask<String, Void, List> {
    public interface AsyncResponse {
        void processFinish(List output);
    }

    public AsyncResponse delegate = null;

    public asyncSearchMusic(AsyncResponse delegate){
        this.delegate = delegate;
    }


    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected List doInBackground(String... id) {
        List mSearchTracks = null;

        int i, j, k, l, m;
        Pager mArtistAlbums;
        Album mAlbums = null;
        Pager mAlbumTracks = null;
        List<String> albumsList = new ArrayList<>();

        TracksPager mTracks = MainActivity.mSpotifyAccess.mService.searchTracks(id[0]);

        mSearchTracks = mTracks.tracks.items;



        ArtistsPager mArtists = MainActivity.mSpotifyAccess.mService.searchArtists(id[0]);


        for(i = 0; i < mArtists.artists.items.size(); i++){
            mArtistAlbums = MainActivity.mSpotifyAccess.mService.getArtistAlbums(mArtists.artists.items.get(i).id);

            List<Album> mArtistAlbumsHelper = mArtistAlbums.items;

            for(j=0; j < mArtistAlbumsHelper.size(); j++){
                albumsList.add(mArtistAlbumsHelper.get(j).id);
            }

            do{
                String albumRequests = null;
                int counter = 0;
                do{
                    if(albumRequests == null){
                        albumRequests = albumsList.get(0);
                        albumsList.remove(0);
                        counter++;
                    }
                    else {
                        albumRequests += "," + albumsList.get(0);
                        albumsList.remove(0);
                        counter++;
                    }
                }while (counter < 50 && !albumsList.isEmpty());

                Albums temp2 = MainActivity.mSpotifyAccess.mService.getAlbums(albumRequests);

                List<Album> temp3 = temp2.albums;

                for(k = 0; k < temp3.size(); k++){
                    for(l = 0; l < temp3.get(k).tracks.items.size(); l++){
                        if (!mSearchTracks.contains(temp3.get(k).tracks.items.get(l))) {
                            mSearchTracks.add(temp3.get(k).tracks.items.get(l));
                        }
                    }
                }

            }while (!albumsList.isEmpty());

        }

        return mSearchTracks;
    }

    @Override
    protected void onPostExecute(List t){
        try {
            delegate.processFinish(t);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
