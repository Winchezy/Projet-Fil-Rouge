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

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, ClickableActivity.class);
            startActivity(intent);
            finish();
        }, 2000);

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
    }
}
