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
/**
            mArtists = new asyncGetArtists(new asyncGetArtists().AsyncResponse(){
                @Override
                public void processFinish(Pager output){
                }
            }).execute(u_id, p_id).get();
 **/
        }catch (Exception e){
        }

        List<PlaylistTrack> tracksList = tracksPager.items;
        for(int j=0; j < tracksList.size(); j++){
            track = tracksList.get(j).track;
            for(int i=0; i < track.artists.size(); i++){
                artistsList.add(track.artists.get(i).id);
            }
        }

        List<String> genres = new ArrayList();
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

                }

                for (int i = 0; i < mArtists.artists.size(); i++){
                    int temp = mArtists.artists.get(i).genres.size();
                    for(int j = 0; j < temp; j++){
                        String[] tempGenres = mArtists.artists.get(i).genres.get(j).split("\\W+");
                        for(int y = 0; y < tempGenres.length; y++){
                            genres.add(tempGenres[y]);
                        }
                    }
                }
            }while (!artistsList.isEmpty());

            return genres;
        }catch (Exception e){
            return new ArrayList<>();
        }


    }

    public List<List> calculateWeights(String u_id, String p_id){
        List<String> genres = generateGenreList(u_id, p_id);

        Collections.sort(genres);

        List<String> difGenres = new ArrayList<>();
        List<Integer> occGenre = new  ArrayList<>();

        int occurence = 0;
        String tempGenre = null;
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

    public void pearsonRecommender(String u_id, String p_id){

        List<List> genres = calculateWeights(u_id, p_id);
        List<String> difGenres = genres.get(0);
        List<Integer> occGenres = genres.get(1);
        List<Double> genreWeights = new ArrayList<>();
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
        Double avgDif = 0.0;

        for(int i = 0; i < occGenres.size(); i++)
        {
            summation += Math.pow(occGenres.get(i) - avgGenre, 2);
        }

        for(int i = 0; i < occGenres.size(); i++)
        {
            Double pearson = 0.0;
            pearson = (occGenres.get(i)-avgGenre)/Math.sqrt(summation);
            genreWeights.add(pearson);
        }


        int o = 10;

    }
}

