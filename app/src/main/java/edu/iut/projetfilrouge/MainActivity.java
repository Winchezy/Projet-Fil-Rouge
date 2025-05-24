package edu.iut.projetfilrouge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
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


public class MainActivity extends AppCompatActivity {

    private boolean etatLogoRandom = false;
    private boolean etatLogoReplay = false;

    private List<Musique> musiqueList = new ArrayList<>();
    private int currentIndex = 0;
    private boolean showingLyrics = false;
    private Handler seekBarHandler = new Handler();
    private Runnable seekBarRunnable;
    private boolean isPlaying = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecture_musique);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        /*ImageView background = findViewById(R.id.backgroundImage);
        Bitmap original = ((BitmapDrawable) getResources().getDrawable(R.drawable.test)).getBitmap();
        Bitmap blurred = blurBitmap(original, 20f);
        background.setImageBitmap(blurred);*/

        findViewById(R.id.random_logo).setOnClickListener(v -> changerCouleurBoutonRandom());
        findViewById(R.id.replay_logo).setOnClickListener(v -> changerCouleurBoutonReplay());

        findViewById(R.id.skip_previous_logo).setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                afficherMusique(musiqueList.get(currentIndex));
            }
        });

        findViewById(R.id.skip_next_logo).setOnClickListener(v -> {
            if (currentIndex < musiqueList.size() - 1) {
                currentIndex++;
                afficherMusique(musiqueList.get(currentIndex));
            }
        });

        findViewById(R.id.lyrics_logo).setOnClickListener(v -> toggleLyrics());

        findViewById(R.id.pause_circle_logo).setOnClickListener(v -> {
            ImageView pauseButton = findViewById(R.id.pause_circle_logo);
            ExoPlayer player = PlayerService.getPlayerInstance();

            if (player != null) {
                if (PlayerService.isPlaying()) {
                    PlayerService.pause();
                    pauseButton.setImageResource(R.drawable.play_circle);
                    isPlaying = false;
                } else {
                    player.play(); // on ne relance pas PlayerService.play(...) pour ne pas réinitialiser
                    pauseButton.setImageResource(R.drawable.pause_circle);
                    isPlaying = true;
                }
            }
        });


        new Thread(() -> {
            File fichier = CsvDownloader.download(this, "http://edu.info06.net/lyrics/lyrics.csv");
            if (fichier != null) {
                musiqueList = CsvParser.parseAll(fichier);
                if (!musiqueList.isEmpty()) {
                    currentIndex = 0;
                    runOnUiThread(() -> afficherMusique(musiqueList.get(currentIndex)));
                } else {
                    Log.d("DEBUG_CSV", "Liste vide");
                }
            } else {
                Log.d("DEBUG_CSV", "Téléchargement échoué");
            }
        }).start();
    }

    private void afficherMusique(Musique musique) {
        ((TextView) findViewById(R.id.title)).setText(musique.getTitre());
        ((TextView) findViewById(R.id.album)).setText(musique.getAlbum());
        ((TextView) findViewById(R.id.author)).setText(musique.getArtist());
        ((TextView) findViewById(R.id.date)).setText(musique.getDate());

        String imageUrl = "http://edu.info06.net/lyrics/images/" + musique.getCover();
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                        ((ImageView) findViewById(R.id.music_image)).setImageBitmap(resource);
                        Bitmap flou = blurBitmap(resource, 20f);
                        ((ImageView) findViewById(R.id.backgroundImage)).setImageBitmap(flou);
                    }

                    @Override
                    public void onLoadCleared(android.graphics.drawable.Drawable placeholder) {}
                });

        // Forcer retour à l’image si on était sur les lyrics
        if (showingLyrics) {
            findViewById(R.id.lyrics_scroll).setVisibility(View.GONE);
            findViewById(R.id.music_image).setVisibility(View.VISIBLE);
            showingLyrics = false;
        }

        // Lire musique via PlayerService
        String mp3Url = "http://edu.info06.net/lyrics/mp3/" + musique.getMp3();
        PlayerService.play(this, mp3Url);

        // SeekBar
        SeekBar seekBar = findViewById(R.id.music_seekbar);
        seekBar.setProgress(0);
        seekBar.setMax(0);

        seekBarRunnable = new Runnable() {
            @Override
            public void run() {
                ExoPlayer player = PlayerService.getPlayerInstance();
                if (player != null && player.isPlaying()) {
                    long position = player.getCurrentPosition();
                    long duration = player.getDuration();
                    seekBar.setMax((int) duration);
                    seekBar.setProgress((int) position);
                }
                seekBarHandler.postDelayed(this, 500);
            }
        };
        seekBarHandler.post(seekBarRunnable);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    ExoPlayer player = PlayerService.getPlayerInstance();
                    if (player != null) {
                        player.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Optionnel : tu peux mettre en pause ici si tu veux
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Optionnel : tu peux relancer la musique ici si besoin
            }
        });

    }


    public Bitmap blurBitmap(Bitmap input, float radius) {
        Bitmap output = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
        RenderScript rs = RenderScript.create(this);
        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(Math.min(25f, Math.max(0.1f, radius)));
        script.setInput(inputAlloc);
        script.forEach(outputAlloc);
        outputAlloc.copyTo(output);
        rs.destroy();
        return output;
    }

    public void changerCouleurBoutonRandom() {
        ImageView randomButton = findViewById(R.id.random_logo);
        randomButton.setImageResource(etatLogoRandom ? R.drawable.random : R.drawable.random_green);
        etatLogoRandom = !etatLogoRandom;
    }

    public void changerCouleurBoutonReplay() {
        ImageView replayButton = findViewById(R.id.replay_logo);
        replayButton.setImageResource(etatLogoReplay ? R.drawable.replay : R.drawable.replay_green);
        etatLogoReplay = !etatLogoReplay;
    }

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
                    line = line.trim();
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

    private void lireMusique(String mp3File) {
        String url = "http://edu.info06.net/lyrics/audio/" + mp3File;
        Intent intent = new Intent(this, PlayerService.class);
        intent.putExtra("URL", url);
        startService(intent);
    }


}
