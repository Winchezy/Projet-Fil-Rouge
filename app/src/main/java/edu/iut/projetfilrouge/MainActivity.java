package edu.iut.projetfilrouge; // Assurez-vous que c'est le bon package

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar; // Importez la classe ProgressBar

public class MainActivity extends AppCompatActivity {

    private ProgressBar monProgressBar; // Déclarez la variable ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Assurez-vous que cela correspond au nom de votre fichier layout

        // Initialisez le ProgressBar APRÈS setContentView
        monProgressBar = findViewById(R.id.monProgressBar); // Utilisez le bon ID de votre XML

        // **Vérification cruciale pour éviter le NullPointerException**
        if (monProgressBar != null) {
            // Maintenant, vous pouvez utiliser votre ProgressBar en toute sécurité
            // Par exemple, pour le rendre visible :
            monProgressBar.setVisibility(View.VISIBLE);

            // Ou pour le masquer :
            // monProgressBar.setVisibility(View.GONE);
        } else {
            // Si monProgressBar est null, c'est que findViewById n'a pas trouvé la vue.
            // Cela peut être dû à un mauvais ID dans findViewById ou dans le fichier XML,
            // ou si la vue n'existe pas dans le layout activity_main.xml.
            // Loggez une erreur pour le débogage :
            android.util.Log.e("MainActivity", "ProgressBar avec l'ID R.id.monProgressBar non trouvé !");
            // Vous pourriez vouloir gérer ce cas d'une manière ou d'une autre (ex: ne pas exécuter le code qui en dépend)
        }

        // Si vous appelez setVisibility sur monProgressBar ailleurs dans votre code,
        // assurez-vous que cette initialisation dans onCreate() a bien eu lieu avant.
    }
}