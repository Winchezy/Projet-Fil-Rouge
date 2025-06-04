package edu.iut.projetfilrouge;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
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
import androidx.core.content.ContextCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

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
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        TextView textView = findViewById(R.id.app_title);

        TextPaint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        Shader shader = new LinearGradient(
                0, 0, width, 0, // gauche → droite
                new int[]{
                        ContextCompat.getColor(this, R.color.degrade_bleu),
                        ContextCompat.getColor(this, R.color.degrade_violet)
                },
                null,
                Shader.TileMode.CLAMP);

        paint.setShader(shader);
        textView.invalidate();


        findViewById(R.id.btn_home).setOnClickListener(v -> {
            // déjà ici
        });

        findViewById(R.id.btn_music).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        ImageView imgView = findViewById(R.id.btn_other);

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

            // Réactiver le mode plein écran après une courte pause
            new Handler().postDelayed(() -> {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }, 100); // 100 ms pour laisser le menu apparaître
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
                            String query = s.toString().toLowerCase();

                            for (Musique m : musiqueList) {
                                if (
                                        m.getTitre().toLowerCase().contains(query) ||
                                                m.getArtist().toLowerCase().contains(query) ||
                                                m.getAlbum().toLowerCase().contains(query) ||
                                                String.valueOf(m.getDate()).contains(query) ||
                                                m.getLyrics().toLowerCase().contains(query)
                                ) {
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
