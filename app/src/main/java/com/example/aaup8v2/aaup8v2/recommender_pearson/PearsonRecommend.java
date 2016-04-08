package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetTrack;
import com.example.aaup8v2.aaup8v2.recommender_pearson.Genre;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtistTopTrack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Lasse on 21-03-2016.
 **/

public class PearsonRecommend{

    List<ArtistSimple> artists = new ArrayList<>();
    List<Track> trackList = new ArrayList<>();
    List<String> difGenres = new ArrayList<>();

    public List<String> generateGenreList(String u_id, String p_id){

        Pager tracksPager= null;
        Track track;
        Artists mArtists = null;
        List<String> artistsIdList = new ArrayList<>();
        try{
            tracksPager = new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
                @Override
                public void processFinish(Pager output){
                }
            }).execute(u_id, p_id).get();
        }catch (Exception e){
            e.getMessage();
        }
        int temp1 = tracksPager.items.size();
        List<PlaylistTrack> tracksList = tracksPager.items;
        for(int j=0; j < tracksList.size(); j++){
            track = tracksList.get(j).track;
            trackList.add(track);
            for(int i=0; i < track.artists.size(); i++){
                artistsIdList.add(track.artists.get(i).id);
                artists.add(track.artists.get(i));
            }
        }

        List<String> genres = new ArrayList<>();
        try{
            do{
                String artistsRequests = null;
                int counter = 0;
                do{
                    if(artistsRequests == null){
                        artistsRequests = artistsIdList.get(0);
                        artistsIdList.remove(0);
                        counter++;
                    }
                    else {
                        artistsRequests += "," + artistsIdList.get(0);
                        artistsIdList.remove(0);
                        counter++;
                    }
                }while (counter < 50 && !artistsIdList.isEmpty());
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
            }while (!artistsIdList.isEmpty());

            return genres;

        }catch (Exception e){
            return new ArrayList<>();
        }


    }

    public List<Integer> getGenreCount(String u_id, String p_id){
        List<String> genres = generateGenreList(u_id, p_id);

        Collections.sort(genres);
        List<Integer> occGenre = new  ArrayList<>();

        int occurrence = 0;
        for (int i = 0; i < genres.size(); i++){
            if (!difGenres.contains(genres.get(i))){
                difGenres.add(genres.get(i));
                if(occurrence != 0){
                    occGenre.add(occurrence);
                    occurrence = 0;
                }
                occurrence++;
                if(i == genres.size()- 1){
                    occGenre.add(occurrence);
                }
            }
            else{
                occurrence++;
                if(i == genres.size()- 1){
                    occGenre.add(occurrence);
                }
            }
        }

        return occGenre;
    }

    public List<Genre> calculateWeights(String u_id, String p_id){

        List<Integer> occGenres = getGenreCount(u_id, p_id);
        List<Double> weights = new ArrayList<>();
        List<Genre> genreObjects = new ArrayList<>();
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
            Double pearson;
            pearson = (occGenres.get(i)-avgGenre)/Math.sqrt(summation);
            weights.add(pearson);
        }
        for(int i = 0; i < difGenres.size(); i++){
            genreObjects.add(new Genre(i, difGenres.get(i), weights.get(i)));
        }
        difGenres = null;
        Collections.sort(genreObjects, new Comparator<Genre>() {
            @Override
            public int compare(Genre lhs, Genre rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });

        return genreObjects;
    }

    public List<Pager> recommender(String u_id, String p_id){

        List<Genre> genres = calculateWeights(u_id, p_id);
        Collections.reverse(genres);
        Tracks tracks;
        Track track;
        artists.size();
        trackList.size();
        try{

            tracks = new asyncGetArtistTopTrack(new asyncGetArtistTopTrack.AsyncResponse(){
                @Override
                public void processFinish(Tracks output){
                }
            }).execute("6FBDaR13swtiWwGhX1WQsP").get();
            track = new asyncGetTrack(new asyncGetTrack.AsyncResponse(){
                @Override
                public void processFinish(Track output){
                }
            }).execute("1zHlj4dQ8ZAtrayhuDDmkY").get();
        }catch (Exception e){

        }




        return null;
    }

}

