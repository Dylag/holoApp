package com.example.holoappkotlin

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import com.ortiz.touchview.TouchImageView
import com.example.holoappkotlin.databinding.ActivityFsideBinding

class FsideActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFsideBinding

    var darkerForegroundColor: Drawable? = null

//    var photoSets = ArrayList<MutableMap<Int, Uri?>>(1)
   // var photoSets = ArrayList<MutableMap<Int, Uri?>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fside)

        binding = ActivityFsideBinding.inflate(layoutInflater)
        setContentView(binding.root)


        darkerForegroundColor = binding.root.foreground
        binding.controlGrid.foreground = null

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        binding.pictureGrid.layoutParams.height = dm.widthPixels



        binding.imageViewTop.maxZoom = 4f
        binding.imageViewRight.maxZoom = 4f
        binding.imageViewBottom.maxZoom = 4f
        binding.imageViewLeft.maxZoom = 4f


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


    fun addForwardCard_onClick(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 0)
    }
    fun addLeftCard_onClick(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 1)
    }
    fun addBackwardCard_onClick(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 2)
    }
    fun addRightCard_onClick(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 3)
    }

    override fun onActivityResult(imageViewPos: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(imageViewPos, resultCode, data)

        if (resultCode == RESULT_OK)
        {
            when(imageViewPos)
            {
                0-> {binding.imageViewTop.setImageURI(data!!.data)
                    Log.d("personal maid", "1")
                    binding.addForwardCard.visibility = View.GONE
                    Log.d("personal maid", "2")
                    binding.imageViewTop.visibility = View.VISIBLE
                    Log.d("personal maid", "3")
//                    photoSets[photoSets.count()-1][0] = data.data
                    Log.d("personal maid", "4")
                    binding.chooseNewImageCard.isClickable = true
                    Log.d("personal maid", "5")}

                1-> {binding.imageViewRight.setImageURI(data!!.data)
                    binding.addLeftCard.visibility =View.GONE
                    binding.imageViewRight.visibility = View.VISIBLE
                    //photoSets[photoSets.count()-1][1] = data.data
                    binding. chooseNewImageCard.isClickable = true}

                2-> {binding.imageViewBottom.setImageURI(data!!.data)
                    binding.addBackwardCard.visibility = View.GONE
                    binding.imageViewBottom.visibility = View.VISIBLE
                    //photoSets[photoSets.count()-1][2] = data.data
                    binding.chooseNewImageCard.isClickable = true}

                3-> {binding.imageViewLeft.setImageURI(data!!.data)
                    binding.addRightCard.visibility = View.GONE
                    binding.imageViewLeft.visibility = View.VISIBLE
                   // photoSets[photoSets.count()-1][3] = data.data
                    binding.chooseNewImageCard.isClickable = true}
            }
        }
    }
    fun correctImageCard_onClick(view:View)
    {
        cards_turnIsClickable()
        binding.correctImageTIV.visibility = View.VISIBLE
        binding.controlGrid.foreground = darkerForegroundColor

    }

    override fun onBackPressed() {
        if (binding.correctImageTIV.visibility == View.INVISIBLE)
            super.onBackPressed()
        if(binding.correctImageTIV.visibility == View.VISIBLE) {
            cards_turnIsClickable()
            binding.correctImageTIV.visibility = View.INVISIBLE
            binding.controlGrid.foreground = null
        }
    }


    fun cards_turnIsClickable()
    {

        binding.nextImageCard.isClickable = false
        binding.prevImageCard.isClickable = false

        binding.correctImageCard.isClickable = !binding.correctImageCard.isClickable
        binding.chooseNewImageCard.isClickable = binding.correctImageCard.isClickable
    }

    fun chooseNewPhoto(view:View)
    {
        binding.addForwardCard.visibility = View.VISIBLE
        binding.addRightCard.visibility = View.VISIBLE
        binding.addBackwardCard.visibility = View.VISIBLE
        binding.addLeftCard.visibility = View.VISIBLE

        binding.imageViewTop.visibility = View.INVISIBLE
        binding.imageViewRight.visibility = View.INVISIBLE
        binding.imageViewBottom.visibility = View.INVISIBLE
        binding.imageViewLeft.visibility = View.INVISIBLE

        //im so sorry for this >-<       :''(            I'll fix it ASAP
       // photoSets.add({} as MutableMap<Int, Uri?>)



        //yooooooooo its 05.03.23
    }
}