package edu.iut.projetfilrouge;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

public class MusiqueAdapter extends BaseAdapter {

    private List<Musique> musiques;
    private LayoutInflater inflater;
    private ClickableActivity activity;

    public MusiqueAdapter(List<Musique> musiques, ClickableActivity activity) {
        this.musiques = musiques;
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity.getContext());
    }

    @Override
    public int getCount() {
        return musiques.size();
    }

    @Override
    public Object getItem(int position) {
        return musiques.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = inflater.inflate(R.layout.musique_layout, parent, false);

        Musique musique = musiques.get(position);

        TextView titre = layout.findViewById(R.id.tv_titre);
        TextView artiste = layout.findViewById(R.id.tv_artiste);
        ImageView image = layout.findViewById(R.id.img_musique);
        RatingBar ratingBar = layout.findViewById(R.id.ratingBar);

        titre.setText(musique.getTitre());
        artiste.setText(musique.getArtiste());
        image.setImageResource(musique.getImageResId());
        ratingBar.setRating(musique.getRating());

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            musique.setRating(rating);
            activity.onRatingChanged(position, rating);
        });

        layout.setOnClickListener(v -> activity.onMusiqueClick(position));

        return layout;
    }
}
