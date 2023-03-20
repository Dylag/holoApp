package com.example.holoappkotlin

import VideoThread
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore.Video
import android.util.Log
import android.view.Surface
import android.view.TextureView.SurfaceTextureListener
import android.view.View
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.holoappkotlin.databinding.ActivityVideoBinding
import com.warnyul.android.widget.FastVideoView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class VideoActivity : AppCompatActivity() {
    lateinit var binding:ActivityVideoBinding

    var videosArePlaying = false

    var TEST_VIDEO_URI = "https://media.w3.org/2010/05/sintel/trailer.mp4"

    //var videoThreads  = mutableListOf<VideoThread>()
    private var mediaPlayers = mutableListOf<MediaPlayer>()
    private var videoViews = mutableListOf<FastVideoView>()
    private var videoStarterThreads = mutableListOf<Thread>()

    private var videoLauncher = Thread{launchVideos()}

    private var currentUris = mutableListOf<Uri>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val uri = intent.getStringExtra("uri")

        currentUris = copyFile(Uri.parse(intent.getStringExtra("uri")))


        videoViews.add(binding.videoViewTop)
        videoViews.add(binding.videoViewRight)
        videoViews.add(binding.videoViewBottom)
        videoViews.add(binding.videoViewLeft)

        for(i in 0 until 4)
        {
            mediaPlayers.add(MediaPlayer())
            mediaPlayers[i].setDataSource(baseContext, currentUris[i])
            mediaPlayers[i].isLooping = true
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

        //WOW SHIT (aka sqwore hahaha)
        Toast.makeText(this, R.string.activity_video_videoIsPreparing,Toast.LENGTH_SHORT).show()
        mediaPlayers.forEach { it.prepare() }
        Toast.makeText(this, R.string.activity_video_videoIsReady,Toast.LENGTH_SHORT).show()
        mediaPlayers.forEach { it.start() }
        mediaPlayers.forEach { it.pause() }



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



    fun chooseNewVideoCard_onClick(view: View)
    {
        val videoIntent = Intent()
            .setType("video/*")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
        startActivityForResult(videoIntent, RequestCodes.GALLERY_VIDEO.ordinal)
    }


    private fun copyFile(uri:Uri):MutableList<Uri>
    {
        val uris = mutableListOf<Uri>()
        for (i in 0 until 4)
        {
            val inputStream = this.contentResolver.openInputStream(uri)

            val file = File(this.filesDir, SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) + "_copy$i.mp4")
            uris.add(FileProvider.getUriForFile(this, "${this.packageName}.provider", file))
            val outputStream  = this.contentResolver.openOutputStream(uris[i])


            Log.e("maid id:$i", uri.toString())
            inputStream?.use {input ->
                outputStream?.use {output ->
                    input.copyTo(output)
                }
            }
        }

        return uris
    }

    private fun setUri(uri:Uri)
    {


        videosArePlaying = false
        mediaPlayers.forEach { it.reset() }
        currentUris = copyFile(uri)

        for(i in 0 until 4) {
            videoStarterThreads[i].start()
            Log.d("maid","id $i")
            mediaPlayers[i].setDataSource(baseContext, currentUris[i])
        }



        Toast.makeText(this, R.string.activity_video_videoIsPreparing,Toast.LENGTH_SHORT).show()
        mediaPlayers.forEach { it.prepare() }
        Toast.makeText(this, R.string.activity_video_videoIsReady,Toast.LENGTH_SHORT).show()
        mediaPlayers.forEach { it.start() }
        mediaPlayers.forEach { it.pause() }

        videoLauncher.start()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if (data!!.data!=null)
                setUri(data.data!!)
    }

}