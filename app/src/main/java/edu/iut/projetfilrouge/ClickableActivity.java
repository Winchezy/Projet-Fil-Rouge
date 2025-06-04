package edu.iut.projetfilrouge;

import android.content.SharedPreferences;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.List;

import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.content.Intent;
import android.widget.PopupMenu;
import android.widget.TextView;

public class ClickableActivity extends AppCompatActivity {

    private ListView listView; // Liste visuelle des musiques
    private List<Musique> musiqueList; // Toutes les musiques chargées
    private List<Musique> musiquesFiltrees; // Musiques visibles après filtrage
    private MusiqueAdapter adapter; // Adaptateur pour afficher les musiques dans la ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Cache la barre de navigation et met l'app en plein écran
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        TextView textView = findViewById(R.id.app_title);
        TextPaint paint = textView.getPaint(); // Récupère le style du texte
        float width = paint.measureText(textView.getText().toString()); // Mesure la largeur du texte

        // Crée un dégradé horizontal avec deux couleurs
        Shader shader = new LinearGradient(
                0, 0, width, 0,
                new int[]{
                        ContextCompat.getColor(this, R.color.degrade_bleu),
                        ContextCompat.getColor(this, R.color.degrade_violet)
                },
                null,
                Shader.TileMode.CLAMP);

        paint.setShader(shader); // Applique le dégradé au texte
        textView.invalidate(); // Redessine le texte avec l'effet appliqué

        // Bouton musique : ouvre MainActivity
        findViewById(R.id.btn_music).setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Évite de créer une nouvelle instance
            startActivity(intent);
        });

        // icône à droite dans le menu
        ImageView imgView = findViewById(R.id.btn_other);

        // Quand on clique dessus, on affiche un menu avec les notes 1 à 5
        imgView.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v); // Crée un menu contextuel
            popupMenu.getMenu().add("1");
            popupMenu.getMenu().add("2");
            popupMenu.getMenu().add("3");
            popupMenu.getMenu().add("4");
            popupMenu.getMenu().add("5");

            // Quand une note est sélectionnée dans le menu
            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedNote = item.getTitle().toString(); // Récupère la note choisie
                int seuil = Integer.parseInt(selectedNote); // Convertit en entier

                musiquesFiltrees.clear(); // Vide la liste filtrée
                SharedPreferences prefs = getSharedPreferences("notations", MODE_PRIVATE); // Récupère les notes sauvegardées

                // Garde les musiques dont la note est >= au seuil
                for (Musique m : musiqueList) {
                    int note = prefs.getInt(m.getTitre(), 3); // Note par défaut : 3
                    if (note >= seuil) {
                        musiquesFiltrees.add(m); // Ajoute à la liste filtrée
                    }
                }

                adapter.notifyDataSetChanged(); // Met à jour l'affichage
                return true;
            });

            popupMenu.show(); // Affiche le menu

            // Remet le plein écran après un court délai
            new Handler().postDelayed(() -> {
                View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            }, 100);
        });

        listView = findViewById(R.id.lv_musiques); // Récupère la ListView depuis le layout

        // Lance une tâche en arrière-plan pour télécharger et charger les musiques
        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv"); // Télécharge le CSV
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier); // Parse le CSV
                runOnUiThread(() -> { // Une fois terminé, met à jour l’interface sur le thread principal
                    musiquesFiltrees = new java.util.ArrayList<>(musiqueList); // Copie toutes les musiques
                    adapter = new MusiqueAdapter(this, musiquesFiltrees); // Prépare l’adaptateur
                    listView.setAdapter(adapter); // Affiche les musiques dans la ListView

                    // barre de recherche
                    android.widget.EditText searchBar = findViewById(R.id.search_bar);
                    searchBar.addTextChangedListener(new android.text.TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            musiquesFiltrees.clear(); // Vide la liste visible
                            String query = s.toString().toLowerCase(); // Texte tapé en minuscules

                            // Ajoute les musiques qui correspondent à la recherche
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
                            adapter.notifyDataSetChanged(); // Rafraîchit l'affichage
                        }

                        @Override
                        public void afterTextChanged(android.text.Editable s) {}
                    });

                    // Gère le clic sur une musique : ouvre MainActivity avec la musique choisie
                    listView.setOnItemClickListener((parent, view, position, id) -> {
                        Musique musiqueSelectionnee = musiquesFiltrees.get(position);
                        Log.d("DEBUG_CLIC", "Musique cliquée : " + musiqueSelectionnee.getTitre());

                        Intent intent = new Intent(ClickableActivity.this, MainActivity.class);
                        intent.putExtra("musique_titre", musiqueSelectionnee.getTitre()); // Passe le titre sélectionné
                        startActivity(intent); // Lance la lecture
                    });
                });
            } else {
                Log.e("DEBUG_CSV", "Fichier CSV non téléchargé");
            }
        }).start(); // Démarre le thread
    }
}
