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

    private static ExoPlayer player;
    private static String currentUrl = null;
    public static boolean replayEnabled = false;
    private static MainActivity mainActivityInstance;
    private static boolean listenerAjoute = false;
    public static boolean randomEnabled = false;




    public static void play(Context context, String url) {
        if (player == null) {
            player = new ExoPlayer.Builder(context).build();
        }

        if (!url.equals(currentUrl)) {
            currentUrl = url;
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();

            if (!listenerAjoute) {
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_ENDED) {
                            if (mainActivityInstance != null) {
                                mainActivityInstance.runOnUiThread(() -> {
                                    if (replayEnabled) {
                                        player.seekTo(0);
                                        player.play();
                                    } else if (randomEnabled) {
                                        mainActivityInstance.playRandomMusic();
                                    } else {
                                        mainActivityInstance.playNextMusic();
                                    }
                                });
                            }

                        }
                    }
                });
                listenerAjoute = true;
            }


        }

        player.play();
    }

    public static void pause() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    public static boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public static ExoPlayer getPlayerInstance() {
        return player;
    }

    public static void setMainActivityInstance(MainActivity instance) {
        mainActivityInstance = instance;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

