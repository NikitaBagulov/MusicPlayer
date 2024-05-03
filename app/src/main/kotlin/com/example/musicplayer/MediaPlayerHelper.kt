package com.example.musicplayer

import android.media.MediaPlayer

object MediaPlayerHelper {
    private var mediaPlayer: MediaPlayer? = null

    fun getMediaPlayer(): MediaPlayer {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        return mediaPlayer!!
    }

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}