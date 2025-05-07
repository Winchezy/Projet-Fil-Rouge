package edu.iut.projetfilrouge;

import android.content.Context;

public interface ClickableActivity {
    void onMusiqueClick(int index);
    void onRatingChanged(int index, float rating);
    Context getContext();
}
