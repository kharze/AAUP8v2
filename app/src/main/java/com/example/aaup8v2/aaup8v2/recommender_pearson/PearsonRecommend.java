package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtistTopTrack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Lasse on 21-03-2016.
 **/

public class PearsonRecommend{

    List<Track> trackList = new ArrayList<>();

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

    public List<RecommenderArtist> getArtistList(List<Artist> artistsList){
        List<String> difArtists = new ArrayList<>();
        List<Integer> occArtist = new ArrayList<>();

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
            artistObjects.add(new RecommenderArtist(difArtists.get(i), weights.get(i)));
        }

        Collections.sort(artistObjects, new Comparator<RecommenderArtist>() {
            @Override
            public int compare(RecommenderArtist lhs, RecommenderArtist rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });

        return artistObjects;
    }

    public  List<Genre> getGenreList(List<Artist> artistsList){
        List<String> genresList = new ArrayList<>();
        for(int i = 0; i < artistsList.size(); i++){
            int temp = artistsList.get(i).genres.size();
            for(int j = 0; j < temp; j++){
                genresList.add(artistsList.get(i).genres.get(j));
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
        List<Genre> genreObjects = new ArrayList<>();
        for (int i = 0; i < occGenre.size(); i++){
            genreObjects.add(new Genre(i, difGenres.get(i), weights.get(i)));
        }

        Collections.sort(genreObjects, new Comparator<Genre>() {
            @Override
            public int compare(Genre lhs, Genre rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });
        return genreObjects;
    }

    public List<Double> calculateWeights(List<Integer> occurrence){

        List<Double> weights = new ArrayList<>();
/**
        List<Integer> occGenres = new ArrayList<>();
        occGenres.add(5);
        occGenres.add(3);
        occGenres.add(4);
        occGenres.add(4);
**/
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

    public List<Pager> recommend(String u_id, String p_id){
        List<Artist> artistsList = getArtists(u_id, p_id);
        List<RecommenderArtist> artistList = getArtistList(artistsList);
        List<Genre> genreList = getGenreList(artistsList);

        Collections.reverse(genreList);
        Collections.reverse(artistList);
        Tracks tracks;
        trackList.size();
        try{
            tracks = new asyncGetArtistTopTrack(new asyncGetArtistTopTrack.AsyncResponse(){
                @Override
                public void processFinish(Tracks output){
                }
            }).execute("6FBDaR13swtiWwGhX1WQsP").get();

        }catch (Exception e){
        }

        return null;
    }

}