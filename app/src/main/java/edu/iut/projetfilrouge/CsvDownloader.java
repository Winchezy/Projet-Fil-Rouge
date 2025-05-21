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

    public static File download(Context context, String urlString) {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlString).build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                Log.e("CSV", "Réponse non OK : " + response.code());
                return null;
            }

            File outputFile = new File(context.getCacheDir(), "lyrics.csv");
            InputStream in = response.body().byteStream();
            FileOutputStream out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            out.close();
            in.close();
            return outputFile;
        } catch (Exception e) {
            Log.e("CSV", "Erreur téléchargement : " + e.getMessage(), e);
            return null;
        }
    }
}
