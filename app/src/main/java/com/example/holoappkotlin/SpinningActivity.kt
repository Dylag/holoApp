package com.example.holoappkotlin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import com.example.holoappkotlin.databinding.ActivitySpinningBinding
import com.ortiz.touchview.TouchImageView
import kotlinx.coroutines.*

class SpinningActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpinningBinding

    var darkerForegroundColor: Drawable? = null
    var imagesSources:ArrayList<Uri> = arrayListOf()

    var fps = 1

    private val timer = object: CountDownTimer(86400000, 1) {
        var time = 0
        var delay = 0f
        private var ids:ArrayList<Int> = arrayListOf(0,1,2,3)

        override fun onTick(millisUntilFinished: Long) {
            time++
            if (time >= delay) {
                changeImages()
                time = 0
            }

        }

        private fun changeImages()
        {
            for(i in 0 until 4) {
                ids[i] = (ids[i] + 1) % imagesSources.count()
            }

            runBlocking {
                launch { imageViewTop.setImageURI(imagesSources[ids[0]]); imageViewTop.setZoom(correctImageTIV)}
                launch  {imageViewRight.setImageURI(imagesSources[ids[1]]); imageViewRight.setZoom(correctImageTIV)}
                launch  {imageViewBottom.setImageURI(imagesSources[ids[2]]); imageViewBottom.setZoom(correctImageTIV)}
                launch  {imageViewLeft.setImageURI(imagesSources[ids[3]]); imageViewLeft.setZoom(correctImageTIV)}
            }

            imageViewTop.setImageURI(imagesSources[ids[0]]); imageViewTop.setZoom(correctImageTIV)
            imageViewRight.setImageURI(imagesSources[ids[1]]); imageViewRight.setZoom(correctImageTIV)
            imageViewBottom.setImageURI(imagesSources[ids[2]]); imageViewBottom.setZoom(correctImageTIV)
            imageViewLeft.setImageURI(imagesSources[ids[3]]); imageViewLeft.setZoom(correctImageTIV)

            Log.d("maid", "bottom ${imageViewBottom.scrollPosition} TIV ${correctImageTIV.scrollPosition}")

        }

        fun setIds()
        {
            when (imagesSources.count())
            {
                4->{ids = arrayListOf(0,1,2,3)}
                8->{ids = arrayListOf(0,2,4,6)}
                16->{ids = arrayListOf(0,3,6,9)}
            }
        }

        override fun onFinish() {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpinningBinding.inflate(layoutInflater)
        val photoview = binding.root
        setContentView(photoview)

        darkerForegroundColor = activity_spin_controlGrid.foreground
        activity_spin_controlGrid.foreground = null
        fpsSetter_seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                var fps:Float = (fpsSetter_seekBar.progress)/20f
                activity_spin_debugTV.text = fps.toString()

                timer.delay = 1000f/fps
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        })

        binding.imageViewMain.setOnTouchImageViewListener( object: TouchImageView.OnTouchImageViewListener {
            override fun onMove() {
                binding.imageViewTop.setZoom(binding.imageViewMain)
                binding.imageViewRight.setZoom(binding.imageViewMain)
                binding.imageViewBottom.setZoom(binding.imageViewMain)
                binding.imageViewLeft.setZoom(binding.imageViewMain)

                binding.correctImageTIV.setZoom(binding.imageViewMain)
            }
        })

        binding.correctImageTIV.setOnTouchImageViewListener( object: TouchImageView.OnTouchImageViewListener {
            override fun onMove() {
                binding.imageViewTop.setZoom(binding.correctImageTIV)
                binding.imageViewRight.setZoom(binding.correctImageTIV)
                binding.imageViewBottom.setZoom(binding.correctImageTIV)
                binding.imageViewLeft.setZoom(binding.correctImageTIV)

                binding.imageViewMain.setZoom(binding.correctImageTIV)

            }
        })

    }

    fun chooseNewImageCard_onClick(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select files in order 0->360; 4, 8 or 12"), 0)
    }

    fun correctImageCard_onClick(view:View) {
        turn_controls_isClicable()
        correctImageTIV.visibility = View.VISIBLE
        activity_spin_controlGrid.foreground = darkerForegroundColor
    }

    fun turn_controls_isClicable()
    {
        chooseNewImageCard.isClickable = !chooseNewImageCard.isClickable
        correctImageCard.isClickable = chooseNewImageCard.isClickable

        if(fpsSetter_seekBar.visibility == View.GONE)
            fpsSetter_seekBar.visibility = View.VISIBLE
        else
            fpsSetter_seekBar.visibility = View.GONE

    }

    override fun onBackPressed() {
        if (correctImageTIV.visibility == View.INVISIBLE)
            super.onBackPressed()
        if(correctImageTIV.visibility == View.VISIBLE)
        {
            turn_controls_isClicable()
            correctImageTIV.visibility = View.INVISIBLE
            activity_spin_controlGrid.foreground = null
        }


    }

    override fun onActivityResult(imageViewPos: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(imageViewPos, resultCode, data)

        if (resultCode == RESULT_OK)
        {
            if (data?.clipData != null)
            {
                imagesSources = arrayListOf()

                if (data.clipData!!.itemCount !in arrayOf(4,8,12))
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                else
                {
                    addForward_card.visibility = View.GONE
                    addLeft_card.visibility = View.GONE
                    addRight_card.visibility = View.GONE
                    addBackward_card.visibility = View.GONE

                    imageViewTop.visibility = View.VISIBLE
                    imageViewLeft.visibility = View.VISIBLE
                    imageViewBottom.visibility = View.VISIBLE
                    imageViewRight.visibility = View.VISIBLE



                    timer.cancel()

                    timer.delay = 1000f/fps
                    timer.time = (timer.delay+1).toInt()
                    activity_spin_debugTV.text = timer.delay.toString()
                    for (i in 0 until data.clipData!!.itemCount)
                        imagesSources.add(data.clipData!!.getItemAt(i).uri)

                    timer.setIds()
                    timer.start()
                }
            }
            else
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

}