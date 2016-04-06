package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;

import com.example.aaup8v2.aaup8v2.MainActivity;

import kaaes.spotify.webapi.android.models.Album;
import kaaes.spotify.webapi.android.models.Artists;

public class asyncGetAlbum extends AsyncTask<String, Void, Album> {

    public interface AsyncResponse {
        void processFinish(Album output);
    }

    public AsyncResponse delegate = null;

    public asyncGetAlbum(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected Album doInBackground(String... id) {
        try {
            Album mAlbum = MainActivity.mSpotifyAccess.mService.getAlbum(id[0]);
            return mAlbum;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(Album p){
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