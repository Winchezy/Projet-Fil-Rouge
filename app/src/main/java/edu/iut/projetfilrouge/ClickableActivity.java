package edu.iut.projetfilrouge;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

public class ClickableActivity extends AppCompatActivity {

    private ListView listView;
    private List<Musique> musiqueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Tu l’as demandé :)

        listView = findViewById(R.id.lv_musiques);

        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv");
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier);
                runOnUiThread(() -> {
                    MusiqueAdapter adapter = new MusiqueAdapter(this, musiqueList);
                    listView.setAdapter(adapter);
                });
            } else {
                Log.e("DEBUG_CSV", "Fichier CSV non téléchargé");
            }
        }).start();
    }
}
