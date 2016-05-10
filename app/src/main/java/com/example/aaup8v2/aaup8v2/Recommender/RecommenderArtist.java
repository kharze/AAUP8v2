package com.example.aaup8v2.aaup8v2.Recommender;

import java.util.List;

/**
 * Created by lasse on 11-04-2016.
 */
public class RecommenderArtist {
    public String name;
    public Double weight;
    public int count;
    public List<String> genre;
    public List<String> tracks;

    public RecommenderArtist(String name, Double weight, int count, List<String> genre, List<String> tracks) {
        this.weight = weight;
        this.count = count;
        this.name = name;
        this.genre = genre;
        this.tracks = tracks;
    }

    public void setCount (int newCount) { count = newCount; }
    public void setWeight (Double newWeight){
        weight = newWeight;
    }
    public void setTracks (List<String> newTracks){ tracks = newTracks; }
}
