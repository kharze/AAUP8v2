package com.example.aaup8v2.aaup8v2.asyncTasks;

/**
 * Created by Claus on 4/22/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A simple server socket that accepts connection and writes some data on
 * the stream.
 */
public class asyncDataTransfer extends AsyncTask<Void, Void, String> {
    private Context context;
    /**
     * @param context
     */
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
            //final File f = new File(Environment.getExternalStorageDirectory() + "/"
            // + context.getPackageName() + "/wifip2pshared-" + System.currentTimeMillis()
            // + ".jpg");
            //File dirs = new File(f.getParent());
            //if (!dirs.exists())
                //dirs.mkdirs();
            //f.createNewFile();
            //Log.d(WifiDirectActivity.TAG, "server: copying files " + f.toString());
            ObjectInputStream objectInputStream = new ObjectInputStream(client.getInputStream());
            Object object = objectInputStream.readObject();
            if (object.getClass().equals(String.class) && ((String) object).equals("BROFIST")) {
                Log.d(WifiDirectActivity.TAG, "Connected");
            }

            InputStream inputstream = client.getInputStream();
            //copyFile(inputstream, new FileOutputStream(f));
            serverSocket.close();
            return null;//f.getAbsolutePath();
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
    protected void onPostExecute(String result) {
        if (result != null) {

        }
    }
}
