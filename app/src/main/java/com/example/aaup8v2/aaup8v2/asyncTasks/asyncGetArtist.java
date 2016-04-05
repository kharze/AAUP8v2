package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;

public class asyncGetArtist extends AsyncTask<String, Void, Artist> {

    public interface AsyncResponse {
        void processFinish(Artist output);
    }

    public AsyncResponse delegate = null;

    public asyncGetArtist(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Artist doInBackground(String... id) {
        try {
            Artist mArtist = MainActivity.mSpotifyAccess.mService.getArtist(id[0]);
            return mArtist;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Artist p){
        try {
            MainActivity.mTextView.setText(p.describeContents());
            delegate.processFinish(p);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
