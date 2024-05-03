package com.example.musicplayer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var progressBar: ProgressBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var playPauseButton: Button

    private val handler = Handler()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        progressBar = findViewById(R.id.progressBar)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
        totalTimeTextView = findViewById(R.id.totalTimeTextView)
        playPauseButton = findViewById(R.id.playPauseButton)

        val currentPosition = intent.getIntExtra("currentPosition", 0)
        Log.e("LONG", formatTime(currentPosition))
        mediaPlayer = MediaPlayerHelper.getMediaPlayer()

        mediaPlayer.setOnPreparedListener {
            mediaPlayer.seekTo(currentPosition)
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
                playPauseButton.text = "Pause"
                progressBar.max = mediaPlayer.duration
                progressBar.progress = currentPosition
                totalTimeTextView.text = formatTime(mediaPlayer.duration)
                currentTimeTextView.text = formatTime(currentPosition)
                updateProgressBar()
            }
        }

        mediaPlayer.setOnCompletionListener {}

        playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playPauseButton.text = "Play"
            } else {
                mediaPlayer.start()
                playPauseButton.text = "Pause"
            }
        }
    }

    private fun returnToMainActivity() {
        val intent = Intent()
        intent.putExtra("isPlaying", mediaPlayer.isPlaying)
        intent.putExtra("currentPosition", mediaPlayer.currentPosition)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onStart() {
        super.onStart()
        updateProgressBar()
    }

    private fun updateProgressBar() {
        handler.post(object : Runnable {
            override fun run() {
                try {
                    if (mediaPlayer.isPlaying) {
                        val currentPosition = mediaPlayer.currentPosition
                        val totalTime = mediaPlayer.duration
                        progressBar.progress = currentPosition
                        currentTimeTextView.text = formatTime(currentPosition)
                        totalTimeTextView.text = formatTime(totalTime)
                    }
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
                if (!mediaPlayer.isPlaying && mediaPlayer.currentPosition == mediaPlayer.duration) {
                    handler.removeCallbacks(this)
                } else {
                    handler.postDelayed(this, 1000)
                }
            }
        })
    }

    private fun formatTime(ms: Int): String {
        val seconds = ms / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "$minutes:${String.format("%02d", remainingSeconds)}"
    }

    override fun onDestroy() {
        super.onDestroy()
        returnToMainActivity()
    }
}