package edu.iut.projetfilrouge;

import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextPaint;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

// Classe pour l'écran de chargement au lancement de l'application
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Lance la prochaine activité (ClickableActivity) après 2 secondes
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, ClickableActivity.class); // Prépare l’intent
            startActivity(intent); // Lance l’activité principale
            finish(); // Termine l’activité splash pour qu’elle ne reste pas dans l’historique
        }, 2000);

        TextView textView = findViewById(R.id.app_title);

        // Récupère la taille du texte pour créer le dégradé
        TextPaint paint = textView.getPaint();
        float width = paint.measureText(textView.getText().toString());

        // Crée le dégradé
        Shader shader = new LinearGradient(
                0, 0, width, 0,
                new int[]{
                        ContextCompat.getColor(this, R.color.degrade_bleu),
                        ContextCompat.getColor(this, R.color.degrade_violet)
                },
                null,
                Shader.TileMode.CLAMP);

        paint.setShader(shader); // Applique le dégradé au texte
        textView.invalidate(); // Redessine le texte pour afficher l’effet
    }
}
