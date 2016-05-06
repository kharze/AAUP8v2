package com.example.aaup8v2.aaup8v2.Recommender;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Flaka_000 on 05-05-2016.
 */
public class Recommender extends MainActivity {
    private HashMap<String, Double> mWeights;
    private Integer count = 0;

    public Recommender(HashMap<String, Double> weights){
        mWeights.putAll(weights);
    }

    public void adjWeights(HashMap<String, Double> inWeights){
        count++;
        //Make an iterator to process the incoming weights
        Iterator<HashMap.Entry<String, Double>> inIter = inWeights.entrySet().iterator();
        while(inIter.hasNext()){
            HashMap.Entry<String, Double> inPair = inIter.next();
            boolean isNew = false;
            Iterator<HashMap.Entry<String, Double>> localIter = mWeights.entrySet().iterator();

            while(localIter.hasNext()){
                HashMap.Entry<String, Double> localPair = localIter.next();

                if(inPair.getKey().equals(localPair.getKey())){

                } else {    //New genre/artist, add to weights
                    isNew = true;
                }
            }

            if(isNew){  //Add it to hashmap of weights
                mWeights.put(inPair.getKey(), inPair.getValue()/count);
            }
        }
    }
}
