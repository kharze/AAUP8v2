package com.example.aaup8v2.aaup8v2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.RetrofitError;

/**
 * Created by MSI on 14-03-2016.
 */
public class SpotifyAccess extends AppCompatActivity{

    //Spotify instance
    private SpotifyApi mSpotify = new SpotifyApi();
    public SpotifyService mService = mSpotify.getService();

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

    public class asyncGetPlaylist extends AsyncTask<String, Void, Playlist> {
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

    public class asyncGetArtists extends AsyncTask<String, Void, Artists> {
        private SpotifyApi mSpotifyApi = new SpotifyApi();
        private SpotifyService mSpotifyService = mSpotifyApi.getService();

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Artists doInBackground(String... id) {
            try {
                Artists mArtists = mService.getArtists(id[0]);
                return mArtists;
            }
            catch (Exception e)
            {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Artists p){
            try {
                MainActivity.mTextView.setText(p.describeContents());
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

    public class asyncGetPlaylistTracks extends AsyncTask<String, Void, Pager> {
        private SpotifyApi mSpotifyApi = new SpotifyApi();
        private SpotifyService mSpotifyService = mSpotifyApi.getService();

        @Override
        protected void onProgressUpdate(Void... values) {
        }

        @Override
        protected Pager doInBackground(String... id) {
            try {
                Pager mPlaylistTracks = mService.getPlaylistTracks(id[0], id[1]);
                return mPlaylistTracks;
            }
            catch (Exception e)
            {
                return null;
            }

        }

        @Override
        protected void onPostExecute(Pager p){
            try {
                MainActivity.mTextView.setText(p.next.length());
            }
            catch (Exception e)
            {
                e.getCause();
            }
        }

    }

}
