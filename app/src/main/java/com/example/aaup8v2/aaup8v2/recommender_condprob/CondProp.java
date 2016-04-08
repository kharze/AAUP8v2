package com.example.aaup8v2.aaup8v2.recommender_condprob;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetArtists;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylistTracks;
import com.example.aaup8v2.aaup8v2.asyncTasks.asyncGetPlaylists;

import java.lang.Math;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

//Item based Collaborative Filtering Recommender using Conditional Probability
public class CondProp extends MainActivity {
    private String u_id;
    private Pager<PlaylistSimple> playListList;

    private List<String> mGenres;

    public CondProp(String user_id){
        setU_id(user_id);
    }

    //inverse document frequency, term frequency
    private List<Double> idf_tf(int[] tf, Double[] idf){
        List<Double> idf_tf = new ArrayList<>();

        for (int i = 0; idf[i] < idf.length; i++)
            idf_tf.add(Math.log(tf[i]) * idf[i]);
        return idf_tf;
    }




    //Returns a list of genres from a playlist
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

    public void Recommend(){
        new asyncGetPlaylists(new asyncGetPlaylists.AsyncResponse() {
            @Override
            public void processFinish(Pager<PlaylistSimple> output) {
                setPlayListList(output);
            }
        });
        String playlists = "";
        for (PlaylistSimple pl: getPlayListList().items) {
            playlists += pl + ",";
        }
        setmGenres(generateGenreList(getU_id(), playlists));
    }

    public String getU_id(){
        return u_id;
    }

    public void setU_id(String id){
        u_id = id;
    }

    public Pager<PlaylistSimple> getPlayListList() {
        return playListList;
    }

    public void setPlayListList(Pager<PlaylistSimple> playListList) {
        this.playListList = playListList;
    }

    public List<String> getmGenres() {
        return mGenres;
    }

    public void setmGenres(List<String> mGenres) {
        this.mGenres = mGenres;
    }
}