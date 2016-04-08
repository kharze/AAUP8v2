package com.example.aaup8v2.aaup8v2.recommender_pearson;

/**
 * Created by lasse on 07-04-2016.
 */
public class Genre{
    public Integer id;
    public String genre;
    public Double weight;

    public Genre(int id, String genre, Double weight){
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
