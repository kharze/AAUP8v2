package com.example.aaup8v2.aaup8v2.Recommender;

import com.example.aaup8v2.aaup8v2.MainActivity;

import java.util.ArrayList;

/**
 * Created by Flaka_000 on 05-05-2016.
 */
public class Recommender extends MainActivity {
    private Double[][] mGenreWeights;
    private Double[][] mArtistWeights;
    private ArrayList<String> mGenreOrder;
    private ArrayList<String> mArtistOrder;

    public Recommender(String uid){
        init(uid);
    }

    //Initializes recommender
    //Takes user id - Currently not implemented
    private void init(String uid){

    }

    //Updates the matrix with a new user-row
    //Works on both genres and artists. Takes as param. Returns the updated matrix.
    //Bool for type of weights. True for mGenre. False for mArtist.
    public Double[][] addWeights(Double[][] lArr, ArrayList<String> inOrder, Double[] inArr, boolean isGenre){
        ArrayList<String> order;
        //Get the order in the arraylist.
        if (isGenre){
            order = mGenreOrder;
        } else{
            order = mArtistOrder;
        }

        //Add new occurrences to the order
        for (String str: inOrder) {
            if (!order.contains(str)){
                order.add(str);
            }
        }

        //Replace the old array - one dimension the size of new number of genres/artists
        Double[][] local = new Double[order.size()][];
        for (int i = 0; i < order.size(); i++){
            //Fill in each row with length of former array plus one (new peer)
            //Fill in old information
            local[i] = new Double[lArr[0].length + 1];
            for (int j = 0; j < lArr[0].length - 1; j++){
                local[i][j] = lArr[i][j];
            }
            //Last row is the new peer
            local[i][local[0].length - 1] = inArr[inOrder.indexOf(order.get(i))];
        }

        return local;
    }

    //Not yet implemented
    private void personsim(){
    }

    //Not yet implemented
    public void predict(){

    }
}
