package com.example.aaup8v2.aaup8v2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

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
    private SpotifyService mService = mSpotify.getService();

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
        Playlist result;
        try {
            //mService.getPlaylist(userid, pid);
            new asyncGetPlaylist().execute(userid, pid);
            return new Playlist();
        }
        catch (RetrofitError e){
            return new Playlist();
        }

    }


    class getTrack2 implements Runnable {
        @Override
        public void run() {

        }


    }

    public class asyncGetTrack extends AsyncTask<String, Void, Track> {
        private SpotifyApi mSpotifyApi = new SpotifyApi();
        private SpotifyService mSpotifyService = mSpotifyApi.getService();

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Track doInBackground(String... id) {
            Track mTrack = mService.getTrack(id[0]);
            return mTrack;
        }

        @Override
        protected void onPostExecute(Track t){
            try {
                MainActivity.mTextView.setText(t.name);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

    private class asyncGetPlaylist extends AsyncTask<String, Void, Playlist> {
        private SpotifyApi mSpotifyApi = new SpotifyApi();
        private SpotifyService mSpotifyService = mSpotifyApi.getService();

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Playlist doInBackground(String... id) {
            try {
                Playlist mPlaylist = mService.getPlaylist(id[0], id[1]);
                return mPlaylist;
            }
            catch (Exception e)
            {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Playlist p){
            try {
                MainActivity.mTextView.setText(p.name);
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

}
