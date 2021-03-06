package com.example.aaup8v2.aaup8v2.Recommender;

import android.util.Pair;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.Runnables.GetArtistTopTrackRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetArtistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistTracksRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.ThreadResponseInterface;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by Lukas on 05-05-2016.
 **/
public class Recommender extends MainActivity {
    /**
    private Double[][] mGenreWeights;
    private Double[][] mArtistWeights;
    private ArrayList<String> mGenreOrder;
    private ArrayList<String> mArtistOrder;
     **/
    private RealMatrix userRatingsMatrix;
    private List<String> genresList = new ArrayList<>();
    private List<List<String>> userGenreLists = new ArrayList<>();
    private List<List<Integer>> userRatingLists = new ArrayList<>();
    private ArrayList<RecommenderArtist> artistObject = new ArrayList<>();
    public List<Track> recommendedTracks = new ArrayList<>();


    public Pair<List<RecommenderArtist>, List<RecommenderGenre>> userRecommendations;
    List<Artist> artistsList = new ArrayList<>();
    List<String> p_id = new ArrayList<>();
    List<String> playlistOwnerId = new ArrayList<>();
    List<Track> trackList = new ArrayList<>();
    Pager<PlaylistTrack> tracksPager;
    Artists mArtists;

    public Recommender(){
        userRecommendations = new Pair<List<RecommenderArtist>, List<RecommenderGenre>>(new ArrayList<RecommenderArtist>(), new ArrayList<RecommenderGenre>());
    }

    //Updates the matrix with a new user-row
    //Works on both genres and artists. Takes as param. Returns the updated matrix.
    //Bool for type of weights. True for mGenre. False for mArtist.
    /**
    public Double[][] addWeights(Double[][] lArr, ArrayList<String> inOrder, Double[] inArr, boolean isGenre){
        ArrayList<String> order;
        //Get the order in the arraylist.
        if (isGenre){
            order = mGenreOrder;
        } else{
            order = mArtistOrder;
        }

        //Add new occurrences to the order
        for (String str: inOrder) {
            if (!order.contains(str)){
                order.add(str);
            }
        }

        //Replace the old array - one dimension the size of new number of genres/artists
        Double[][] local = new Double[order.size()][];
        for (int i = 0; i < order.size(); i++){
            //Fill in each row with length of former array plus one (new peer)
            //Fill in old information
            local[i] = new Double[lArr[0].length + 1];
            for (int j = 0; j < lArr[0].length - 1; j++){
                local[i][j] = lArr[i][j];
            }
            //Last row is the new peer
            local[i][local[0].length - 1] = inArr[inOrder.indexOf(order.get(i))];
        }

        return local;
    }
     **/

    /*
    Getting a list of artist based on tracks from a playlist
     */


