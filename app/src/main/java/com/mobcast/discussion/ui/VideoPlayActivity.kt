package com.mobcast.discussion.ui

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Util
import com.mobcast.R
import com.mobcast.databinding.ActivityVideoPlayBinding
import java.util.ArrayList

class VideoPlayActivity : AppCompatActivity() {
    private val binding:ActivityVideoPlayBinding by lazy {
        ActivityVideoPlayBinding.inflate(layoutInflater)
    }
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition = 0L

    private var player:SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.closeVideo.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializePlayer() {
        try {

            player = SimpleExoPlayer.Builder(this).build().also {
                binding.videoPlayer.player = it
                it.setMediaItem(MediaItem.fromUri(intent.getStringExtra("videoURL")!!))
                it.playWhenReady = playWhenReady
                it.seekTo(currentWindow, playbackPosition)
                it.prepare()
            }
        } catch (e:Exception) {
            Toast.makeText(this, getString(R.string.unableToPlayVideo), Toast.LENGTH_SHORT).show()
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }
    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        binding.videoPlayer.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            release()
        }
        player = null
    }
}