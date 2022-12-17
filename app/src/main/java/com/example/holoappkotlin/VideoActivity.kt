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
import kotlinx.android.synthetic.main.activity_video.*


class VideoActivity : AppCompatActivity() {

    val videViewsHadPrepared = mutableListOf<Boolean>(false,false,false,false)
    var globalMediaController: MediaController = MediaController(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        Toast.makeText(this,"123",Toast.LENGTH_LONG).show()


        videoViewTop.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        videoViewRight.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        videoViewBottom.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))
        videoViewLeft.setVideoURI(Uri.parse(intent.getStringArrayListExtra("uris")!![0]))


        videoViewTop.start()
//        videoViewRight.start()
//        videoViewBottom.start()
//        videoViewLeft.start()

    }
}