package edu.iut.projetfilrouge;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

public class ClickableActivity extends AppCompatActivity {

    private ListView listView;
    private List<Musique> musiqueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_home).setOnClickListener(v -> {
            // déjà ici
        });

        findViewById(R.id.btn_music).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });



        findViewById(R.id.btn_other).setOnClickListener(v -> {
            // action future
        });


        listView = findViewById(R.id.lv_musiques);

        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv");
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier);
                runOnUiThread(() -> {
                    MusiqueAdapter adapter = new MusiqueAdapter(this, musiqueList);
                    listView.setAdapter(adapter);

                    // Clic sur une ligne de musique
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Musique musiqueSelectionnee = musiqueList.get(position);
                        Log.d("DEBUG_CLIC", "Musique cliquée : " + musiqueSelectionnee.getTitre()); // <-- ICI

                        Intent intent = new Intent(ClickableActivity.this, MainActivity.class);
                        intent.putExtra("musique_titre", musiqueSelectionnee.getTitre());
                        startActivity(intent);
                    });
                });
            } else {
                Log.e("DEBUG_CSV", "Fichier CSV non téléchargé");
            }
        }).start();
    }

}
