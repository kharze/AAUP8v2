package com.example.aaup8v2.aaup8v2;

import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import java.util.List;
import java.util.Random;

/**
 * Created by lasse on 19-04-2016.
 **/
public class MusicPlayer implements ConnectionStateCallback, PlayerNotificationCallback {

    public Player mPlayer;
    boolean isPlaying = false; //Used to check if the player already is playing a track.

    public void play(){

        try{
            if(isPlaying) {
                mPlayer.resume();
            }
            else { //If the queue is empty the recommender is called to get a track.
                if (MainActivity.mQueueFragment.mQueueElementList.isEmpty() ){
                    MainActivity.mRecommend.recommendedTracks.add("61DLPczTvnkCwvwNqwPKgv");
                    List<String> recommendList = MainActivity.mRecommend.recommendedTracks;
                    Random random = new Random();
                    String track = recommendList.get(random.nextInt(recommendList.size()));
                    mPlayer.play("spotify:track:" + track);
                }
                else { //If queue is not empty, play the top song on the list.
                    mPlayer.play("spotify:track:" + MainActivity.mQueueFragment.nextSong());
                }

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
                isPlaying = false; //Set player to stopped.
                play(); //Play next track.
                MainActivity.mQueueFragment.trackWeightIncrease(); //Apply weight to elements in the queue.
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
