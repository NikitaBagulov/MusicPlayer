package com.example.musicplayer

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View



@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer

    companion object {
        const val PLAYER_ACTIVITY_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mediaPlayer = MediaPlayerHelper.getMediaPlayer()

        if (mediaPlayer.isPlaying || mediaPlayer.isLooping || (mediaPlayer.duration > 0)) {
            mediaPlayer.reset()
        }

        val afd = assets.openFd("song.mp3")
        mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        afd.close()

        mediaPlayer.setOnPreparedListener {
            val duration = mediaPlayer.duration
            val currentPosition = mediaPlayer.currentPosition
        }
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnCompletionListener {

        }
    }

    fun playPause(view: View) {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else if (::mediaPlayer.isInitialized) {
            mediaPlayer.start()
        }
    }

    fun goToPlayer(view: View) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra("isPlaying", mediaPlayer.isPlaying)
        intent.putExtra("currentPosition", mediaPlayer.currentPosition)
        startActivityForResult(intent, PLAYER_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAYER_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK && data != null) {
                val isPlaying = data.getBooleanExtra("isPlaying", false)
                val currentPosition = data.getIntExtra("currentPosition", 0)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MediaPlayerHelper.releaseMediaPlayer()
    }
}
