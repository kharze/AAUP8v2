package com.example.aaup8v2.aaup8v2;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.aaup8v2.aaup8v2.fragments.QueueFragment;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

/**
 * Created by lasse on 19-04-2016.
 **/
public class MusicPlayer implements ConnectionStateCallback, PlayerNotificationCallback {

    public static Player mPlayer;
    public Activity activity;
    boolean isPlaying = false;

    public void play(){

        try{
            if(isPlaying) {
                mPlayer.resume();
            }
            else {
                mPlayer.play("spotify:track:" + MainActivity.mQueueFragment.nextSong());
                isPlaying = true;
            }
        } catch (Exception e){
            e.getMessage();
        }
    }

    public void pause(){ mPlayer.pause(); }

    @Override
    public void onLoggedIn() { Log.d("MusicPlayer", "User logged in"); }

    @Override
    public void onLoggedOut() {
        Log.d("MusicPlayer", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MusicPlayer", "Login failed" + error);
    }

    @Override
    public void onTemporaryError() {
        Log.d("MusicPlayer", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MusicPlayer", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MusicPlayer", "Playback event received: " + eventType.name());
        switch (eventType) {
            case TRACK_END:
                isPlaying = false;
                play();
                MainActivity.mQueueFragment.trackWeightIncrease();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MusicPlayer", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }
}
