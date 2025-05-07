package edu.iut.projetfilrouge;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ClickableActivity {

    private MusiqueAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = findViewById(R.id.lv_musiques);

        ArrayList<Musique> musiques = new ArrayList<>();
        musiques.add(new Musique("Imagine", "John Lennon", R.drawable.ic_launcher_foreground));
        musiques.add(new Musique("Bohemian Rhapsody", "Queen", R.drawable.ic_launcher_foreground));
        musiques.add(new Musique("Billie Jean", "Michael Jackson", R.drawable.ic_launcher_foreground));

        adapter = new MusiqueAdapter(musiques, this);
        lv.setAdapter(adapter);
    }

    @Override
    public void onMusiqueClick(int index) {
        // Gérer le clic sur une musique (lecture, détails, etc.)
    }

    @Override
    public void onRatingChanged(int index, float rating) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
