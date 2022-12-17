package com.example.holoappkotlin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.holoappkotlin.databinding.ActivityPhotoBinding
import com.ortiz.touchview.TouchImageView.OnTouchImageViewListener
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding

    var REQUEST_CODE_GALLERY = 0
    var REQUEST_CODE_PHOTO_CAPTURE = 1

    var images = ArrayList<String>()
    var currentImageId = 0


    var darkerForegroundColor:Drawable? = null

    lateinit var photoPopup:PopupWindow
    var popup_isActive = false
    var makingChooseIntent = false
    lateinit var newPhotoUri:Uri
    lateinit var currentPhotoPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPhotoBinding.inflate(layoutInflater)
        val photoview = binding.root
        setContentView(photoview)

        Log.d("personalMaid", "photo on create")

        darkerForegroundColor = activity_photo_controlGrid.foreground
        activity_photo_controlGrid.foreground = null

        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        pictureGrid.layoutParams.height = dm.widthPixels



        imageViewTop.maxZoom = 4f
        imageViewRight.maxZoom = 4f
        imageViewBottom.maxZoom = 4f
        imageViewLeft.maxZoom = 4f


        binding.imageViewMain.setOnTouchImageViewListener( object:OnTouchImageViewListener{
            override fun onMove() {
                binding.imageViewTop.setZoom(binding.imageViewMain)
                binding.imageViewRight.setZoom(binding.imageViewMain)
                binding.imageViewBottom.setZoom(binding.imageViewMain)
                binding.imageViewLeft.setZoom(binding.imageViewMain)

                binding.correctImageTIV.setZoom(binding.imageViewMain)
            }
        })

        binding.correctImageTIV.setOnTouchImageViewListener( object:OnTouchImageViewListener{
            override fun onMove() {
                binding.imageViewTop.setZoom(binding.correctImageTIV)
                binding.imageViewRight.setZoom(binding.correctImageTIV)
                binding.imageViewBottom.setZoom(binding.correctImageTIV)
                binding.imageViewLeft.setZoom(binding.correctImageTIV)

                binding.imageViewMain.setZoom(binding.correctImageTIV)

            }
        })
