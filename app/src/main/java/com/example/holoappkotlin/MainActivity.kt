package com.example.holoappkotlin

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import com.example.holoappkotlin.databinding.ActivityMainBinding
import RequestCodes

import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var darkerForegroundColor:Drawable? = null

    lateinit var photoPopup:PopupWindow


    var popup_isActive = false

    var makingChooseIntent = false

    lateinit var newPhotoUri:Uri

    lateinit var currentPhotoPath: String


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding  = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        photoPopup = PopupWindow(this)
        val popupView =layoutInflater.inflate(R.layout.layout_photo_popup,null)
        photoPopup.contentView = popupView
        photoPopup.setBackgroundDrawable(null)

        popupView.findViewById<CardView>(R.id.fromGalleryCard).setOnClickListener{
            if (!makingChooseIntent)
            {
                makingChooseIntent = true

                val fromGalleryIntent = Intent()
                    .setType("image/jpeg")
                    .setAction(Intent.ACTION_GET_CONTENT)
                    .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
                startActivityForResult(Intent.createChooser(fromGalleryIntent, "Select a file"), RequestCodes.GALLERY_PHOTO.ordinal)
            }
        }

        popupView.findViewById<CardView>(R.id.takeAPhotoCard).setOnClickListener{
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
                        startActivityForResult(takePictureIntent, RequestCodes.CAPTURE_PHOTO.ordinal)
                    }
                }
            }
        }

        darkerForegroundColor = binding.root.foreground
        binding.root.foreground = null

        binding.exitCard.setOnClickListener {finishAffinity()}

    }

    fun photoCard_onClick(view: View)
    {
        photoPopup.showAtLocation(binding.root,Gravity.CENTER,0,0)

        binding.root.foreground = darkerForegroundColor

        turnCards_isEnable(false)
        popup_isActive = true

    }

    fun gifCard_onClick(view:View)
    {
        val gifGalleryIntent = Intent()
            .setType("image/gif")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(gifGalleryIntent, "Select a file"), RequestCodes.GALLERY_GIF.ordinal)
    }


    fun videoCard_onClick(view:View)
    {
        val videoIntent = Intent()
            .setType("video/*")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,false)
        startActivityForResult(videoIntent, RequestCodes.GALLERY_VIDEO.ordinal)
    }

    fun fsideCard_onClick(view:View)
    {
        startActivity(Intent(this,FsideActivity::class.java))
    }

    fun spinCard_onClick(view:View)
    {
        startActivity(Intent(this, SpinningActivity::class.java))
    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN){
            photoPopup.dismiss()
            binding.root.foreground = null
            turnCards_isEnable(true)
            popup_isActive = false
        }
        return super.onTouchEvent(event)
    }


    override fun onBackPressed() {

        if(popup_isActive) {
            photoPopup.dismiss()
            binding.root.foreground = null
            turnCards_isEnable(true)
        }
        else
            return super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            val uris :ArrayList<String> = ArrayList()
            when (requestCode) {
                RequestCodes.GALLERY_PHOTO.ordinal -> {
                    val photoIntent = Intent(this, PhotoActivity::class.java)
                    if (data?.clipData != null)
                        for (i in 0 until data.clipData!!.itemCount)
                            uris.add(data.clipData!!.getItemAt(i).uri.toString())

                    else if (data?.data != null)
                        uris.add(data.data!!.toString())

                    photoIntent.putExtra("photosUri_stringArrayList", uris)
                    startActivity(photoIntent)
                }

                RequestCodes.CAPTURE_PHOTO.ordinal -> {
                    val photoIntent = Intent(this, PhotoActivity::class.java)
                    uris.add(newPhotoUri.toString())
                    photoIntent.putExtra("photosUri_stringArrayList", uris)
                    startActivity(photoIntent)
                }

                RequestCodes.GALLERY_GIF.ordinal ->{
                    val gifIntent = Intent(this,GifActivity::class.java)

                    if (data?.clipData!=null)
                        for (i in 0 until data.clipData!!.itemCount )
                            uris.add(data.clipData!!.getItemAt(i).uri.toString())

                    else if (data?.data!=null)
                        uris.add(data.data!!.toString())

                    gifIntent.putExtra("uris",uris)
                    startActivity(gifIntent)
                }

                RequestCodes.GALLERY_VIDEO.ordinal ->{
                    val videoIntent = Intent(this,VideoActivity::class.java)
                    if(data!!.data!=null) {
                        videoIntent.putExtra("uri", data.data.toString() )
                        startActivity(videoIntent)
                    }



//                    if (data?.clipData!=null)
//                    {
//                        for (i in 0 until data.clipData!!.itemCount)
//                        {
//                            uri_stringArrayList.add(data.clipData!!.getItemAt(i).uri.toString())
//                        }
//                    }
//                    else if (data?.data!=null)
//                    {
//                        uri_stringArrayList.add(data.data!!.toString())
//                    }
//                    videoIntent.putExtra("uris",uri_stringArrayList)
//                    startActivity(videoIntent)
                }
            }

        }
        makingChooseIntent = false
    }


    fun turnCards_isEnable(state:Boolean)
    {
        binding.photoCard.isClickable = state
        binding.gifCard.isClickable = state
        binding.videoCard.isClickable = state
        binding.exitCard.isClickable = state
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

}