package com.example.aaup8v2.aaup8v2;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by MSI on 14-03-2016.
 */
public class SpotifyAccessTest {
    private SpotifyAccess mService;

    @Before
    public void setUp() throws Exception {
        mService = new SpotifyAccess();
    }

    @After
    public void tearDown() throws Exception {
        mService = null;
    }

    @Test
    public void testGetTrack() throws Exception {
        Assert.assertEquals("Timber", mService.getTrack("1zHlj4dQ8ZAtrayhuDDmkY").album.name);
    }

    @Test
    public void testGetPlaylist() throws Exception {

    }
}