package edu.iut.projetfilrouge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

public class PlayerService extends Service {

    private static ExoPlayer player; // Le lecteur audio
    private static String currentUrl = null;
    public static boolean replayEnabled = false; // Si la lecture en boucle est activée
    private static MainActivity mainActivityInstance; // Référence vers l'activité principale
    private static boolean listenerAjoute = false; // Pour ne pas ajouter plusieurs fois le listener (car la musique se relançait en boucle)
    public static boolean randomEnabled = false; // Si la lecture aléatoire est activée

    // Méthode pour lancer la lecture d'une musique
    public static void play(Context context, String url) {
        if (player == null) {
            player = new ExoPlayer.Builder(context).build(); // Initialise le lecteur
        }

        // Si l'URL a changé, on prépare la nouvelle musique
        if (!url.equals(currentUrl)) {
            currentUrl = url;
            MediaItem mediaItem = MediaItem.fromUri(url); // Crée un objet média
            player.setMediaItem(mediaItem); // Définit le média à lire
            player.prepare(); // Prépare le lecteur

            // Ajoute un listener pour gérer la fin de lecture
            if (!listenerAjoute) {
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        // Quand la musique est terminée
                        if (playbackState == Player.STATE_ENDED) {
                            if (replayEnabled) {
                                player.seekTo(0); // Recommence depuis le début
                                player.play();
                            } else if (mainActivityInstance != null && !mainActivityInstance.isFinishing()) {
                                // Si l'activité est visible, on demande à jouer la prochaine musique
                                mainActivityInstance.runOnUiThread(() -> {
                                    if (randomEnabled) {
                                        mainActivityInstance.playRandomMusic(); // Aléatoire
                                    } else {
                                        mainActivityInstance.playNextMusic(); // Lecture normale
                                    }
                                });
                            } else {
                                // Sinon, lecture suivante automatique
                                int nextIndex;
                                if (randomEnabled) {
                                    nextIndex = new java.util.Random().nextInt(player.getMediaItemCount());
                                } else {
                                    nextIndex = player.getCurrentMediaItemIndex() + 1;
                                }

                                if (nextIndex < player.getMediaItemCount()) {
                                    player.seekTo(nextIndex, 0);
                                    player.play();
                                } else {
                                    currentUrl = null; // Plus rien à lire
                                }
                            }
                        }
                    }
                });

                listenerAjoute = true; // Marque que le listener a été ajouté
            }
        }

        player.play(); // Lance la lecture
    }

    public static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    // Retourne vrai si une musique est en train de jouer
    public static boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    // Permet d’accéder à l’instance du lecteur
    public static ExoPlayer getPlayerInstance() {
        return player;
    }

    // Permet de transmettre l’instance de MainActivity
    public static void setMainActivityInstance(MainActivity instance) {
        mainActivityInstance = instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
