package com.example.aaup8v2.aaup8v2.recommender_pearson;

/**
 * Created by lasse on 11-04-2016.
 */
public class RecommenderArtist {
    public String name;
    public Double weight;
    public String genre;

    public RecommenderArtist(String name, Double weight) {
        this.weight = weight;
        this.name = name;
    }
}
