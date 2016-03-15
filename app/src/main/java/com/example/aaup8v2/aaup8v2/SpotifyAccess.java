package com.example.aaup8v2.aaup8v2;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * Created by MSI on 14-03-2016.
 */
public class SpotifyAccess {

    //Spotify instance
    private SpotifyApi mSpotify = new SpotifyApi();
    private SpotifyService mService;

    public void setAccessToken(String token){

        //mService = mSpotify.setAccessToken(AuthenticationClient.getResponse().getAccessToken()).getService();
    }

    public Track getTrack(String id){
        Track mTrack;
        try {
            mTrack = mService.getTrack(id);
        }
        catch (RetrofitError e){
            return new Track();
        }
        return mTrack;
    }

    public Album getAlbum(String id){
        Album mAlbum;

        try {
            mAlbum = mService.getAlbum(id);
        }
        catch (RetrofitError e){
            return new Album();
        }
        return mAlbum;
    }

    public Playlist getPlaylist(String userid, String pid) {
        Playlist result;

        try {
            result = mService.getPlaylist(userid, pid);
        }
        catch (RetrofitError e){
            return new Playlist();
        }
        return result;
    }

}