//        activity_photo_seekBar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
//            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                val currentScale = seekBar?.progress!!.toFloat()/100+1
//
//                imageViewTop.scaleX = currentScale
//                imageViewTop.scaleY = currentScale
//
//                imageViewRight.scaleX = currentScale
//                imageViewRight.scaleY = currentScale
//
//                imageViewBottom.scaleX = currentScale
//                imageViewBottom.scaleY = currentScale
//
//                imageViewLeft.scaleX = currentScale
//                imageViewLeft.scaleY = currentScale
//
//            }
//
//            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
//
//            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
//        })
        images = intent.getStringArrayListExtra("photosUri_stringArrayList")!!


        setImageUri()

        if(images.count()==1)
        {
            nextImageCard.isClickable = false
            prevImageCard.isClickable = false
        }

        photoPopup = PopupWindow(this)
        val view =layoutInflater.inflate(R.layout.layout_photo_popup,null)
        photoPopup.contentView = view
        photoPopup.setBackgroundDrawable(null)

        view.findViewById<CardView>(R.id.fromGalleryCard).setOnClickListener{
            if (!makingChooseIntent)
            {
                makingChooseIntent = true

                val fromGalleryIntent = Intent()
                    .setType("image/jpeg")
                    .setAction(Intent.ACTION_GET_CONTENT)
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), REQUEST_CODE_GALLERY)
            }
            photoPopup.dismiss()
            popup_isActive = false
            cards_turnIsClickable()
            activity_photo_root.foreground = null
        }

        view.findViewById<CardView>(R.id.takeAPhotoCard).setOnClickListener{
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                // Ensure that there's a camera activity to handle the intent
                takePictureIntent.resolveActivity(packageManager)?.also {
                    // Create the File where the photo should go
                    val photoFile: File? = try {
                        createImageFile()
                    } catch (ex: IOException) {
                        // Error occurred while creating the File

                        null
                    }
                    // Continue only if the File was successfully created
                    photoFile?.also {
                        newPhotoUri= FileProvider.getUriForFile(
                            this,
                            "com.example.android.fileprovider",
                            it
                        )
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, newPhotoUri)
                        startActivityForResult(takePictureIntent, REQUEST_CODE_PHOTO_CAPTURE)
                    }
                }
            }
            photoPopup.dismiss()
            popup_isActive = false
            cards_turnIsClickable()
            activity_photo_root.foreground = null
        }

    }

    fun switchToNextImage(view: View)
    {
        currentImageId++
        if (currentImageId == images.count())
            currentImageId = 0
        setImageUri()

    }
    fun switchToPreviousImage(view:View)
    {
        currentImageId--
        if (currentImageId==-1)
            currentImageId+=images.count()
        setImageUri()

    }

    fun correctImage(view:View)
    {
        cards_turnIsClickable()
        correctImageTIV.visibility = View.VISIBLE
        activity_photo_controlGrid.foreground = darkerForegroundColor

    }

    fun chooseNewPhoto(view:View)
    {
        photoPopup.showAtLocation(activity_photo_root, Gravity.CENTER,0,0)
        activity_photo_root.foreground = darkerForegroundColor
        cards_turnIsClickable()
        popup_isActive = true
    }

    override fun onBackPressed() {
        Log.d("popup_isActiveState", popup_isActive.toString())
        if (correctImageTIV.visibility == View.INVISIBLE && !popup_isActive )
            super.onBackPressed()
        if(correctImageTIV.visibility == View.VISIBLE)
        {
            cards_turnIsClickable()
            correctImageTIV.visibility = View.INVISIBLE
            activity_photo_controlGrid.foreground = null
        }
        if (popup_isActive)
        {
            photoPopup.dismiss()
            popup_isActive = false
            cards_turnIsClickable()
            activity_photo_root.foreground = null
        }

    }

    fun cards_turnIsClickable()
    {

        nextImageCard.isClickable = false
        prevImageCard.isClickable = false

        correctImageCard.isClickable = !correctImageCard.isClickable
        chooseNewPhotoCard.isClickable = correctImageCard.isClickable

        if (correctImageCard.isClickable)
            if(images.count() > 1)
            {
                nextImageCard.isClickable = true
                prevImageCard.isClickable = true
            }


    }

    fun setImageUri()
    {
        imageViewTop.setImageURI(Uri.parse(images[currentImageId]))
        imageViewRight.setImageURI(Uri.parse(images[currentImageId]))
        imageViewBottom.setImageURI(Uri.parse(images[currentImageId]))
        imageViewLeft.setImageURI(Uri.parse(images[currentImageId]))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        Log.d("touch",event.toString())

        if (event?.action == MotionEvent.ACTION_DOWN)
        {
            photoPopup.dismiss()
            activity_photo_root.foreground = null
            cards_turnIsClickable()
            popup_isActive = false

        }


        return super.onTouchEvent(event)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val uri_stringArrayList :ArrayList<String> = ArrayList()
            when (requestCode) {
                REQUEST_CODE_GALLERY -> {
                    val photoIntent = Intent(this, PhotoActivity::class.java)
                    if (data?.clipData != null) {
                        for (i in 0 until data.clipData!!.itemCount) {
                            uri_stringArrayList.add(data.clipData!!.getItemAt(i).uri.toString())
                        }
                    } else if (data?.data != null) {
                        uri_stringArrayList.add(data.data!!.toString())
                    }

                }

                REQUEST_CODE_PHOTO_CAPTURE -> {
                    val photoIntent = Intent(this, PhotoActivity::class.java)
                    uri_stringArrayList.add(newPhotoUri.toString())
                }
            }

            images.addAll(uri_stringArrayList)

            prevImageCard.isClickable = true
            nextImageCard.isClickable = true

        }
        makingChooseIntent = false
    }

}