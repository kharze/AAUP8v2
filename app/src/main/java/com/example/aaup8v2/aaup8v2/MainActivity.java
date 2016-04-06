package com.example.aaup8v2.aaup8v2;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylist;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetTrack;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncSearchArtists;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncSearchTracks;
import com.example.aaup8v2.aaup8v2.fragments.AdminFragment;
import com.example.aaup8v2.aaup8v2.fragments.DisconnectFragment;
import com.example.aaup8v2.aaup8v2.fragments.HomeFragment;
import com.example.aaup8v2.aaup8v2.fragments.PlayListFragment;
import com.example.aaup8v2.aaup8v2.fragments.QueueFragment;
import com.example.aaup8v2.aaup8v2.fragments.SearchFragment;
import com.example.aaup8v2.aaup8v2.fragments.SettingsFragment;
import com.example.aaup8v2.aaup8v2.recommender_pearson.PearsonRecommend;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.lang.reflect.Array;
import java.util.List;

import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;

public class MainActivity extends AppCompatActivity
        implements /*NavigationView.OnNavigationItemSelectedListener,*/AdminFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, PlayListFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        QueueFragment.OnFragmentInteractionListener, DisconnectFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener, ConnectionStateCallback, PlayerNotificationCallback {

    TextView textView;
    DrawerLayout drawer;
    Toolbar toolbar;
    public static TextView mTextView;

    // Replace with your client ID
    private static final String CLIENT_ID = "8d04022ead4444d0b005d171e5941922";
    // Replace with your redirect URI
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private Player mPlayer;
    private static final int REQUEST_CODE = 1337;
    public static SpotifyAccess mSpotifyAccess;
    public PearsonRecommend mRecommend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        /**FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });**/


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }

        });

        //Authenticates Spotify
        authenticate();
        //Sets the spotify web Api access class
        mSpotifyAccess = new SpotifyAccess();

        //Temporary TextView used to show playlist and Track.
        mTextView = (TextView)findViewById(R.id.Name_for_song);
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass = null;
        switch(menuItem.getItemId()) {
            case R.id.nav_queue:
                fragmentClass = QueueFragment.class;
                break;
            case R.id.nav_home:
                fragmentClass = HomeFragment.class;
                break;
            case R.id.nav_playlist:
                fragmentClass = PlayListFragment.class;
                break;
            case R.id.nav_settings:
                fragmentClass = SettingsFragment.class;
                break;
            case R.id.nav_admin:
                fragmentClass = AdminFragment.class;
                break;
            case R.id.nav_disconnect:
                fragmentClass = DisconnectFragment.class;
                break;
            case R.id.nav_search:
                fragmentClass = SearchFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawers();
    }


    public void authenticate(){
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, //authentication
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        selectDrawerItem(item);

        return super.onOptionsItemSelected(item);
    }

    /*@SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
               mSpotifyAccess.setAccessToken2(response.getAccessToken());

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        //mPlayer.play("spotify:track:2SUpC3UgKwLVOS2FtZif9N");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    //Spotify functions
    @Override
    public void onLoggedIn() { Log.d("MainActivity", "User logged in"); }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed" + error);
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void Test_spotify(View view){
        //mSpotifyAccess.getPlaylist("jmperezperez", "3cEYpjA9oz9GiPac4AsH4n");
        try {
            Track b = new asyncGetTrack(new asyncGetTrack.AsyncResponse(){
                @Override
                public void processFinish(Track output){
                }
            }).execute("1zHlj4dQ8ZAtrayhuDDmkY").get();
            Pager c = new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
                @Override
                public void processFinish(Pager output){
                }
            }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV").get();
            //Track t = ((Track) c.items.get(1));
            Playlist z = new asyncGetPlaylist(new asyncGetPlaylist.AsyncResponse(){
                @Override
                public void processFinish(Playlist output){
                }
            }).execute("spotify_denmark", "2qPIOBAKYc1SQI1QHDV4EV").get();
            String a = b.album.name;
        }
        catch (Exception e){

        }
    }
    public void playMusic(View view){
        mPlayer.play("spotify:track:2SUpC3UgKwLVOS2FtZif9N");
    }
    public void pauseMusic(View view){
        mPlayer.pause();
    }
    public void skipMusic(View view){
        mPlayer.skipToNext();
    }
    public void prevMusic(View view){
        mPlayer.skipToPrevious();
    }

    public void resumeMusic(View view) {
        mPlayer.resume();
    }


    private String searchString = "";

    EditText mText;


    Array searchResult;
    List temp = null;
    List temp2 = null;
    int i = 0;

    public void searchMusic(View view){
        mText = (EditText) findViewById(R.id.Search_Text);
        searchString = mText.getText().toString();

        new asyncSearchTracks(new asyncSearchTracks.AsyncResponse(){
            @Override
            public void processFinish(TracksPager output){

                temp = output.tracks.items;

                i++;
            }
        }).execute(searchString);

        new asyncSearchArtists(new asyncSearchArtists.AsyncResponse(){
            @Override
            public void processFinish(ArtistsPager output){

                temp2 = output.artists.items;
                // Artists -> Albums -> simpleTracks

            }
        }).execute(searchString);
    }
}
