package com.example.holoappkotlin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager.OnActivityResultListener
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.holoappkotlin.databinding.ActivityPhotoBinding
import com.ortiz.touchview.TouchImageView
import kotlinx.android.synthetic.main.activity_fside.*
import java.io.File
import java.io.IOException
import com.example.holoappkotlin.databinding.ActivityFsideBinding
import kotlinx.android.synthetic.main.activity_fside.activity_photo_controlGrid
import kotlinx.android.synthetic.main.activity_fside.chooseNewPhotoCard
import kotlinx.android.synthetic.main.activity_fside.correctImageCard
import kotlinx.android.synthetic.main.activity_fside.correctImageTIV
import kotlinx.android.synthetic.main.activity_fside.imageViewBottom
import kotlinx.android.synthetic.main.activity_fside.imageViewLeft
import kotlinx.android.synthetic.main.activity_fside.imageViewRight
import kotlinx.android.synthetic.main.activity_fside.imageViewTop
import kotlinx.android.synthetic.main.activity_fside.nextImageCard
import kotlinx.android.synthetic.main.activity_fside.pictureGrid
import kotlinx.android.synthetic.main.activity_fside.prevImageCard
import kotlinx.android.synthetic.main.activity_photo.*
import java.net.URI

class fsideActivity : AppCompatActivity() {


    private lateinit var binding: ActivityFsideBinding

    var darkerForegroundColor: Drawable? = null

//    var photoSets = ArrayList<MutableMap<Int, Uri?>>(1)
   // var photoSets = ArrayList<MutableMap<Int, Uri?>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fside)

        binding = ActivityFsideBinding.inflate(layoutInflater)
        val fsideview = binding.root
        setContentView(fsideview)


        darkerForegroundColor = activity_photo_controlGrid.foreground
        activity_photo_controlGrid.foreground = null

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        pictureGrid.layoutParams.height = dm.widthPixels



        imageViewTop.maxZoom = 4f
        imageViewRight.maxZoom = 4f
        imageViewBottom.maxZoom = 4f
        imageViewLeft.maxZoom = 4f


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


    fun addForward_card_clicked(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 0)
    }
    fun addLeft_card_clicked(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 1)
    }
    fun addBackward_card_clicked(view: View)
    {
        val fromGalleryIntent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), 2)
    }
    fun addRight_card_clicked(view: View)
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
                0-> {imageViewTop.setImageURI(data!!.data)
                    Log.d("personal maid", "1")
                    addForward_card.visibility = View.GONE
                    Log.d("personal maid", "2")
                    imageViewTop.visibility = View.VISIBLE
                    Log.d("personal maid", "3")
//                    photoSets[photoSets.count()-1][0] = data.data
                    Log.d("personal maid", "4")
                    chooseNewPhotoCard.isClickable = true
                    Log.d("personal maid", "5")}

                1-> {imageViewRight.setImageURI(data!!.data)
                    addLeft_card.visibility =View.GONE
                    imageViewRight.visibility = View.VISIBLE
                    //photoSets[photoSets.count()-1][1] = data.data
                    chooseNewPhotoCard.isClickable = true}

                2-> {imageViewBottom.setImageURI(data!!.data)
                    addBackward_card.visibility = View.GONE
                    imageViewBottom.visibility = View.VISIBLE
                    //photoSets[photoSets.count()-1][2] = data.data
                    chooseNewPhotoCard.isClickable = true}

                3-> {imageViewLeft.setImageURI(data!!.data)
                    addRight_card.visibility = View.GONE
                    imageViewLeft.visibility = View.VISIBLE
                   // photoSets[photoSets.count()-1][3] = data.data
                    chooseNewPhotoCard.isClickable = true}
            }
        }
    }
    fun correctImage_card_onClick(view:View)
    {
        cards_turnIsClickable()
        correctImageTIV.visibility = View.VISIBLE
        activity_photo_controlGrid.foreground = darkerForegroundColor

    }

    override fun onBackPressed() {
        if (correctImageTIV.visibility == View.INVISIBLE)
            super.onBackPressed()
        if(correctImageTIV.visibility == View.VISIBLE) {
            cards_turnIsClickable()
            correctImageTIV.visibility = View.INVISIBLE
            activity_photo_controlGrid.foreground = null
        }
    }


    fun cards_turnIsClickable()
    {

        nextImageCard.isClickable = false
        prevImageCard.isClickable = false

        correctImageCard.isClickable = !correctImageCard.isClickable
        chooseNewPhotoCard.isClickable = correctImageCard.isClickable
    }

    fun chooseNewPhoto(view:View)
    {
        addForward_card.visibility = View.VISIBLE
        addRight_card.visibility = View.VISIBLE
        addBackward_card.visibility = View.VISIBLE
        addLeft_card.visibility = View.VISIBLE

        imageViewTop.visibility = View.INVISIBLE
        imageViewRight.visibility = View.INVISIBLE
        imageViewBottom.visibility = View.INVISIBLE
        imageViewLeft.visibility = View.INVISIBLE

        //im so sorry for this >-<       :''(            I'll fix it ASAP
       // photoSets.add({} as MutableMap<Int, Uri?>)

    }
}