package edu.iut.projetfilrouge;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvParser {

    public static List<Musique> parseAll(File fichierCsv) {
        List<Musique> musiqueList = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fichierCsv));
            String line;
            boolean headerSkipped = false;

            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                String[] parts = line.split("#");
                if (parts.length >= 7) {
                    musiqueList.add(new Musique(
                            parts[0], // title
                            parts[1], // album
                            parts[2], // artist
                            parts[3], // date
                            parts[4], // cover
                            parts[5], // lyrics
                            parts[6]  // mp3
                    ));
                }
            }
        } catch (Exception e) {
            Log.e("DEBUG_CSV", "Erreur parsing : " + e.getMessage(), e);
        }

        return musiqueList;
    }
}