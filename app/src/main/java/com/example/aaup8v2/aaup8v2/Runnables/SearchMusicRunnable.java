package com.example.aaup8v2.aaup8v2.Runnables;

import android.app.Activity;

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
 * Created by MSI on 04-05-2016.
 */
public class SearchMusicRunnable implements Runnable {
    private String id;
    Runnable onUIThread;
    Activity activity;
    static public List<myTrack> output;

    public SearchMusicRunnable(String id, Activity activity, Runnable onUIThread) {
        this.id = id;
        this.onUIThread = onUIThread;
        this.activity = activity;
    }

    private void updateUI(List<myTrack> output){
        SearchMusicRunnable.output = output;
        activity.runOnUiThread(onUIThread);
    }


    public void run() {
        List<myTrack> mSearchTracks = new ArrayList<>();
        Pager mArtistAlbums;
        List<String> albumsList = new ArrayList<>();

        try {
            //Call to the Spotify API searchTracks function, returns a TracksPager object containing the search result.
            TracksPager mTracks = MainActivity.mSpotifyAccess.mService.searchTracks(id);

            //Extracting the list of tracks to make code cleaner.
            List<Track> mTracksHelper = mTracks.tracks.items;

            //Add found tracks to the return list.
            for(int i = 0; i < mTracksHelper.size(); i++){
                myTrack temp = new myTrack();
                temp.setMyTrack(mTracksHelper.get(i));
                mSearchTracks.add(temp);
            }

            //Call to the Spotify API searchArtists function, returns a ArtistsPager object containing the search result.
            ArtistsPager mArtists = MainActivity.mSpotifyAccess.mService.searchArtists(id);

            //Go through each result from the artist search.
            for(int i = 0; i < mArtists.artists.items.size(); i++){
                //Get the albums an artist have made, as this is the only way to find which tracks an artist have made.
                //Call to the Spotify API getArtistAlbums function, returns a Pager object.
                mArtistAlbums = MainActivity.mSpotifyAccess.mService.getArtistAlbums(mArtists.artists.items.get(i).id);

                //Helper to make cleaner code, the call is safe despite the warning.
                List<Album> mArtistAlbumsHelper = mArtistAlbums.items;

                //Populate the list of albums ids
                for(int j=0; j < mArtistAlbumsHelper.size(); j++){
                    albumsList.add(mArtistAlbumsHelper.get(j).id);
                }

                //To reduce the number of requests to Spotify we get multiple albums in one request, limit of 50 albums, so we loop it.
                do{
                    String albumRequests = null;
                    int counter = 0;

                    //Append the request string with up to 50 album ids.
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

                    //Call to Spotify API getAlbums function with the request string, returns the albums.
                    Albums mAlbums = MainActivity.mSpotifyAccess.mService.getAlbums(albumRequests);
                    List<Album> mAlbumsHelper = mAlbums.albums;

                    //Go through all the albums, extract the tracks, and add them to the return list.
                    for(int j = 0; j < mAlbumsHelper.size(); j++){
                        for(int l = 0; l < mAlbumsHelper.get(j).tracks.items.size(); l++){
                            myTrack temp = new myTrack();
                            temp.setMyTrack(mAlbumsHelper.get(j).tracks.items.get(l));
                            mSearchTracks.add(temp);
                        }
                    }
                }while (!albumsList.isEmpty());
            }

            //Sort on id
            Collections.sort(mSearchTracks, new Comparator<myTrack>() {
                @Override
                public int compare(myTrack lhs, myTrack rhs) {
                    return lhs.id.compareTo(rhs.id);
                }
            });

            //Remove duplicates
            for(int i = 0; i < mSearchTracks.size()-1; i++){
                if(mSearchTracks.get(i).id.equals(mSearchTracks.get(i+1).id)){
                    mSearchTracks.remove(i+1);
                }
            }
            updateUI(mSearchTracks);
        } catch (Exception e) {
            e.printStackTrace();
            updateUI(null);
        }
    }
}
