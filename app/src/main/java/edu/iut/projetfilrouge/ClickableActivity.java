package edu.iut.projetfilrouge;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.PopupMenu;
import android.widget.Spinner;

public class ClickableActivity extends AppCompatActivity {

    private ListView listView;
    private List<Musique> musiqueList;
    private List<Musique> musiquesFiltrees;
    private MusiqueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        findViewById(R.id.btn_home).setOnClickListener(v -> {
            // déjà ici
        });

        findViewById(R.id.btn_music).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        ImageView imgView = findViewById(R.id.btn_other); // ou ton image

        imgView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("1");
            popupMenu.getMenu().add("2");
            popupMenu.getMenu().add("3");
            popupMenu.getMenu().add("4");
            popupMenu.getMenu().add("5");

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedNote = item.getTitle().toString();
                int seuil = Integer.parseInt(selectedNote);

                musiquesFiltrees.clear();
                SharedPreferences prefs = getSharedPreferences("notations", MODE_PRIVATE);

                for (Musique m : musiqueList) {
                    int note = prefs.getInt(m.getTitre(), 3);
                    if (note >= seuil) {
                        musiquesFiltrees.add(m);
                    }
                }

                adapter.notifyDataSetChanged();
                return true;
            });

            popupMenu.show();
        });




        listView = findViewById(R.id.lv_musiques);

        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv");
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier);
                runOnUiThread(() -> {
                    musiquesFiltrees = new java.util.ArrayList<>(musiqueList);
                    adapter = new MusiqueAdapter(this, musiquesFiltrees);
                    listView.setAdapter(adapter);

                    // Barre de recherche
                    android.widget.EditText searchBar = findViewById(R.id.search_bar);
                    searchBar.addTextChangedListener(new android.text.TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            musiquesFiltrees.clear();
                            for (Musique m : musiqueList) {
                                if (m.getTitre().toLowerCase().contains(s.toString().toLowerCase())) {
                                    musiquesFiltrees.add(m);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void afterTextChanged(android.text.Editable s) {}
                    });

                    // Clic sur une ligne de musique
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Musique musiqueSelectionnee = musiquesFiltrees.get(position);
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
