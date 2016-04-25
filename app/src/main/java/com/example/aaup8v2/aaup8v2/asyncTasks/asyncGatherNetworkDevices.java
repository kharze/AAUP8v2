package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import kaaes.spotify.webapi.android.models.Artist;

public class asyncGatherNetworkDevices extends AsyncTask<String, Void, String> {

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public asyncGatherNetworkDevices(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    @Override
    protected String doInBackground(String... id) {
        String ip = "";
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(WifiDirectActivity.TAG, "Server: connection done");
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            Object object = objectInputStream.readObject();
            if (object.getClass().equals(String.class)) {
                ip = (String) object;
                Log.d(WifiDirectActivity.TAG, "Got ip address");
            }
            serverSocket.close();
            return ip;
        }
        catch (Exception e)
        {
            return null;
        }

    }

    @Override
    protected void onPostExecute(String deviceIP){
        try {
            delegate.processFinish(deviceIP);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

}
