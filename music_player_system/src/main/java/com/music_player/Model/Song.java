package com.music_player.Model;

public class Song{
    private int id;
    private String title;
    private String artist;
    private String genre;
    private String fileName;
    private int playCount;
    private boolean favorite;

    public Song() {}

    //uuniig AddSongController d ashiglana
    public Song(int id, String title, String artist, String genre, String fileName) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.fileName = fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public boolean isFavorite() { 
        return favorite; }

    public void setFavorite(boolean favorite) { 
        this.favorite = favorite; }
}