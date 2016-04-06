package com.example.aaup8v2.aaup8v2;

import android.test.InstrumentationTestCase;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetTrack;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by MSI on 14-03-2016.
 */
public class SpotifyAccessTest extends InstrumentationTestCase {
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
    public final void testGetTrack() throws Exception {
        String b;
        //Assert.assertEquals("Timber", mService.getTrack("1zHlj4dQ8ZAtrayhuDDmkY").album.name);
        //Assert.assertEquals("Timber", mService.new asyncGetTrack().execute("1zHlj4dQ8ZAtrayhuDDmkY").get().album.name);
        try {
            //Track a = new asyncGetTrack().execute("1zHlj4dQ8ZAtrayhuDDmkY").get();
            //b = a.name;
        }
        catch (Exception e){
            e.getCause();
            Assert.fail();
        }
    }

    @Test
    public void testGetPlaylist() throws Exception {

    }
}