package com.example.aaup8v2.aaup8v2.Runnables;

/**
 * Created by MSI on 04-05-2016.
 */
public class ThreadResponseInterface<T> {

    public ThreadResponse delegate = null;

    public interface ThreadResponse<T> {
        void processFinish(T output);
    }

}
