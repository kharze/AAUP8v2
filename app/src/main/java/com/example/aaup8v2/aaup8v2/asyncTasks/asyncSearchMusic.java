package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.myTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Albums;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;

/**
 * Created by Claus on 4/5/2016.
 *
 * Async task to handle the music searching
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
        List<myTrack> mSearchTracks = new ArrayList<>();

        int i, j, k, l, m, n;
        Pager mArtistAlbums;
        List<String> albumsList = new ArrayList<>();

        TracksPager mTracks = MainActivity.mSpotifyAccess.mService.searchTracks(id[0]);

        List<Track> mTracksHelper = mTracks.tracks.items;

        for(n = 0; n < mTracksHelper.size(); n++){
            myTrack temp = new myTrack(mTracksHelper.get(n).id, mTracksHelper.get(n).name, mTracksHelper.get(n).duration_ms);
            temp.setArtist(mTracksHelper.get(n).artists.get(0).name);
            mSearchTracks.add(temp);
        }

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

                Albums mAlbums = MainActivity.mSpotifyAccess.mService.getAlbums(albumRequests);

                List<Album> temp3 = mAlbums.albums;

                for(k = 0; k < temp3.size(); k++){
                    for(l = 0; l < temp3.get(k).tracks.items.size(); l++){
                        myTrack temp = new myTrack(temp3.get(k).tracks.items.get(l).id, temp3.get(k).tracks.items.get(l).name, temp3.get(k).tracks.items.get(l).duration_ms);
                        temp.setArtist(temp3.get(k).tracks.items.get(l).artists.get(0).name);
                        mSearchTracks.add(temp);
                    }
                }

            }while (!albumsList.isEmpty());

        }

        //sort on id
        Collections.sort(mSearchTracks, new Comparator<myTrack>() {
            @Override
            public int compare(myTrack lhs, myTrack rhs) {
                return lhs.id.compareTo(rhs.id);
            }
        });

        for(m = 0; m < mSearchTracks.size()-1; m++){
            if(mSearchTracks.get(m).id.equals(mSearchTracks.get(m+1).id)){
                mSearchTracks.remove(m+1);
            }
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
