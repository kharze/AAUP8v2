package com.example.aaup8v2.aaup8v2.recommender_condprob;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylists;

import java.security.Key;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

//Item based Collaborative Filtering Recommender using Conditional Probability
public class CondProp extends MainActivity {
    private User localHost;

    //Constructor
    public CondProp(final String user_id){
        new asyncGetPlaylists(new asyncGetPlaylists.AsyncResponse() {
            @Override
            public void processFinish(Pager<PlaylistSimple> output) {
                localHost = new User(user_id, output.items, generateFullGenreList(output));
            }
        });
    }

    public List<String> generateFullGenreList(Pager<PlaylistSimple> playlists){
        List<String> result = null;
        for (PlaylistSimple playlist : playlists.items){
            result.addAll(generateGenreList(localHost.getID(), playlist.id));
        }
        return result;
    }

    //Returns a list of genres from a playlist,
    //given a string of User id and playlist id
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
        }catch (Exception e){}

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
                }catch (Exception e){}

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

    private void passToNetwork(User user){
        //Do stuff
    }

    public void Recommend(List<User> users){
        int total = 0;
        Map<String, Integer> genres = new HashMap<>();
        for (User user: users) {
            for(int i = 0; i < user.getmGenres().size(); i++){
                if (genres.containsKey(user.mGenreAt(i))){
                    genres.put(user.mGenreAt(i), genres.get(user.mGenreAt(i)) + user.mOccAt(i));
                } else {
                    genres.put(user.mGenreAt(i), user.mOccAt(i));
                }
                total += user.mOccAt(i);
            }
        }
        double simi = 0;
        for (String genreA : genres.keySet()){
            for (String genreB : genres.keySet()){
                if (!genreA.equals(genreB)){
                    double temp = sim(genres.get(genreA), genres.get(genreB), total);
                    if (temp > simi){
                        simi = temp;
                    }
                }
            }
        }
    }

    public double sim(Integer genreA, Integer genreB, int total){
        double pOfA = genreA/total;
        double pOfB = genreB/total;
        return (pOfA * pOfB)/pOfA;
    }
}

class User {
    private String mID;
    private List<PlaylistSimple> mPlaylists;
    private List<String> mGenres;
    private List<Integer> mOcc;

    public User(String user, List<PlaylistSimple> playlists, List<String> genres){
        this.mID = user;
        this.mPlaylists = playlists;
        this.mGenres = genres;
        this.mOcc = getGenreCount(genres);
    }

    public String getID(){return mID;}
    public void setID(String id){mID = id;}
    public List<PlaylistSimple> getPlaylists(){return mPlaylists;}
    public void setPlaylists(List<PlaylistSimple> lists) {this.mPlaylists = lists;}
    public List<String> getmGenres() {return mGenres;}
    public void setmGenres(List<String> mGenres) {this.mGenres = mGenres;}
    public void mGenresAdd(String s){this.mGenres.add(s);}
    public List<Integer> getmOcc(){return this.mOcc;}
    public void setmOcc(List<Integer> Occ){this.mOcc = Occ;}
    public String mGenreAt(int index){return this.mGenres.get(index);}
    public Integer mOccAt(int index){return this.mOcc.get(index);}

    private List<Integer> getGenreCount(List<String> genres){
        Collections.sort(genres);
        List<Integer> occGenre = new  ArrayList<>();

        int occurrence = 0;
        for (int i = 0; i < genres.size(); i++){
            if (this.getmGenres().contains(genres.get(i))){
                this.mGenresAdd(genres.get(i));
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
}