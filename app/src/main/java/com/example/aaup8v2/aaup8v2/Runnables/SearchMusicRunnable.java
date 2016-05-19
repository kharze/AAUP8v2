package com.example.aaup8v2.aaup8v2.Runnables;

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
 * Created by Sean Skov Them on 04-05-2016.
 * Returns a number of tracks given a searchString.
 */

public class SearchMusicRunnable extends ThreadResponseInterface<List<myTrack>> implements Runnable {
    //private static final String TAG = "Search music runnable:";
    private String searchTerm;
    List<myTrack> mSearchTracks = new ArrayList<>();

    public SearchMusicRunnable(String searchTerm, ThreadResponse<List<myTrack>> delegate) {
        this.searchTerm = searchTerm;
        this.delegate = delegate;
    }

    List<Track> mTracksHelper = new ArrayList<>();
    public void run() {
        Pager<Album> mArtistAlbums;
        List<String> albumsList = new ArrayList<>();
        mTracksHelper.clear();


        //Call to the Spotify API searchTracks function, returns a TracksPager object containing the search result.
        new SearchTracksRunnable(searchTerm, 1000 , new ThreadResponse<TracksPager>() {
            @Override
            public void processFinish(TracksPager output) {
                mTracksHelper = output.tracks.items;
            }
        }).run();

        //Add found tracks to the return list.
        for(int i = 0; i < mTracksHelper.size(); i++){
            myTrack temp = new myTrack(mTracksHelper.get(i));
            mSearchTracks.add(temp);
        }

        //Call to the Spotify API searchArtists function, returns a ArtistsPager object containing the search result.
        ArtistsPager mArtists = MainActivity.mSpotifyAccess.mService.searchArtists(searchTerm);

        //Go through each result from the artist search.
        for(int i = 0; i < mArtists.artists.items.size(); i++){
            //Get the albums an artist have made, as this is the only way to find which tracks an artist have made.
            //Call to the Spotify API getArtistAlbums function, returns a Pager object.
            mArtistAlbums = MainActivity.mSpotifyAccess.mService.getArtistAlbums(mArtists.artists.items.get(i).id);

            //Helper to make cleaner code, the call is safe despite the warning.
            final List<Album> mArtistAlbumsHelper = mArtistAlbums.items;

            //Populate the list of albums ids
            for(int j = 0; mArtistAlbumsHelper.size() > j; j++){
                albumsList.add(mArtistAlbumsHelper.get(j).id);
            }

            new GetAlbumsRunnable(albumsList, new ThreadResponse<Albums>() {
                @Override
                public void processFinish(Albums albums) {
                    List<Album> mAlbumsHelper = albums.albums;
                    for(int j = 0; j < mAlbumsHelper.size(); j++){
                        for(int l = 0; l < mAlbumsHelper.get(j).tracks.items.size(); l++){
                            myTrack temp = new myTrack(mAlbumsHelper.get(j).tracks.items.get(l));
                            mSearchTracks.add(temp);
                        }
                    }
                }
            }).run();
        }

        //Sort on id
        Collections.sort(mSearchTracks, new Comparator<myTrack>() {
            @Override
            public int compare(myTrack lhs, myTrack rhs) {
                return lhs.id.compareTo(rhs.id);
            }
        });

        //Log.d(TAG, "run: with dublicates " + Integer.toString(mSearchTracks.size()));

        //Remove duplicates
        for(int i = 0; i < mSearchTracks.size()-1; i++){
            if(mSearchTracks.get(i).id.equals(mSearchTracks.get(i+1).id)){
                mSearchTracks.remove(i+1);
            }
        }

        //Log.d(TAG, "run: without dublicates " + Integer.toString(mSearchTracks.size()));

        delegate.processFinish(mSearchTracks);
    }
}
