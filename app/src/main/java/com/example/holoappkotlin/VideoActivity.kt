package com.example.holoappkotlin

import android.media.MediaPlayer.OnPreparedListener
import android.net.Uri
import android.opengl.Visibility
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.holoappkotlin.databinding.ActivityVideoBinding


class VideoActivity : AppCompatActivity() {

    val videViewsHadPrepared = mutableListOf<Boolean>(false,false,false,false)
    var globalMediaController: MediaController = MediaController(this)


    private lateinit var binding:ActivityVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Toast.makeText(this,"123",Toast.LENGTH_LONG).show()


        binding.videoViewTop.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        binding.videoViewRight.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        binding.videoViewBottom.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        binding.videoViewLeft.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))


        binding.videoViewTop.start()
//        videoViewRight.start()
//        videoViewBottom.start()
//        videoViewLeft.start()

    }
}