    public void getArtists(){
        try{
            new GetPlaylistsRunnable(MainActivity.me.id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistSimple>>() {
                @Override
                public void processFinish(Pager<PlaylistSimple> output) {
                    for (int i = 0; i < output.items.size(); i++) {
                        p_id.add(output.items.get(i).id);
                        playlistOwnerId.add(output.items.get(i).owner.id);
                    }
                }
            }).run();

            for(int k = 0; k < p_id.size(); k++){
                try{
                    new GetPlaylistTracksRunnable(playlistOwnerId.get(k), p_id.get(k), new ThreadResponseInterface.ThreadResponse<Pager<PlaylistTrack>>() {
                        @Override
                        public void processFinish(Pager<PlaylistTrack> output) {
                            tracksPager = output;
                        }
                    }).run();

                }catch (Exception e){
                    e.getMessage();
                }

                List<PlaylistTrack> tracksList = tracksPager.items;
                Track track;
                List<String> artistsIdList = new ArrayList<>();

                for(int j=0; j < tracksList.size(); j++){
                    track = tracksList.get(j).track;
                    trackList.add(track);
                    for(int i = 0; i < track.artists.size(); i++){
                        artistsIdList.add(track.artists.get(i).id);
                    }
                }
                try {
                            new GetArtistsRunnable(artistsIdList, new ThreadResponseInterface.ThreadResponse<Artists>() {
                                @Override
                                public void processFinish(Artists output) {
                                    mArtists = output;
                                }
                            }).run();
                            artistsList.addAll(mArtists.artists);
                }catch (Exception e) {
                    e.getMessage();
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        putArtistList(artistsList);
        putGenreList(artistsList);
    }

    /*
    Converting artist list to a list of new artist objects contaning artist and weight of the artist
     */
    private void putArtistList(List<Artist> artistsList){
        List<String> difArtists = new ArrayList<>();
        List<Integer> occArtist = new ArrayList<>();
        List<Integer> artistPop = new ArrayList<>();
        List<List<String>> genresList = new ArrayList<>();

        Collections.sort(artistsList, new Comparator<Artist>() {
            @Override
            public int compare(Artist lhs, Artist rhs) {
                return lhs.id.compareTo(rhs.id);
            }
        });

        int occurrence = 0;
        for (int i = 0; i < artistsList.size(); i++){
            if (!difArtists.contains(artistsList.get(i).id)){
                difArtists.add(artistsList.get(i).id);
                artistPop.add(artistsList.get(i).popularity);
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
                if (i == artistsList.size()- 1){
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
        List<RecommenderArtist> artistObjects = new ArrayList<>();
        for (int i = 0; i < occArtist.size(); i++){
            artistObjects.add(new RecommenderArtist(difArtists.get(i), artistPop.get(i), occArtist.get(i), genresList.get(i), null));
        }

        Collections.sort(artistObjects, new Comparator<RecommenderArtist>() {
            @Override
            public int compare(RecommenderArtist lhs, RecommenderArtist rhs) {
                return lhs.name.compareTo(rhs.name);
            }
        });

        userRecommendations.first.addAll(artistObjects);
    }
    /*
    Generating a list of new genre object which contains id, genre and weight
     */

    private void putGenreList(List<Artist> artistsList){
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
        List<RecommenderGenre> genreObjects = new ArrayList<>();
        for (int i = 0; i < occGenre.size(); i++){
            genreObjects.add(new RecommenderGenre(difGenres.get(i), occGenre.get(i)));
        }

        Collections.sort(genreObjects, new Comparator<RecommenderGenre>() {
            @Override
            public int compare(RecommenderGenre lhs, RecommenderGenre rhs) {
                return lhs.getGenre().compareTo(rhs.getGenre());
            }
        });
        userRecommendations.second.addAll(genreObjects);
    }

    public void extractUserInfo (Pair<List<RecommenderArtist>, List<RecommenderGenre>> userArtistGenres){

        ArrayList<RecommenderGenre> genresObject = new ArrayList<>();
        List<Integer> genreRatings = new ArrayList<>();
        List<String> genreName = new ArrayList<>();
        genresObject.addAll(userArtistGenres.second);
        artistObject.addAll(userArtistGenres.first);
        HashSet<String> unionGenres = new HashSet<>();

        for (int i = 0; i < genresObject.size(); i++){
            genreRatings.add(genresObject.get(i).count);
            genreName.add(genresObject.get(i).genre);
        }
        userGenreLists.add(genreName);
        userRatingLists.add(genreRatings);

        unionGenres.addAll(genreName);
        unionGenres.addAll(genresList);
        genresList = new ArrayList<>(unionGenres);
        Collections.sort(genresList);

        createRatingMatrix();

    }

    private void createRatingMatrix (){

        userRatingsMatrix = new Array2DRowRealMatrix(userRatingLists.size(), genresList.size());

        for(int i = 0; i < userGenreLists.size(); i++){
            double[] row = new double[genresList.size()];
            List<Integer> userRatings = new ArrayList<>();
            userRatings.addAll(userRatingLists.get(i));
            List<String> userGenres = new ArrayList<>();
            userGenres.addAll(userGenreLists.get(i));
            for (int j = 0; j < genresList.size(); j++){
                String lGenre = genresList.get(j);
                if(userGenres.size() > 0){
                    if(userGenres.get(0).equals(lGenre)){
                        row[j] = userRatings.get(0);
                        userGenres.remove(0);
                        userRatings.remove(0);
                    } else {
                        row[j] = 0;
                    }
                }
                else {
                    row[j] = 0;
                }
            }
            userRatingsMatrix.setRow(i, row);
        }
        pearsonSim(userRatingsMatrix);
    }

    private void pearsonSim(RealMatrix userMatrix) {
        int rowSize = userRatingsMatrix.getColumn(0).length;
        int columnSize = userRatingsMatrix.getRow(0).length;

        RealMatrix userRatingsMatrix;
        userRatingsMatrix = userMatrix;
        List<List<Double>> userRatingList = new ArrayList<>();
        for (int i = 0; i < userMatrix.getColumn(0).length; i++) {
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j < userMatrix.getRow(0).length; j++) {
                temp.add(userRatingsMatrix.getEntry(i, j));
            }
            userRatingList.add(temp);
        }

        for (int i = 0; i < userMatrix.getColumn(0).length; i++) {
            List<Double> temp = new ArrayList<>();
            temp.addAll(userRatingList.get(i));
            for (int j = 0; j < userMatrix.getRow(0).length; j++) {
                if (temp.get(j) == 0) {
                    userRatingList.get(i).set(j, predictRating(userRatingList, i, j));
                }
            }
        }

        RealMatrix normalizedUserRatings = new Array2DRowRealMatrix(rowSize, columnSize);

        for (int i = 0; i < userRatingList.size(); i++){
            double[] row = new double[userRatingList.get(i).size()];
            for (int j = 0; j < userRatingList.get(i).size(); j++){
                row[j] = userRatingList.get(i).get(j);
            }
            normalizedUserRatings.setRow(i, StatUtils.normalize(row));
        }
        recommendGenre(normalizedUserRatings);

    }

    private Double predictRating(List<List<Double>> userRatings, int row, int column){

        List <List<Double>> userRatingsList = new ArrayList<>();
        userRatingsList.addAll(userRatings);

        List<Double> simList = new ArrayList<>();

        List<Double> userList = new ArrayList<>();
        userList.addAll(userRatingsList.get(row));
        double userAvg = 0;
        userRatingsList.remove(row);
        double wSum = 0;
        PearsonsCorrelation pearson = new PearsonsCorrelation();
        List<Double> itemRating = new ArrayList<>();
        List<Double> avgRating = new ArrayList<>();

        for(int i = 0; i < userRatingsList.size(); i++){
            List<Double> temp = new ArrayList<>();
            temp.addAll(userRatingsList.get(i));
            if (temp.get(column) != 0){
                List<Double> localUserList = new ArrayList<>();
                itemRating.add(temp.get(column));
                localUserList.addAll(userList);
                localUserList.remove(column);
                temp.remove(column);
                for(int j = 0; j < temp.size(); j++){
                    if(temp.get(j) == 0){
                        temp.remove(j);
                        localUserList.remove(j);
                    }
                }
                for(int j = column; j < localUserList.size(); j++){
                    if(localUserList.get(j) == 0){
                        temp.remove(j);
                        localUserList.remove(j);
                    }
                }
                double[] tempArray = new double[temp.size()];
                double[] localUserArray = new double[localUserList.size()];
                for (int j = 0; j < temp.size(); j++){
                    tempArray[j] = temp.get(j);
                    localUserArray[j] = localUserList.get(j);
                }
                userAvg = StatUtils.mean(localUserArray);
                double simValue = pearson.correlation(localUserArray, tempArray);
                if (simValue >= 0){
                    wSum += simValue;
                    simList.add(simValue);
                    avgRating.add(StatUtils.mean(tempArray));
                }

            }
        }

        double prediction = 0.0;

        for (int i = 0; i < simList.size(); i++){
            double w = simList.get(i)/wSum;
            prediction += w * (itemRating.get(i) - avgRating.get(i) );
        }

        //userList.remove(column);
        prediction += userAvg;
        return prediction;
    }

    private void recommendGenre (RealMatrix userRatings){

        int columnSize = userRatings.getRow(0).length;

        double[] genreScore = new double[columnSize];

        for (int i = 0; i < columnSize; i++ ){
            double[] temp = userRatings.getColumn(i);
            genreScore[i] = StatUtils.mean(temp)/2 + StatUtils.min(temp);
        }

        Double bestGenre = StatUtils.max(genreScore);
        int bestGenreIndex = 0;
        for(int i = 0; i < genreScore.length; i++){
            if (genreScore[i] == bestGenre){
                bestGenreIndex = i;
            }
        }
        findTracks(genresList.get(bestGenreIndex));
    }

    private void findTracks(final String genre){

        Collections.sort(artistObject, new Comparator<RecommenderArtist>() {
            @Override
            public int compare(RecommenderArtist lhs, RecommenderArtist rhs) {
                return lhs.popularity.compareTo(rhs.popularity);
            }
        });
        Collections.reverse(artistObject);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> topArtists = new ArrayList<>();
                int counter = 0;
                while (topArtists.size() < 10 && counter < artistObject.size()){
                    if (artistObject.get(counter).genre.contains(genre)){
                        topArtists.add(artistObject.get(counter).name);
                    }
                    counter++;
                }

                for(int i = 0; i < topArtists.size(); i++){
                    new GetArtistTopTrackRunnable(topArtists.get(i), new ThreadResponseInterface.ThreadResponse<Tracks>() {
                        @Override
                        public void processFinish(Tracks output) {
                            for(int j = 0; j < output.tracks.size(); j++){
                                recommendedTracks.add(output.tracks.get(j));
                            }
                        }
                    }).run();
                }
            }
        }).start();

    }

    public void sendToHost(){
        Gson gson = new Gson();
        String data = gson.toJson(userRecommendations);
        MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.RECOMMENDER, data);
    }
}
