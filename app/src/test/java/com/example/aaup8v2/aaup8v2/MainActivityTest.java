package com.example.aaup8v2.aaup8v2;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by MSI on 11-03-2016.
 */
public class MainActivityTest {

    private MainActivity mMainActivity;
    private SpotifyAccess mSpotifyAccess = new SpotifyAccess();

    @Before
    public void setUp() throws Exception {
        mMainActivity = new MainActivity();
    }

    @After
    public void tearDown() throws Exception {
        mMainActivity = null;
    }

    @Test
    public void testOnCreate() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testOnBackPressed() throws Exception {

    }

    @Test
    public void testOnCreateOptionsMenu() throws Exception {

    }

    @Test
    public void testOnOptionsItemSelected() throws Exception {

    }

    @Test
    public void testOnNavigationItemSelected() throws Exception {

    }

    @Test
    public void testOnActivityResult() throws Exception {

    }

    @Test
    public void testOnLoggedIn() throws Exception {

    }

    @Test
    public void testOnLoggedOut() throws Exception {

    }

    @Test
    public void testOnLoginFailed() throws Exception {

    }

    @Test
    public void testOnTemporaryError() throws Exception {

    }

    @Test
    public void testOnConnectionMessage() throws Exception {

    }

    @Test
    public void testOnPlaybackEvent() throws Exception {

    }

    @Test
    public void testOnPlaybackError() throws Exception {

    }

    @Test
    public void testOnDestroy() throws Exception {

    }

    @Test
    public void testauthenticate() throws Exception {
        //assertNotEquals("Spotify Web API Testing playlist", mSpotifyAccess.getPlaylist("jmperezperez", "3cEYpjA9oz9GiPac4AsH4n").name);

       //mMainActivity.authenticate();

        //assertEquals("Spotify Web API Testing playlist", mSpotifyAccess.getPlaylist("jmperezperez", "3cEYpjA9oz9GiPac4AsH4n").name);


    }
}