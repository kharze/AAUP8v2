package com.example.aaup8v2.aaup8v2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylist;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * Created by MSI on 14-03-2016.
 */
public class SpotifyAccess extends AppCompatActivity{

    //Spotify instance
    private SpotifyApi mSpotify = new SpotifyApi();
    public SpotifyService mService = mSpotify.getService();

    private Playlist mPlaylist;

    public void setAccessToken2(String token){
        mSpotify.setAccessToken(token);
        mService = mSpotify.getService();
    }

    //public Track getTrack(String id){
      //  Track mTrack;
        //try {
          //  new asyncGetTrack().execute(id);
            //return new Track();
       //}
        //catch (Exception e){
          //  mTrack = new Track();
           //return new Track();
        //}
    //}

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
        try {
            //mService.getPlaylist(userid, pid);
            new asyncGetPlaylist(new asyncGetPlaylist.AsyncResponse(){

                @Override
                public void processFinish(Playlist output){
                    mPlaylist = (Playlist) output;
                }
            }).execute();
            return new Playlist();
        }
        catch (RetrofitError e){
            return new Playlist();
        }

    }
}