package edu.iut.projetfilrouge;

public class Musique {
    private String titre;
    private String album;
    private String artist;
    private String date;
    private String cover;
    private String lyrics;
    private String mp3;

    public Musique(String titre, String album, String artist, String date, String cover, String lyrics, String mp3) {
        this.titre = titre;
        this.album = album;
        this.artist = artist;
        this.date = date;
        this.cover = cover;
        this.lyrics = lyrics;
        this.mp3 = mp3;
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

    public String getMp3() { return mp3; }
}

