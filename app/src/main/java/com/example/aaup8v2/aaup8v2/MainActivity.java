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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aaup8v2.aaup8v2.fragments.AdminFragment;
import com.example.aaup8v2.aaup8v2.fragments.DisconnectFragment;
import com.example.aaup8v2.aaup8v2.fragments.HomeFragment;
import com.example.aaup8v2.aaup8v2.fragments.PlayListFragment;
import com.example.aaup8v2.aaup8v2.fragments.QueueFragment;
import com.example.aaup8v2.aaup8v2.fragments.SearchFragment;
import com.example.aaup8v2.aaup8v2.fragments.SettingsFragment;
import com.example.aaup8v2.aaup8v2.recommender_pearson.PearsonRecommend;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class MainActivity extends AppCompatActivity
        implements /*NavigationView.OnNavigationItemSelectedListener,*/AdminFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, PlayListFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener,
        QueueFragment.OnFragmentInteractionListener, DisconnectFragment.OnFragmentInteractionListener, SearchFragment.OnFragmentInteractionListener {

    DrawerLayout drawer;
    Toolbar toolbar;

    // Replace with your client ID
    private static final String CLIENT_ID = "8d04022ead4444d0b005d171e5941922";
    // Replace with your redirect URI
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    public static SpotifyAccess mSpotifyAccess;
    public PearsonRecommend mRecommend;
    public MusicPlayer musicPlayer;
    public static UserPrivate me;

    public static SearchFragment mSearchFragment;
    public static QueueFragment mQueueFragment;
    public static WifiDirectActivity mWifiDirectActivity;
    public static PlayListFragment mPlaylistFragment;
    public static DisconnectFragment mDisconnectFragment;
    public static ImageView playButton;
    public static TextView playedName;
    public static TextView playedArtist;
    public static Button hostButton;
    public static Button connectButton;
    public static Button disconnectButton;
    public static MenuItem disconnectItem;

    public static boolean isHost = true;
    public static boolean isPeer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                selectDrawerItem(menuItem);
                return true;
            }

        });

         authenticate(); //Authenticates Spotify

        mSpotifyAccess = new SpotifyAccess(); //Sets the SpotifyAccess class
        mRecommend = new PearsonRecommend(this, this); //Sets the PearsonRecommend class

        mWifiDirectActivity = new WifiDirectActivity();

        //Instantiate the fragments
        mSearchFragment = new SearchFragment();
        mQueueFragment = new QueueFragment();
        musicPlayer = new MusicPlayer();
        mPlaylistFragment = new PlayListFragment();
        mDisconnectFragment = new DisconnectFragment();

        // Instantiate the playbar
        playedName = (TextView)findViewById(R.id.track_name);
        playedArtist = (TextView)findViewById(R.id.artist_name);
        playButton = (ImageView)findViewById(R.id.playButtonImage);

        //Create onClickListener for playButton
        playButton.setOnClickListener(new View.OnClickListener() {
            int buttonState = 0;
            public void onClick(View v) {
                if (buttonState == 0 && (!mQueueFragment.mQueueElementList.isEmpty() || musicPlayer.isPlaying)) {
                    musicPlayer.play();
                    playButton.setImageResource(R.drawable.ic_action_playback_pause);
                    buttonState = 1;
                } else if (buttonState == 1) {
                    musicPlayer.pause();
                    playButton.setImageResource(R.drawable.ic_action_playback_play);
                    buttonState = 0;
                }
            }
        });
    }

    public void isPremium(){
        Thread worker = new Thread(new Runnable() {
            private void changePlaybutton(final UserPrivate up) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        me = up; //Saves the user for later use
                        if(up.product.equals("premium")){ initializePeer(true); }
                        else { initializePeer(false); }
                    }
                });
            }
            @Override
            public void run() { changePlaybutton(mSpotifyAccess.mService.getMe()); }
        });
        worker.start();
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the planet to show based on
        // position
        Fragment fragment = null;

        Class fragmentClass;
        switch(menuItem.getItemId()) {
            case R.id.nav_queue:
                fragmentClass = QueueFragment.class;
                break;
            // case R.id.nav_home:
             //   fragmentClass = HomeFragment.class;
             //   break;
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
                //Instantiate the disconnect MenuItem
                disconnectItem = menuItem;
                break;
            case R.id.nav_search:
                fragmentClass = SearchFragment.class;
                break;
            default:
                fragmentClass = HomeFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();

            //Special code for SearchFragment
            if(fragmentClass == SearchFragment.class){
                mSearchFragment = (SearchFragment) fragment;
            }
            else if(fragmentClass == QueueFragment.class){

                ((QueueFragment)fragment).mQueueElementList = mQueueFragment.mQueueElementList;
                mQueueFragment = (QueueFragment) fragment;
            }
            else if(fragmentClass == PlayListFragment.class){
                ((PlayListFragment)fragment).listDataChild = mPlaylistFragment.listDataChild;
                ((PlayListFragment)fragment).playlistName = mPlaylistFragment.playlistName;
                mPlaylistFragment = (PlayListFragment) fragment;
            } else if(fragmentClass == DisconnectFragment.class){
                mDisconnectFragment = (DisconnectFragment) fragment;
            }
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
        //builder.setScopes(new String[]{"user-read-private", "streaming"}); //Old version
        builder.setScopes(new String[]{"user-library-read", "streaming"}); //New version
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

    //Might be possible to move some of this to the MusicPlayer class.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                mSpotifyAccess.setAccessToken(response.getAccessToken());
                isPremium();

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                musicPlayer.mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        musicPlayer.mPlayer.addConnectionStateCallback(musicPlayer);
                        musicPlayer.mPlayer.addPlayerNotificationCallback(musicPlayer);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);

        //Disconnect from the network, when the app is closed
        mWifiDirectActivity.disconnect();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void Test_spotify(View view){
        /** ID's
         * Empty list: user:aaup8 :playlist:6B3WEOcvqjEsURp4Icu9vN
         * Our test list: user:aaup8: playlist:1RdQS80EE32zxXBFOfLnNR
         *https://play.spotify.com/user/117012207/playlist/4cFMwqkMGdO2OBAjxGZDyl
         */
        mRecommend.recommend("lasse.d.c91");
    }

    public void pToP(View view){
        if(findViewById(R.id.btn_connectView) == view){
            isPeer = true;
            isHost = false;
        }
        Intent intent = new Intent(this, mWifiDirectActivity.getClass());
        startActivity(intent);
    }

    public static void toggleConnectionButtons(boolean show){
        if(show){
            hostButton.setVisibility(Button.VISIBLE);
            connectButton.setVisibility(Button.VISIBLE);
            disconnectButton.setVisibility(Button.GONE);
            disconnectItem.setIcon(R.drawable.ic_menu_share);
            disconnectItem.setTitle(R.string.Connections);
        }else{
            hostButton.setVisibility(Button.GONE);
            connectButton.setVisibility(Button.GONE);
            disconnectButton.setVisibility(Button.VISIBLE);
            disconnectItem.setIcon(R.drawable.ic_cancel);
            disconnectItem.setTitle(R.string.Disconnect);
        }
    }

    public static void initializePeer(boolean show){
        if(show)
            playButton.setVisibility(ImageView.VISIBLE);
        else
            playButton.setVisibility(ImageView.GONE);
    }

}
