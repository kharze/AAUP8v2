package com.example.aaup8v2.aaup8v2.asyncTasks;

/**
 * Created by Claus on 4/22/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class asyncDataTransfer extends AsyncTask<Void, Void, String> {
    private Context context;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AsyncResponse delegate = null;

    public asyncDataTransfer(AsyncResponse delegate){
        this.delegate = delegate;
    }

    public asyncDataTransfer(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(8988);
            Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
            Socket client = serverSocket.accept();
            Log.d(WifiDirectActivity.TAG, "Server: connection done");

            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            Object object = objectInputStream.readObject();
            if (object.getClass().equals(String.class)) {
                Log.d(WifiDirectActivity.TAG, "Data received");
            }

            objectInputStream.close();
            serverSocket.close();
            return (String) object;
        } catch (IOException e) {
            Log.e(WifiDirectActivity.TAG, e.getMessage());
            return null;
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(String data) {
        try {
            delegate.processFinish(data);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }
}
