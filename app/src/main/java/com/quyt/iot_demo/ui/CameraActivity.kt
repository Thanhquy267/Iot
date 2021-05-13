package com.quyt.iot_demo.ui

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.quyt.iot_demo.R
import com.quyt.iot_demo.databinding.ActivityCameraBinding
import org.videolan.libvlc.IVLCVout
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.lang.ref.WeakReference


class CameraActivity : AppCompatActivity(), IVLCVout.Callback {
    lateinit var mLayoutBinding: ActivityCameraBinding
    val TAG = "MainActivity"
    private var mFilePath: String? = null
    private var libvlc: LibVLC? = null
    private lateinit var mMediaPlayer: MediaPlayer
    private var mVideoWidth = 0
    private var mVideoHeight = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        mLayoutBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        //
        mFilePath = "rtsp://admin:IPNKXL@192.168.1.195:554/video.mp4";
        Log.d(TAG, "Playing: $mFilePath")
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setSize(mVideoWidth, mVideoHeight)
    }

    override fun onResume() {
        super.onResume()
        createPlayer(mFilePath!!)
    }

    override fun onPause() {
        super.onPause()
//        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
//        releasePlayer()
    }

    /**
     * Used to set size for SurfaceView
     *
     * @param width
     * @param height
     */
    private fun setSize(width: Int, height: Int) {
        mVideoWidth = width
        mVideoHeight = height
        if (mVideoWidth * mVideoHeight <= 1) return
        if (mLayoutBinding.surface.holder == null) return
        var w = window.decorView.width
        var h = window.decorView.height
        val isPortrait =
            resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        if (w > h && isPortrait || w < h && !isPortrait) {
            val i = w
            w = h
            h = i
        }
        val videoAR = mVideoWidth.toFloat() / mVideoHeight.toFloat()
        val screenAR = w.toFloat() / h.toFloat()
        if (screenAR < videoAR) h = (w / videoAR).toInt() else w = (h * videoAR).toInt()
        mLayoutBinding.surface.holder?.setFixedSize(mVideoWidth, mVideoHeight)
        val lp: ViewGroup.LayoutParams = mLayoutBinding.surface.layoutParams
        lp.width = w
        lp.height = h
        mLayoutBinding.surface.layoutParams = lp
        mLayoutBinding.surface.invalidate()
    }

    /**
     * Creates MediaPlayer and plays video
     *
     * @param media
     */
    private fun createPlayer(media: String) {
//            releasePlayer()
        try {
            if (media.length > 0) {
                val toast = Toast.makeText(this, media, Toast.LENGTH_LONG)
                toast.setGravity(
                    Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0,
                    0
                )
                toast.show()
            }
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            val options = ArrayList<String>()
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles")
            options.add("--audio-time-stretch") // time stretching
            options.add("-vvv") // verbosity
            libvlc = LibVLC(this, options)
            mLayoutBinding.surface.holder!!.setKeepScreenOn(true)
            // Creating media player
            mMediaPlayer = MediaPlayer(libvlc)
            mMediaPlayer.setEventListener(mPlayerListener)
            // Seting up video output
            val vout = mMediaPlayer.vlcVout
            vout.setVideoView(mLayoutBinding.surface)
            //vout.setSubtitlesView(mLayoutBinding.surfaceSubtitles);
            vout.addCallback(this)
            vout.attachViews()
            val m = Media(libvlc, Uri.parse(media))
            mMediaPlayer.media = m
            mMediaPlayer.play()
        } catch (e: Exception) {
            Toast.makeText(this, "Error in creating player!", Toast.LENGTH_LONG).show()
        }
    }

//    private fun releasePlayer() {
//        if (libvlc == null) return
//        mMediaPlayer.release()
//        val vout = mMediaPlayer!!.vlcVout
//        vout.removeCallback(this)
//        vout.detachViews()
//        libvlc?.release()
//        libvlc = null
//        mVideoWidth = 0
//        mVideoHeight = 0
//    }

    /**
     * Registering callbacks
     */
    private val mPlayerListener: MediaPlayer.EventListener =
        MyPlayerListener(this)

    override fun onNewLayout(
        vout: IVLCVout?,
        width: Int,
        height: Int,
        visibleWidth: Int,
        visibleHeight: Int,
        sarNum: Int,
        sarDen: Int
    ) {
        if (width * height == 0) return
        // store video size
        mVideoWidth = width
        mVideoHeight = height
        setSize(mVideoWidth, mVideoHeight)
    }

    override fun onSurfacesCreated(vout: IVLCVout?) {}
    override fun onSurfacesDestroyed(vout: IVLCVout?) {}
    override fun onHardwareAccelerationError(vlcVout: IVLCVout?) {
        Log.e(TAG, "Error with hardware acceleration")
//        releasePlayer()
        Toast.makeText(this, "Error with hardware acceleration", Toast.LENGTH_LONG).show()
    }

    private class MyPlayerListener(owner: CameraActivity?) :
        MediaPlayer.EventListener {
        private val mOwner: WeakReference<CameraActivity> = WeakReference<CameraActivity>(owner)
        override fun onEvent(event: MediaPlayer.Event) {
            val player: CameraActivity = mOwner.get()!!
            when (event.type) {
                MediaPlayer.Event.EndReached -> {
                    Log.d("CameraActivity", "MediaPlayerEndReached")
//                    player.releasePlayer()
                }
                MediaPlayer.Event.Playing, MediaPlayer.Event.Paused, MediaPlayer.Event.Stopped -> {
                }
                else -> {
                }
            }
        }

    }
}