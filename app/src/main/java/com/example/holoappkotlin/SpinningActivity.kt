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
            for(i in 0 until 4)
                ids[i] = (ids[i] + 1) % imagesSources.count()

            binding.imageViewTop.setImageURI(imagesSources[ids[0]]); binding.imageViewTop.setZoom(binding.correctImageTIV)
            binding.imageViewRight.setImageURI(imagesSources[ids[1]]); binding.imageViewRight.setZoom(binding.correctImageTIV)
            binding.imageViewBottom.setImageURI(imagesSources[ids[2]]); binding.imageViewBottom.setZoom(binding.correctImageTIV)
            binding.imageViewLeft.setImageURI(imagesSources[ids[3]]); binding.imageViewLeft.setZoom(binding.correctImageTIV)

            Log.d("maid", "bottom ${binding.imageViewBottom.scrollPosition} TIV ${binding.correctImageTIV.scrollPosition}")
        }

        fun getIds()
        {
            when (imagesSources.count())
            {
                4->{ids = arrayListOf(0,1,2,3)}
                8->{ids = arrayListOf(0,2,4,6)}
                16->{ids = arrayListOf(0,4,8,12)}
            }
        }

        override fun onFinish() {}
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpinningBinding.inflate(layoutInflater)
        setContentView(binding.root)

        darkerForegroundColor = binding.controlGrid.foreground
        binding.controlGrid.foreground = null
        binding.delaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                val fps = (binding.delaySeekBar.progress)/20f
                binding.debugTV.text = fps.toString()

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
        binding.correctImageTIV.visibility = View.VISIBLE
        binding.controlGrid.foreground = darkerForegroundColor
    }

    fun turn_controls_isClicable()
    {
        binding.chooseNewImageCard.isClickable = !binding.chooseNewImageCard.isClickable
        binding.correctImageCard.isClickable = binding.chooseNewImageCard.isClickable

        if(binding.delaySeekBar.visibility == View.GONE)
            binding.delaySeekBar.visibility = View.VISIBLE
        else
            binding.delaySeekBar.visibility = View.GONE

    }

    override fun onBackPressed() {
        if (binding.correctImageTIV.visibility == View.INVISIBLE)
            super.onBackPressed()
        if(binding.correctImageTIV.visibility == View.VISIBLE)
        {
            turn_controls_isClicable()
            binding.correctImageTIV.visibility = View.INVISIBLE
            binding.correctImageTIV.foreground = null
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
                    binding.addForwardCard.visibility = View.GONE
                    binding.addLeftCard.visibility = View.GONE
                    binding.addRightCard.visibility = View.GONE
                    binding.addBackwardCard.visibility = View.GONE

                    binding.imageViewTop.visibility = View.VISIBLE
                    binding.imageViewLeft.visibility = View.VISIBLE
                    binding.imageViewBottom.visibility = View.VISIBLE
                    binding.imageViewRight.visibility = View.VISIBLE



                    timer.cancel()

                    timer.delay = 1000f/fps
                    timer.time = (timer.delay+1).toInt()
                    binding.debugTV.text = timer.delay.toString()
                    for (i in 0 until data.clipData!!.itemCount)
                        imagesSources.add(data.clipData!!.getItemAt(i).uri)

                    timer.getIds()
                    timer.start()
                }
            }
            else
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

}