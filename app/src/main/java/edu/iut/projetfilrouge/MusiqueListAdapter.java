package edu.iut.projetfilrouge;// In MusiqueListAdapter.java

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView; // Assuming you have TextViews in your list item
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import edu.iut.projetfilrouge.Musique;

public class MusiqueListAdapter extends RecyclerView.Adapter<MusiqueListAdapter.MusiqueViewHolder> {

    private List<Musique> musiqueList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(@NonNull Musique musique);
    }

    public MusiqueListAdapter(List<Musique> musiqueList, OnItemClickListener listener) {
        this.musiqueList = musiqueList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MusiqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate your item layout here
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_musique, parent, false); // Replace R.layout.item_musique with your actual item layout file
        return new MusiqueViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MusiqueViewHolder holder, int position) {
        Musique currentMusique = musiqueList.get(position);
        holder.bind(currentMusique, listener);
        // Set data to your views here
        // For example: holder.textViewTitre.setText(currentMusique.getTitre());
    }

    @Override
    public int getItemCount() {
        return musiqueList.size();
    }

    // This is the method you need to add
    public void updateData(List<Musique> newMusiqueList) {
        if (newMusiqueList != null) {
            musiqueList.clear();
            musiqueList.addAll(newMusiqueList);
            notifyDataSetChanged(); // Notifies the RecyclerView that the data has changed
        }
    }

    static class MusiqueViewHolder extends RecyclerView.ViewHolder {
        // Declare your views here (e.g., TextView textViewTitre;)

        public MusiqueViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize your views here (e.g., textViewTitre = itemView.findViewById(R.id.textViewTitre);)
        }

        public void bind(final Musique musique, final OnItemClickListener listener) {
            // Set data to views
            // e.g., textViewTitre.setText(musique.getTitre());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(musique);
                }
            });
        }
    }
}