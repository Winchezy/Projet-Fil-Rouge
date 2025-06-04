package edu.iut.projetfilrouge;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CsvDownloader {

    // Méthode pour télécharger un fichier CSV depuis une URL
    public static File download(Context context, String urlString) {
        try {
            OkHttpClient client = new OkHttpClient(); // Création d'un client HTTP
            Request request = new Request.Builder().url(urlString).build(); // Prépare la requête vers l'URL
            Response response = client.newCall(request).execute(); // Exécute la requête

            // Si la réponse n'est pas réussie, affiche une erreur et retourne null
            if (!response.isSuccessful()) {
                Log.e("CSV", "Réponse non OK : " + response.code());
                return null;
            }

            // Crée un fichier temporaire dans le cache de l'application
            File outputFile = new File(context.getCacheDir(), "lyrics.csv");

            // Récupère le contenu de la réponse en flux
            InputStream in = response.body().byteStream();
            FileOutputStream out = new FileOutputStream(outputFile);

            // Lit le contenu et l’écrit dans le fichier local
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            // Ferme les flux
            out.close();
            in.close();

            return outputFile; // Retourne le fichier téléchargé
        } catch (Exception e) {
            Log.e("CSV", "Erreur téléchargement : " + e.getMessage(), e);
            return null;
        }
    }
}