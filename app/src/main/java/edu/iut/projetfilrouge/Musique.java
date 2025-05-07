package edu.iut.projetfilrouge;

public class Musique {
    private String titre;
    private String artiste;
    private int imageResId;
    private float rating;

    public Musique(String titre, String artiste, int imageResId) {
        this.titre = titre;
        this.artiste = artiste;
        this.imageResId = imageResId;
        this.rating = 0f;
    }

    public String getTitre() {
        return titre;
    }

    public String getArtiste() {
        return artiste;
    }

    public int getImageResId() {
        return imageResId;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
