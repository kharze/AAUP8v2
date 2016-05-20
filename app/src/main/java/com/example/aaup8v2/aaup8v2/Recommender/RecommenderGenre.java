package com.example.aaup8v2.aaup8v2.Recommender;

/**
 * Created by lasse on 07-04-2016.
 */
public class RecommenderGenre {
    public Integer id;
    public String genre;
    public int count;

    public RecommenderGenre(Integer id, String genre, int count){
        this.id = id;
        this.genre = genre;
        this.count = count;
    }

    public int getCount(){
        return count;
    }

    public String getGenre(){
        return genre;
    }


}
