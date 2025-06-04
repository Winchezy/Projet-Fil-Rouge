package edu.iut.projetfilrouge;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    // Méthode pour lire un fichier CSV et retourner une liste d'objets Musique
    public static List<Musique> parseAll(File fichierCsv) {
        List<Musique> musiqueList = new ArrayList<>(); // Liste des musiques à retourner
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichierCsv)); // Lit le fichier ligne par ligne
            String line;
            boolean headerSkipped = false; // Pour ignorer la première ligne

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true; // Ignore la première ligne du fichier
                    continue;
                }

                String[] parts = line.split("#");
                if (parts.length >= 7) {
                    musiqueList.add(new Musique(
                            parts[0], // titre
                            parts[1], // album
                            parts[2], // artiste
                            parts[3], // date
                            parts[4], // image de couverture
                            parts[5], // paroles
                            parts[6]  // fichier mp3
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("DEBUG_CSV", "Erreur parsing : " + e.getMessage(), e);
        }

        return musiqueList; // Retourne la liste des musiques
    }
}