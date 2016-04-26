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
import java.util.ArrayList;
import java.util.List;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class asyncDataTransfer extends AsyncTask<Void, List<String>, Void> {

    public interface AsyncResponse {
        void processFinish(List<String> output);
    }

    public AsyncResponse delegate = null;

    public asyncDataTransfer(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onProgressUpdate(List<String>... values) {
        List<String> list = values[0];
        try {
            delegate.processFinish(list);
        }
        catch (Exception e)
        {
            e.getCause();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(8988);
                Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WifiDirectActivity.TAG, "Server: connection done");

                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Object type = objectInputStream.readObject();
                Object object = objectInputStream.readObject();
                if (object.getClass().equals(String.class)) {
                    Log.d(WifiDirectActivity.TAG, "Data received");
                }
                List<String> data = new ArrayList<>();
                data.add((String) type);
                data.add((String) object);
                publishProgress(data);

                objectInputStream.close();
                serverSocket.close();
                //return data;
            } catch (IOException e) {
                Log.e(WifiDirectActivity.TAG, e.getMessage());
                //return null;
            }catch (ClassNotFoundException e) {
                e.printStackTrace();
                //return null;
            }
        }
    }
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Void data) {

    }
}
