package com.example.aaup8v2.aaup8v2.Recommender;

import com.example.aaup8v2.aaup8v2.MainActivity;
import com.example.aaup8v2.aaup8v2.Runnables.GetArtistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistTracksRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.GetPlaylistsRunnable;
import com.example.aaup8v2.aaup8v2.Runnables.ThreadResponseInterface;
import com.example.aaup8v2.aaup8v2.wifidirect.WifiDirectActivity;
import com.google.gson.Gson;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.Artists;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by Lukas on 05-05-2016.
 **/
public class Recommender extends MainActivity {
    private Double[][] mGenreWeights;
    private Double[][] mArtistWeights;
    private ArrayList<String> mGenreOrder;
    private ArrayList<String> mArtistOrder;

    HashMap<List<RecommenderArtist>, List<RecommenderGenre>> userRecommendations = new HashMap<>();
    List<Artist> artistsList = new ArrayList<>();
    List<String> p_id = new ArrayList<>();
    List<String> playlistOwnerId = new ArrayList<>();
    List<Track> trackList = new ArrayList<>();
    Pager<PlaylistTrack> tracksPager;
    Artists mArtists;


    //Initializes recommender
    //Takes user id - Currently not implemented
    /**
    private void init(String uid){
        weightAdjust(uid);
    }
**/
    //Updates the matrix with a new user-row
    //Works on both genres and artists. Takes as param. Returns the updated matrix.
    //Bool for type of weights. True for mGenre. False for mArtist.
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

    /*
    Getting a list of artist based on tracks from a playlist
     */


    private List<Artist> getArtists(String u_id){
        try{

            new GetPlaylistsRunnable(u_id, new ThreadResponseInterface.ThreadResponse<Pager<PlaylistSimple>>() {
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
                            new GetArtistsRunnable(artistsRequests, new ThreadResponseInterface.ThreadResponse<Artists>() {
                                @Override
                                public void processFinish(Artists output) {
                                    mArtists = output;
                                }
                            }).run();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        for (int i = 0; i < mArtists.artists.size(); i++) {
                            artistsList.add(mArtists.artists.get(i));
                        }
                    } while (!artistsIdList.isEmpty());

                }catch (Exception e) {
                    e.getMessage();
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return artistsList;
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
                return lhs.id.compareTo(rhs.id);
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
            artistObjects.add(new RecommenderArtist(difArtists.get(i), null, occArtist.get(i), genresList.get(i), null));
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
        List<RecommenderGenre> genreObjects = new ArrayList<>();
        for (int i = 0; i < occGenre.size(); i++){
            genreObjects.add(new RecommenderGenre(i, difGenres.get(i), null, occGenre.get(i)));
        }

        Collections.sort(genreObjects, new Comparator<RecommenderGenre>() {
            @Override
            public int compare(RecommenderGenre lhs, RecommenderGenre rhs) {
                return lhs.weight.compareTo(rhs.weight);
            }
        });
        return genreObjects;
    }

    public void pearsonSim(/**RealMatrix userRatingsMatrix**/) {
        //int columnSize = userRatingsMatrix.getColumn(0).length;
        //int rowSize = userRatingsMatrix.getRow(0).length;

        RealMatrix userRatingsMatrix = new Array2DRowRealMatrix(4, 5);
        int columnSize = userRatingsMatrix.getColumn(0).length;
        int rowSize = userRatingsMatrix.getRow(0).length;
        List<List<Double>> userRatingList = new ArrayList<>();

        double[] row1 = {4, 3, 5, 0, 0};
        double[] row2 = {3, 4, 5, 1, 3};
        double[] row3 = {5, 2, 4, 3, 5};
        double[] row4 = {1, 3, 3, 3, 4};

        userRatingsMatrix.setRow(0, row1);
        userRatingsMatrix.setRow(1, row2);
        userRatingsMatrix.setRow(2, row3);
        userRatingsMatrix.setRow(3, row4);


        for (int i = 0; i < columnSize; i++) {
            List<Double> temp = new ArrayList<>();
            for (int j = 0; j < rowSize; j++) {
                temp.add(userRatingsMatrix.getEntry(i, j));
            }
            userRatingList.add(temp);
        }

        for (int i = 0; i < columnSize; i++) {
            List<Double> temp = new ArrayList<>();
            temp.addAll(userRatingList.get(i));
            for (int j = 0; j < rowSize; j++) {
                if (temp.get(j) == 0) {
                    userRatingList.get(i).set(j, predictRating(userRatingList, i, j));
                    //Double predicted = predictRating(userRatingList, i, j);
                }
            }
        }

        RealMatrix normalizedUserRatings = new Array2DRowRealMatrix(4, 5);

        for (int i = 0; i < userRatingList.size(); i++){
            double[] row = new double[userRatingList.get(i).size()];
            for (int j = 0; j < userRatingList.get(i).size(); j++){
                row[j] = userRatingList.get(i).get(j);
            }
            normalizedUserRatings.setRow(i, StatUtils.normalize(row));
        }
        recommend(normalizedUserRatings);

    }

    public Double predictRating(List<List<Double>> userRatings, int row, int column){

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
                avgRating.add(StatUtils.mean(tempArray));
                userAvg = StatUtils.mean(localUserArray);
                double simValue = pearson.correlation(localUserArray, tempArray);
                if (simValue >= 0){
                    wSum += simValue;
                    simList.add(simValue);
                }

            }
        }

        double prediction = 0.0;

        for (int i = 0; i < simList.size(); i++){
            double w = simList.get(i)/wSum;
            prediction += w * (itemRating.get(i) - avgRating.get(i) );
        }

        userList.remove(column);
        prediction += userAvg;
        return prediction;
    }

    public List<Track> recommend (RealMatrix userRatings){

        int columnSize = userRatings.getRow(0).length;
        int rowSize = userRatings.getColumn(0).length;
        RealMatrix ratings = new Array2DRowRealMatrix(rowSize, columnSize);
        ratings = userRatings;

        List<Double> genreScor = new ArrayList<>();
        List<Double> avgList = new ArrayList<>();
        List<Double> minList = new ArrayList<>();


        for (int i = 0; i < columnSize; i++ ){
            double[] temp = ratings.getColumn(i);
            avgList.add(StatUtils.mean(temp));
            minList.add(StatUtils.min(temp));
            genreScor.add((StatUtils.mean(temp)/2) + StatUtils.min(temp));
        }

        return null;
    }

    public void sendToHost(){
        Gson data = new Gson();
        data.toJson(userRecommendations);

        MainActivity.mWifiDirectActivity.sendDataToHost(WifiDirectActivity.RECOMMENDER, data.toString());
    }
}
