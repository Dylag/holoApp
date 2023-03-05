package com.example.holoappkotlin

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.SeekBar
import com.bumptech.glide.Glide
import com.example.holoappkotlin.databinding.ActivityGifBinding


class GifActivity : AppCompatActivity() {
    var gifs : ArrayList<String> = ArrayList()

    ///WARNING!!! ADD COUROUTINES AND TRY ANOTHER LIB FOR GIF VIEW


    private lateinit var binding:ActivityGifBinding


    var currentGifId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGifBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        binding.gifGrid.layoutParams.height = dm.widthPixels

        gifs = intent.getStringArrayListExtra("uris")!!

        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(binding.gifViewTop)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(binding.gifViewRight)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(binding.gifViewBottom)
        Glide.with(this)
            .load(Uri.parse(gifs[0]))
            .into(binding.gifViewLeft)

        binding.scaleSeekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val currentScale = seekBar?.progress!!.toFloat()/100+1

                binding.gifViewTop.scaleX = currentScale
                binding.gifViewTop.scaleY = currentScale

                binding.gifViewRight.scaleX = currentScale
                binding.gifViewRight.scaleY = currentScale

                binding.gifViewBottom.scaleX = currentScale
                binding.gifViewBottom.scaleY = currentScale

                binding.gifViewLeft.scaleX = currentScale
                binding.gifViewLeft.scaleY = currentScale

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
        })


        if (gifs.count() == 1)
        {
            binding.nextGifCard.isClickable = false
            binding.prevGifCard.isClickable = false
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
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(binding.gifViewTop)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(binding.gifViewRight)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(binding.gifViewBottom)
        Glide.with(this).load(Uri.parse(gifs[currentGifId])).into(binding.gifViewLeft)
    }


}