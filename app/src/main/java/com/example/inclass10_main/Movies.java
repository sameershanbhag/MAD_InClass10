package com.example.inclass10_main;

import android.util.Log;

import java.util.Map;

public class Movies {

    private String MovieName;
    private String Description;
    private String Genre;
    private int Rating;
    private int Year;
    private String IMDB;
    private String MovieID;

    public String getMovieName() {
        return MovieName;
    }

    public void setMovieName(String movieName) {
        MovieName = movieName;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public String getIMDB() {
        return IMDB;
    }

    public void setIMDB(String IMDB) {
        this.IMDB = IMDB;
    }

    public String getMovieID() {
        return MovieID;
    }

    public void setMovieID(String movieID) {
        MovieID = movieID;
    }

    @Override
    public String toString() {
        return "Movies{" +
                "MovieName='" + MovieName + '\'' +
                ", Description='" + Description + '\'' +
                ", Genre='" + Genre + '\'' +
                ", Rating=" + Rating +
                ", Year=" + Year +
                ", IMDB='" + IMDB + '\'' +
                ", MovieID=" + MovieID +
                '}';
    }

    public Movies(Map MovieMap) {
        this.MovieName = (String) MovieMap.get("movieName");
        this.Description = (String) MovieMap.get("description");
        this.Genre = (String) MovieMap.get("genre");
        this.Rating = (int) (long) MovieMap.get("rating");
        this.Year = (int) (long) MovieMap.get("year");
        this.IMDB = (String) MovieMap.get("imdb");
        this.MovieID = (String) MovieMap.get("movieID");
    }

    public Movies() {
        // Empty constructor
    }

}
