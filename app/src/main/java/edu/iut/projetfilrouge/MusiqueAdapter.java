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
    private Context context;
    private List<Musique> musiqueList;

    public MusiqueAdapter(Context context, List<Musique> musiqueList) {
        this.context = context;
        this.musiqueList = musiqueList;
    }

    @Override
    public int getCount() {
        return musiqueList.size();
    }

    @Override
    public Object getItem(int position) {
        return musiqueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.musique_layout, parent, false);
        }

        Musique musique = musiqueList.get(position);

        TextView tvTitre = view.findViewById(R.id.tv_titre);
        TextView tvArtiste = view.findViewById(R.id.tv_artiste);
        ImageView imgMusique = view.findViewById(R.id.img_musique);
        RatingBar ratingBar = view.findViewById(R.id.ratingBar);

        tvTitre.setText(musique.getTitre());
        tvArtiste.setText(musique.getArtist());

        // Charger l’image de couverture
        String imageUrl = "http://edu.info06.net/lyrics/images/" + musique.getCover();
        Glide.with(context).load(imageUrl).into(imgMusique);

        // On peut ignorer le rating pour l’instant ou le remplir par défaut
        ratingBar.setRating(3);

        return view;
    }
}
