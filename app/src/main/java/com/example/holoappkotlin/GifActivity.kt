package com.example.holoappkotlin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.SeekBar
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.activity_photo.*

class GifActivity : AppCompatActivity() {
    var gifs : ArrayList<String> = ArrayList()

    ///WARNING!!! ADD COUROUTINES AND TRY ANOTHER LIB FOR GIF VIEWй



    var currentGifId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        gifGrid.layoutParams.height = dm.widthPixels

        gifs = intent.getStringArrayListExtra("uris")!!

        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(gifViewTop)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(gifViewRight)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(gifViewBottom)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(gifViewLeft)

        activity_gif_seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentScale = seekBar?.progress!!.toFloat()/100+1

                gifViewTop.scaleX = currentScale
                gifViewTop.scaleY = currentScale

                gifViewRight.scaleX = currentScale
                gifViewRight.scaleY = currentScale

                gifViewBottom.scaleX = currentScale
                gifViewBottom.scaleY = currentScale

                gifViewLeft.scaleX = currentScale
                gifViewLeft.scaleY = currentScale

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })


        if (gifs.count() == 1)
        {
            nextGifCard.isClickable = false
            prevGifCard.isClickable = false
        }

    }

    fun switchToNextGif(view: View)
    {
        currentGifId++
        if (currentGifId == gifs.count())
            currentGifId = 0
        setGifUri()
    }

    fun switchToPreviousGif(view: View)
    {
        currentGifId--
        if (currentGifId == -1)
            currentGifId +=gifs.count()
        setGifUri()
    }

    fun setGifUri()
    {
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(gifViewTop)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(gifViewRight)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(gifViewBottom)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(gifViewLeft)
    }


}