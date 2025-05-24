package edu.iut.projetfilrouge;

public class Musique {
    private String titre;
    private String album;
    private String artist;
    private String date;
    private String cover;
    private String lyrics;

    public Musique(String titre, String album, String artist, String date, String cover, String lyrics) {
        this.titre = titre;
        this.album = album;
        this.artist = artist;
        this.date = date;
        this.cover = cover;
        this.lyrics = lyrics;
    }

    public String getTitre() {
        return titre;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getDate() {
        return date;
    }

    public String getCover() {
        return cover;
    }

    public String getLyrics() { return lyrics; }
}

