package edu.iut.projetfilrouge;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_musique);

        // Référence à l'image de fond
        ImageView background = findViewById(R.id.backgroundImage);

        // Charge l'image depuis drawable
        Bitmap original = ((BitmapDrawable) getResources().getDrawable(R.drawable.test)).getBitmap();

        // Applique le flou à l’image
        Bitmap blurred = blurBitmap(original, 20f);

        // Affiche l’image floutée en fond
        background.setImageBitmap(blurred);
    }

    // Fonction utilitaire de flou (RenderScript)
    public Bitmap blurBitmap(Bitmap input, float radius) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(this);
        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);

        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(Math.min(25f, Math.max(0.1f, radius))); // entre 0.1 et 25
        script.setInput(inputAlloc);
        script.forEach(outputAlloc);

        outputAlloc.copyTo(output);
        rs.destroy();

        return output;
    }
}
