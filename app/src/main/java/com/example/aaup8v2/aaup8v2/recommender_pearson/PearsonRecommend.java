package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Lasse on 21-03-2016.
 **/

public class PearsonRecommend{

    List<Track> trackList = new ArrayList<>();
    /*
    Getting a list of artist based on tracks from a playlist
     */
    private List<Artist> getArtists(String u_id, String p_id){

        Pager tracksPager= null;
        Track track;
        Artists mArtists = null;
        List<String> artistsIdList = new ArrayList<>();
        List<Artist> artistsList = new ArrayList<>();
        try{
            tracksPager = new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
                @Override
                public void processFinish(Pager output){
                }
            }).execute(u_id, p_id).get();
        }catch (Exception e){
            e.getMessage();
        }
        List<PlaylistTrack> tracksList = tracksPager.items;

        for(int j=0; j < tracksList.size(); j++){
            track = tracksList.get(j).track;
            trackList.add(track);
            for(int i=0; i < track.artists.size(); i++){
                artistsIdList.add(track.artists.get(i).id);
            }
        }
        try {
            do {
                String artistsRequests = null;
                int counter = 0;
                do {
                    if (artistsRequests == null) {
                        artistsRequests = artistsIdList.get(0);
                        artistsIdList.remove(0);
                        counter++;
                    } else {
                        artistsRequests += "," + artistsIdList.get(0);
                        artistsIdList.remove(0);
                        counter++;
                    }
                } while (counter < 50 && !artistsIdList.isEmpty());
                try {
                    mArtists = new asyncGetArtists(new asyncGetArtists.AsyncResponse() {
                        @Override
                        public void processFinish(Artists output) {
                        }
                    }).execute(artistsRequests).get();
                } catch (Exception e) {
                    e.getMessage();
                }
                for (int i = 0; i < mArtists.artists.size(); i++) {
                    artistsList.add(mArtists.artists.get(i));
                }
            } while (!artistsIdList.isEmpty());

            return artistsList;
        }catch (Exception e) {
            return null;
        }
    }
    /*
    Converting artist list to a list of new artist objects contaning artist and weight of the artist
     */
    public List<RecommenderArtist> getArtistList(List<Artist> artistsList){
        List<String> difArtists = new ArrayList<>();
        List<Integer> occArtist = new ArrayList<>();
        List<List<String>> genresList = new ArrayList<>();

        Collections.sort(artistsList, new Comparator<Artist>() {
            @Override
            public int compare(Artist lhs, Artist rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });

        int occurrence = 0;
        for (int i = 0; i < artistsList.size(); i++){
            if (!difArtists.contains(artistsList.get(i).name)){
                difArtists.add(artistsList.get(i).name);
                List<String> temp = new ArrayList<>();
                for(int j = 0; j < artistsList.get(i).genres.size(); j++){
                    String[] split = artistsList.get(i).genres.get(j).split("\\W+");
                    for (int k = 0; k < split.length; k++){
                        temp.add(split[k]);
                    }
                }
                genresList.add(temp);
                if(occurrence != 0){
                    occArtist.add(occurrence);
                    occurrence = 0;
                }
                occurrence++;
                if(i == artistsList.size()- 1){
                    occArtist.add(occurrence);
                }
            }
            else{
                occurrence++;
                if(i == artistsList.size()- 1){
                    occArtist.add(occurrence);
                }
            }
        }
        List<Double> weights = calculateWeights(occArtist);
        List<RecommenderArtist> artistObjects = new ArrayList<>();
        for (int i = 0; i < occArtist.size(); i++){
            artistObjects.add(new RecommenderArtist(difArtists.get(i), weights.get(i), genresList.get(i), null));
        }

        Collections.sort(artistObjects, new Comparator<RecommenderArtist>() {
            @Override
            public int compare(RecommenderArtist lhs, RecommenderArtist rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });

        return artistObjects;
    }
    /*
    Generating a list of new genre object which contains id, genre and weight
     */

    public  List<RecommenderGenre> getGenreList(List<Artist> artistsList){
        List<String> genresList = new ArrayList<>();
        for(int i = 0; i < artistsList.size(); i++){
            int temp = artistsList.get(i).genres.size();
            for(int j = 0; j < temp; j++){
                String[] split = artistsList.get(i).genres.get(j).split("\\W+");
                for (int k = 0; k < split.length; k++){
                    genresList.add(split[k]);
                }
            }
        }

        Collections.sort(genresList);

        List<String> difGenres = new ArrayList<>();
        List<Integer> occGenre = new  ArrayList<>();

        int occurrence = 0;
        for (int i = 0; i < genresList.size(); i++){
            if (!difGenres.contains(genresList.get(i))){
                difGenres.add(genresList.get(i));
                if(occurrence != 0){
                    occGenre.add(occurrence);
                    occurrence = 0;
                }
                occurrence++;
                if(i == genresList.size()- 1){
                    occGenre.add(occurrence);
                }
            }
            else{
                occurrence++;
                if(i == genresList.size()- 1){
                    occGenre.add(occurrence);
                }
            }
        }
        List<Double> weights = calculateWeights(occGenre);
        List<RecommenderGenre> genreObjects = new ArrayList<>();
        for (int i = 0; i < occGenre.size(); i++){
            genreObjects.add(new RecommenderGenre(i, difGenres.get(i), weights.get(i)));
        }

        Collections.sort(genreObjects, new Comparator<RecommenderGenre>() {
            @Override
            public int compare(RecommenderGenre lhs, RecommenderGenre rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });
        return genreObjects;
    }
    /*
    Pearson weight calculator
     */

    public List<Double> calculateWeights(List<Integer> occurrence){

        List<Double> weights = new ArrayList<>();

        Double avgGenre = 0.0;
        Double summation = 0.0;

        for(int i = 0; i < occurrence.size(); i++)
        {
            avgGenre += occurrence.get(i);
        }

        avgGenre = avgGenre / occurrence.size();

        for(int i = 0; i < occurrence.size(); i++)
        {
            summation += Math.pow(occurrence.get(i) - avgGenre, 2);
        }

        for(int i = 0; i < occurrence.size(); i++)
        {
            Double pearson;
            pearson = (occurrence.get(i)-avgGenre)/Math.sqrt(summation);
            weights.add(pearson);
        }
        return weights;
    }
    /*
    Should end up being the recommender
     */

    public List<RecommenderArtist> recommend(String u_id, String p_id) {
        List<Artist> artistsList = getArtists(u_id, p_id);
        List<RecommenderArtist> artistList = getArtistList(artistsList);
        List<RecommenderGenre> genreList = getGenreList(artistsList);
        List<RecommenderArtist> recommended = new ArrayList<>();

        for (int i = 0; i < artistList.size(); i++) {
            double artistWeight = artistList.get(i).weight;
            double genreWeight = 0;
            for (int j = 0; j < genreList.size(); j++) {

                double tempWeight = genreList.get(j).weight;
                if (artistList.get(i).genre.contains(genreList.get(j).genre) && tempWeight > genreWeight ) {
                    genreWeight = tempWeight;
                }
            }
            double newWeight = genreWeight + artistWeight;
            artistList.get(i).setWeight(newWeight);
        }
        /**
        for(int i = 0; i < artistList.size(); i++){
            double newWeight = 0;
            for(int j = 0; j < genreList.size(); j++){
                if (artistList.get(i).genre.contains(genreList.get(j).genre)) {
                    newWeight += genreList.get(j).weight;
                }
            }
            artistList.get(i).setWeight(newWeight);
        }
         **/

        Collections.sort(artistList, new Comparator<RecommenderArtist>() {
            @Override
            public int compare(RecommenderArtist lhs, RecommenderArtist rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });
        Collections.reverse(artistList);
    /**
        for (int i = 0; i < artistList.size(); i++) {
            List<String> artistTracks = new ArrayList<>();
            String artist = artistList.get(i).name;
            for (int j = 0; j < trackList.size(); j++) {
                for (ArtistSimple trackArtist: trackList.get(j).artists) {
                    String trackArtistName = trackArtist.name;
                    if(trackArtistName.equals(artist)){
                        String temp = trackList.get(j).name;
                        if (!artistTracks.contains(temp)){
                            artistTracks.add(temp);
                        }
                    }
                }
            }
            artistList.get(i).setTracks(artistTracks);
            recommended.add(artistList.get(i));
        }
     **/
        int x = 0;
        return recommended;
    }
}