package com.example.holoappkotlin

import VideoThread
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Video
import android.util.Log
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import com.example.holoappkotlin.databinding.ActivityVideoBinding
import com.warnyul.android.widget.FastVideoView

class VideoActivity : AppCompatActivity() {
    lateinit var binding:ActivityVideoBinding

    private var uris = arrayListOf<String>()

    var videosArePlaying = false

    var TEST_VIDEO_URI = "https://media.w3.org/2010/05/sintel/trailer.mp4"

    //var videoThreads  = mutableListOf<VideoThread>()
    private var mediaPlayers = mutableListOf<MediaPlayer>()
    private var videoViews = mutableListOf<FastVideoView>()
    private var videoStarterThreads = mutableListOf<Thread>()

    private var videoLauncher = Thread{launchVideos()}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val uri = intent.getStringExtra("uri")
        uris = intent.getStringArrayListExtra("uris")!!
        Log.d("maid",uris.toString())


        videoViews.add(binding.videoViewTop)
        videoViews.add(binding.videoViewRight)
        videoViews.add(binding.videoViewBottom)
        videoViews.add(binding.videoViewLeft)

        for(i in 0 until 4)
        {
            mediaPlayers.add(MediaPlayer())
            mediaPlayers[i].setDataSource(baseContext, Uri.parse(uris[i]))
            videoStarterThreads.add(Thread{startMediaPlayers(i)})
            videoStarterThreads[i].start()
        }

        for(i in 0 until 4)
        {
            videoViews[i].surfaceTextureListener = object:SurfaceTextureListener
            {
                override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture, width: Int, height: Int) {
                    Log.d("maid", "id $i available")
                    mediaPlayers[i].setSurface(Surface(surfaceTexture))
                }

                override fun onSurfaceTextureSizeChanged(p0: SurfaceTexture, p1: Int, p2: Int) {}

                override fun onSurfaceTextureDestroyed(p0: SurfaceTexture): Boolean {
                    return true
                }
                override fun onSurfaceTextureUpdated(p0: SurfaceTexture) {
                    Log.d("maid","id $i frame")

                }
            }
        }

        mediaPlayers.forEach {it.prepare()}
        mediaPlayers.forEach{it.start()}
        mediaPlayers.forEach{it.pause()}

        videoLauncher.start()

       // videosArePlaying = true

        Log.d("maid","onCreate finished")

    }

    private fun startMediaPlayers(id:Int)
    {
        while(!videosArePlaying){
            if(id == 4 || id == 3)
                Log.d("maid","id $id cycle")
        }
        mediaPlayers[id].start()
        Log.d("maid","media player $id started" )
    }

    private fun launchVideos()
    {
        Log.d("maid",mediaPlayers.toString())

        //videoStarterThreads.forEach {it.start()}

        Log.d("maid","starters started, beginning sleep...")

        Thread.sleep(1000)

        Log.d("maid","slept")

        videosArePlaying = true
        Log.d("maid", "started")

    }
}