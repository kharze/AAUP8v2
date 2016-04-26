package com.example.aaup8v2.aaup8v2.asyncTasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;

public class asyncHostTransfer extends AsyncTask<Void, List<String>, Void> {

    public interface AsyncResponse {
        void processFinish(List<String> output);
    }

    public AsyncResponse delegate = null;

    public asyncHostTransfer(AsyncResponse delegate){
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
    protected Void doInBackground(Void... id) {
        while (true) {
            try {
                ServerSocket serverSocket = new ServerSocket(8888);
                Log.d(WifiDirectActivity.TAG, "Server: Socket opened");
                Socket client = serverSocket.accept();
                Log.d(WifiDirectActivity.TAG, "Server: connection done");
                ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
                Object type = objectInputStream.readObject();
                Object object = objectInputStream.readObject();

                List<String> data = new ArrayList<>();
                data.add((String) type);
                data.add((String) object);
                publishProgress(data);

                serverSocket.close();
                //return data;
            }
            catch (Exception e)
            {
                //return null;
            }
        }

    }

    @Override
    protected void onPostExecute(Void deviceIP){

    }

}
