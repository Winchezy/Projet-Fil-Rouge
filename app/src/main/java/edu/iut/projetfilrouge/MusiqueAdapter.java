package edu.iut.projetfilrouge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MusiqueAdapter extends BaseAdapter {
    private Context context; // Le contexte de l'application
    private List<Musique> musiqueList; // La liste des musiques à afficher

    public MusiqueAdapter(Context context, List<Musique> musiqueList) {
        this.context = context;
        this.musiqueList = musiqueList;
    }

    @Override
    public int getCount() {
        return musiqueList.size(); // Nombre total d’éléments dans la liste
    }

    @Override
    public Object getItem(int position) {
        return musiqueList.get(position); // Récupère une musique à une position donnée
    }

    @Override
    public long getItemId(int position) {
        return position; // Identifiant de l’élément
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        // On crée la vue à partir du layout XML
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.musique_layout, parent, false);
        }

        // Récupère l’objet Musique à la position actuelle
        Musique musique = musiqueList.get(position);

        TextView tvTitre = view.findViewById(R.id.tv_titre);
        TextView tvArtiste = view.findViewById(R.id.tv_artiste);
        ImageView imgMusique = view.findViewById(R.id.img_musique);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        tvTitre.setText(musique.getTitre());
        tvArtiste.setText(musique.getArtist());

        String imageUrl = "http://edu.info06.net/lyrics/images/" + musique.getCover();
        Glide.with(context).load(imageUrl).into(imgMusique);

        // Récupère la note enregistrée dans les préférences (par défaut 3)
        android.content.SharedPreferences prefs = context.getSharedPreferences("notations", Context.MODE_PRIVATE);
        int note = prefs.getInt(musique.getTitre(), 3);
        ratingBar.setRating(note); // Applique la note sur les étoiles

        // Quand l’utilisateur change la note, on la sauvegarde
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                prefs.edit().putInt(musique.getTitre(), (int) rating).apply(); // Sauvegarde
            }
        });

        return view; // Retourne la vue à afficher dans la liste
    }
}
