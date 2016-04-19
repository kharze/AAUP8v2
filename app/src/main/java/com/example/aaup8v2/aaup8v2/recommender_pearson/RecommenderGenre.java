package com.example.aaup8v2.aaup8v2.recommender_pearson;

/**
 * Created by lasse on 07-04-2016.
 */
public class RecommenderGenre {
    public Integer id;
    public String genre;
    public Double weight;

    public RecommenderGenre(Integer id, String genre, Double weight){
        this.id = id;
        this.genre = genre;
        this.weight = weight;
    }

    public int getId(){
        return id;
    }

    public String getGenre(){
        return genre;
    }

    public Double getWeight(){
        return weight;
    }

}
