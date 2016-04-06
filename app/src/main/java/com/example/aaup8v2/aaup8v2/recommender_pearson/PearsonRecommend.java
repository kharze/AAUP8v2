package com.example.aaup8v2.aaup8v2.recommender_pearson;

import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by lasse on 21-03-2016.
 */

public class PearsonRecommend{
    public List<String> generateGenreList(String u_id, String p_id){

        Pager tracksPager= null;
        Track track;
        Track t = null;
        Artists mArtists = null;
        List<String> artistsList = new ArrayList<>();
        try{
            tracksPager = new asyncGetPlaylistTracks(new asyncGetPlaylistTracks.AsyncResponse(){
                @Override
                public void processFinish(Pager output){
                }
            }).execute(u_id, p_id).get();
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

    public Map<String, Integer> calculateWeights(String u_id, String p_id){
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
        Map<String, Integer> genresValue = new HashMap<>();
        for(int i = 0; i < difGenres.size(); i++){
            genresValue.put(difGenres.get(i), occGenre.get(i));
        }

        return genresValue;
    }

    public void pearsonRecommender(String u_id, String p_id){
        Map<String, Integer> genres = calculateWeights(u_id, p_id);

        Iterator iterator = genres.keySet().iterator();
        List<String> genresTypesList = new ArrayList<>();
        List<Integer> genresCountList = new ArrayList<>();

        while (iterator.hasNext()){
            Object key = genres.keySet().iterator();
            Object value = genres.get(key);
            genresTypesList.add((String) key);
            genresCountList.add((int) value);
        }
        int o = 10;

    }
}

