package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Lasse on 21-03-2016.
 **/

public class PearsonRecommend{

    List<String> difGenres = new ArrayList<>();

    public List<String> generateGenreList(String u_id, String p_id){

        Pager tracksPager= null;
        Track track;
        Artists mArtists = null;
        List<String> artistsList = new ArrayList<>();
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
            for(int i=0; i < track.artists.size(); i++){
                artistsList.add(track.artists.get(i).id);
            }
        }

        List<String> genres = new ArrayList<>();
        try{
            do{
                String artistsRequests = null;
                int counter = 0;
                do{
                    if(artistsRequests == null){
                        artistsRequests = artistsList.get(0);
                        artistsList.remove(0);
                        counter++;
                    }
                    else {
                        artistsRequests += "," + artistsList.get(0);
                        artistsList.remove(0);
                        counter++;
                    }
                }while (counter < 50 && !artistsList.isEmpty());
                try
                {
                    mArtists = new asyncGetArtists(new asyncGetArtists.AsyncResponse(){
                        @Override
                        public void processFinish(Artists output){
                        }
                    }).execute(artistsRequests).get();
                }catch (Exception e){
                    e.getMessage();
                }

                for (int i = 0; i < mArtists.artists.size(); i++){
                    int temp = mArtists.artists.get(i).genres.size();
                    for(int j = 0; j < temp; j++){
                        String[] tempGenres = mArtists.artists.get(i).genres.get(j).split("\\W+");
                        for (String genre : tempGenres) {
                            genres.add(genre);
                        }
                    }
                }
            }while (!artistsList.isEmpty());

            return genres;

        }catch (Exception e){
            return new ArrayList<>();
        }


    }

    public List<List> getGenreCount(String u_id, String p_id){
        List<String> genres = generateGenreList(u_id, p_id);

        Collections.sort(genres);

        List<Integer> occGenre = new  ArrayList<>();

        int occurence = 0;
        for (int i = 0; i < genres.size(); i++){
            if (!difGenres.contains(genres.get(i))){
                difGenres.add(genres.get(i));
                if(occurence != 0){
                    occGenre.add(occurence);
                    occurence = 0;
                }
                occurence++;
                if(i == genres.size()- 1){
                    occGenre.add(occurence);
                }
            }
            else{
                occurence++;
                if(i == genres.size()- 1){
                    occGenre.add(occurence);
                }
            }
        }
        List<List> genresValue = new ArrayList<>();
        genresValue.add(difGenres);
        genresValue.add(occGenre);

        return genresValue;
    }

    public List<List> calculateWeights(String u_id, String p_id){

        List<List> genres = getGenreCount(u_id, p_id);
        List<Integer> occGenres = genres.get(1);
        List<List> genreWeights = new ArrayList<>();
/**
        List<Integer> occGenres = new ArrayList<>();
        occGenres.add(5);
        occGenres.add(3);
        occGenres.add(4);
        occGenres.add(4);
**/
        Double avgGenre = 0.0;
        Double summation = 0.0;

        for(int i = 0; i < occGenres.size(); i++)
        {
            avgGenre += occGenres.get(i);
        }

        avgGenre = avgGenre / occGenres.size();

        for(int i = 0; i < occGenres.size(); i++)
        {
            summation += Math.pow(occGenres.get(i) - avgGenre, 2);
        }

        for(int i = 0; i < occGenres.size(); i++)
        {
            List<Double> weightsAndID = new ArrayList<>();
            Double pearson;
            pearson = (occGenres.get(i)-avgGenre)/Math.sqrt(summation);
            weightsAndID.add(pearson);
            weightsAndID.add((double) i);
            genreWeights.add(weightsAndID);
        }

        return genreWeights;
    }

    public List<Pager> recommender(String u_id, String p_id){

        List<List> weights = calculateWeights(u_id, p_id);

        Collections.sort();

        return null;
    }

}

