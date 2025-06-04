package edu.iut.projetfilrouge;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.os.Handler;

import androidx.media3.common.Player;

import java.util.Random;
import java.util.Stack;

// Classe principale pour l'activité de lecture de musique
public class MainActivity extends AppCompatActivity {

    private boolean etatLogoRandom = false;
    private boolean etatLogoReplay = false;

    // Liste de toutes les musiques
    private static List<Musique> musiqueList = new ArrayList<>();

    // Index de la musique en cours
    private static int currentIndex = 0;

    private boolean showingLyrics = false;

    // Handler pour mettre à jour la SeekBar régulièrement
    private Handler seekBarHandler = new Handler();
    private Runnable seekBarRunnable;

    private boolean isPlaying = true;

    private boolean boucleActivee = false;

    private Stack<Integer> historique = new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PlayerService.setMainActivityInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_musique);

        // Active le mode plein écran et cache les barres système
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        findViewById(R.id.random_logo).setOnClickListener(v -> changerCouleurBoutonRandom());
        findViewById(R.id.replay_logo).setOnClickListener(v -> changerCouleurBoutonReplay());

        // Bouton précédent
        findViewById(R.id.skip_previous_logo).setOnClickListener(v -> {
            ExoPlayer player = PlayerService.getPlayerInstance(); // Exécuter des tâches en arrière-plan
            if (player != null) {
                long position = player.getCurrentPosition();

                // Si la chanson a commencé depuis plus de 3s, on revient au début
                if (position > 3000) {
                    player.seekTo(0);
                } else {
                    // Si mode aléatoire activé
                    if (PlayerService.randomEnabled) {
                        if (!historique.isEmpty()) {
                            currentIndex = historique.pop();
                            afficherMusique(musiqueList.get(currentIndex));
                        } else {
                            player.seekTo(0);
                        }
                    } else {
                        // Sinon on recule dans la liste
                        if (currentIndex > 0) {
                            currentIndex--;
                            afficherMusique(musiqueList.get(currentIndex));
                        } else {
                            player.seekTo(0);
                        }
                    }
                }
            }
        });

        // Bouton suivant
        findViewById(R.id.skip_next_logo).setOnClickListener(v -> {
            if (PlayerService.randomEnabled) {
                playRandomMusic();
            } else if (currentIndex < musiqueList.size() - 1) {
                historique.push(currentIndex);
                currentIndex++;
                afficherMusique(musiqueList.get(currentIndex));
            }
        });

        findViewById(R.id.lyrics_logo).setOnClickListener(v -> toggleLyrics());

        // Bouton pause/lecture
        findViewById(R.id.pause_circle_logo).setOnClickListener(v -> {
            ImageView pauseButton = findViewById(R.id.pause_circle_logo);
            ExoPlayer player = PlayerService.getPlayerInstance();

            if (player != null) {
                if (PlayerService.isPlaying()) {
                    PlayerService.pause();
                    pauseButton.setImageResource(R.drawable.play_circle);
                    isPlaying = false;
                } else {
                    player.play();
                    pauseButton.setImageResource(R.drawable.pause_circle);
                    isPlaying = true;
                }
            }
        });

        // Téléchargement et parsing du fichier CSV
        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv");
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier);

                // Cherche si un titre a été passé dans l'intent
                String titreRecherche = getIntent().getStringExtra("musique_titre");

                if (titreRecherche != null && !titreRecherche.isEmpty()) {
                    for (int i = 0; i < musiqueList.size(); i++) {
                        if (musiqueList.get(i).getTitre().equalsIgnoreCase(titreRecherche)) {
                            currentIndex = i;
                            break;
                        }
                    }
                }

                // Affiche la musique en cours dans l'interface
                runOnUiThread(() -> {
                    if (!musiqueList.isEmpty()) {
                        afficherMusique(musiqueList.get(currentIndex));
                    }
                });
            } else {
                Log.d("DEBUG_CSV", "Téléchargement échoué");
            }
        }).start();
    }

    // Affiche les infos d'une musique ET lance la lecture
    private void afficherMusique(Musique musique) {
        ImageView pauseButton = findViewById(R.id.pause_circle_logo);

        ((TextView) findViewById(R.id.title)).setText(musique.getTitre());
        ((TextView) findViewById(R.id.album)).setText(musique.getAlbum());
        ((TextView) findViewById(R.id.author)).setText(musique.getArtist());
        ((TextView) findViewById(R.id.date)).setText(musique.getDate());

        pauseButton.setImageResource(R.drawable.pause_circle);
        isPlaying = true;

        // Charge et affiche l'image de couverture
        String imageUrl = "http://edu.info06.net/lyrics/images/" + musique.getCover();
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        ImageView imageView = findViewById(R.id.music_image);
                        imageView.setImageBitmap(resource);

                        // Animation fondu sur l'image
                        Animation fadeInImage = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fondu);
                        imageView.startAnimation(fadeInImage);

                        // Flou de fond
                        Bitmap flou = blurBitmap(resource, 20f);
                        ImageView backgroundImage = findViewById(R.id.backgroundImage);
                        backgroundImage.setImageBitmap(flou);
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                });

        // Si les paroles sont visibles, on revient à l'image
        if (showingLyrics) {
            findViewById(R.id.lyrics_scroll).setVisibility(View.GONE);
            findViewById(R.id.music_image).setVisibility(View.VISIBLE);
            showingLyrics = false;
        }

        // Lecture de la musique
        String mp3Url = "http://edu.info06.net/lyrics/mp3/" + musique.getMp3();
        PlayerService.play(this, mp3Url);

        // Configuration de la SeekBar
        SeekBar seekBar = findViewById(R.id.music_seekbar);
        seekBar.setProgress(0);
        seekBar.setMax(0); // (mise à jour une fois la musique lancée)

        // Tâche répétitive pour mettre à jour la SeekBar
        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                ExoPlayer player = PlayerService.getPlayerInstance(); // Récupère l'instance du lecteur audio
                if (player != null && player.isPlaying()) {
                    long position = player.getCurrentPosition(); // Position actuelle de la musique
                    long duration = player.getDuration();
                    seekBar.setMax((int) duration); // Met à jour la durée maximale de la SeekBar
                    seekBar.setProgress((int) position); // Met à jour la position actuelle sur la SeekBar
                }
                seekBarHandler.postDelayed(this, 500); // Mise à jour toutes les 500 ms
            }
        };
        seekBarHandler.post(seekBarRunnable); // Lance la tâche de mise à jour

        // Interactions de l’utilisateur avec la SeekBar
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) { // Utilisateur change manuellement la position
                    ExoPlayer player = PlayerService.getPlayerInstance(); // Récupère le lecteur
                    if (player != null) {
                        player.seekTo(progress); // Avance la lecture à la nouvelle position
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }

    // Applique un flou sur une image bitmap (Horrible avec API 29 !) :'(
    public Bitmap blurBitmap(Bitmap input, float radius) {
        // Crée un Bitmap pour contenir l’image floutée (image qu'on va modifier)
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);

        // Initialise RenderScript (utilisé pour effectuer des traitements d’image rapides)
        RenderScript rs = RenderScript.create(this);

        // Prépare l’image d’entrée sous forme d’allocation pour RenderScript
        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);

        // Prépare l’image de sortie sous forme d’allocation
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);

        // Crée le script de flou
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // Rayon du flou
        script.setRadius(Math.min(25f, Math.max(0.1f, radius)));

        // Applique le flou sur l’image d’entrée
        script.setInput(inputAlloc);

        // Exécute le flou et envoie le résultat dans l’image de sortie
        script.forEach(outputAlloc);

        // Copie l’image floutée depuis l’allocation vers le Bitmap de sortie
        outputAlloc.copyTo(output);

        // Libère les ressources
        rs.destroy();

        return output;
    }


    // Change l'état du bouton random
    public void changerCouleurBoutonRandom() {
        ImageView randomButton = findViewById(R.id.random_logo);
        randomButton.setImageResource(etatLogoRandom ? R.drawable.random : R.drawable.random_green);
        etatLogoRandom = !etatLogoRandom;
        PlayerService.randomEnabled = etatLogoRandom;
    }

    // Change l'état du bouton replay et lance l'animation
    public void changerCouleurBoutonReplay() {
        ImageView replayButton = findViewById(R.id.replay_logo);

        Animator animator = AnimatorInflater.loadAnimator(this, R.animator.animation);
        animator.setTarget(replayButton);
        animator.start();

        if (etatLogoReplay) {
            replayButton.setImageResource(R.drawable.replay);
            boucleActivee = false;
        } else {
            replayButton.setImageResource(R.drawable.replay_green);
            boucleActivee = true;
        }

        etatLogoReplay = !etatLogoReplay;

        // Récupère le lecteur audio
        ExoPlayer player = PlayerService.getPlayerInstance();
        if (player != null) {
            // Active ou désactive le mode répétition sur une seule musique
            player.setRepeatMode(boucleActivee ? Player.REPEAT_MODE_ONE : Player.REPEAT_MODE_OFF);
        }
    }


    // Affiche ou cache les paroles de la chanson
    private void toggleLyrics() {
        ImageView musicImage = findViewById(R.id.music_image);
        TextView lyricsText = findViewById(R.id.lyrics_text);
        ScrollView lyricsScroll = findViewById(R.id.lyrics_scroll);

        if (showingLyrics) {
            lyricsScroll.setVisibility(View.GONE);
            musicImage.setVisibility(View.VISIBLE);
        } else {
            if (!musiqueList.isEmpty()) {
                String rawLyrics = musiqueList.get(currentIndex).getLyrics();
                String[] lines = rawLyrics.split(";");
                StringBuilder formatted = new StringBuilder();
                for (String line : lines) {
                    line = line.trim(); // trim pas obligatoire mais bon, on ne sait jamais ^^
                    if (!line.isEmpty()) {
                        formatted.append(Character.toUpperCase(line.charAt(0)))
                                .append(line.substring(1))
                                .append("\n");
                    }
                }
                lyricsText.setText(formatted.toString().trim());
            }
            lyricsScroll.setVisibility(View.VISIBLE);
            musicImage.setVisibility(View.GONE);
        }

        showingLyrics = !showingLyrics;
    }

    // On garde au cas où en a besoin finalement
    private void lireMusique(String mp3File) {
        String url = "http://edu.info06.net/lyrics/audio/" + mp3File;
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("URL", url);
        startService(intent);
    }

    // Lecture de la musique suivante dans la liste
    void playNextMusic() {
        if (currentIndex < musiqueList.size() - 1) {
            currentIndex++;
            Musique musique = musiqueList.get(currentIndex);
            afficherMusique(musique);
        }
    }

    // Lecture aléatoire d’une autre musique
    void playRandomMusic() {
        if (musiqueList.size() <= 1) return;

        int previousIndex = currentIndex;
        int newIndex;

        do {
            newIndex = new Random().nextInt(musiqueList.size());
        } while (newIndex == previousIndex);

        historique.push(currentIndex);
        currentIndex = newIndex;
        Musique musique = musiqueList.get(currentIndex);
        afficherMusique(musique);
    }

    // Nettoie la référence à l’activité lors de la pause
    @Override
    protected void onPause() {
        super.onPause();
        PlayerService.setMainActivityInstance(null);
    }

    // Réassigne l’activité quand elle reprend
    @Override
    protected void onResume() {
        super.onResume();
        PlayerService.setMainActivityInstance(this);
    }
}
