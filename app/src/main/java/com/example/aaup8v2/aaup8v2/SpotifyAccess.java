package com.example.aaup8v2.aaup8v2;

import android.support.v7.app.AppCompatActivity;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * Created by Sean Skov Them on 14-03-2016.
 */
public class SpotifyAccess extends AppCompatActivity{

    //Spotify instance
    private SpotifyApi mSpotify = new SpotifyApi();
    public SpotifyService mService = mSpotify.getService();


    public void setAccessToken(String token){
        mSpotify.setAccessToken(token);
        mService = mSpotify.getService(); //Create an instance of a Spotify service, to use for web requests.
    }
}