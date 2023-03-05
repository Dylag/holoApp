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
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var REQUEST_CODE_GALLERY = 0
    var REQUEST_CODE_PHOTO_CAPTURE = 1
    var REQUEST_CODE_GIF = 2
    var REQUEST_CODE_VIDEO_FROM_GALLERY = 3

    var darkerForegroundColor:Drawable? = null

    lateinit var photoPopup:PopupWindow


    var popup_isActive = false

    var makingChooseIntent = false

    lateinit var newPhotoUri:Uri

    lateinit var currentPhotoPath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


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
        }

        darkerForegroundColor = activity_main_constraintLayout.foreground
        activity_main_constraintLayout.foreground = null

        exitCard.setOnClickListener {finishAffinity()}

    }

    fun photoCard_onClick(view: View)
    {
        photoPopup.showAtLocation(activity_main_constraintLayout,Gravity.CENTER,0,0)

        activity_main_constraintLayout.foreground = darkerForegroundColor

        turnCards_isEnable(false)
        popup_isActive = true

    }

    fun gifCard_onClick(view:View)
    {
        val gifGalleryIntent = Intent()
            .setType("image/gif")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(gifGalleryIntent, "Select a file"), REQUEST_CODE_GIF)
    }


    fun videoCard_onClick(view:View)
    {
        val videoGalleryIntent = Intent()
            .setType("video/mp4")
            .setAction(Intent.ACTION_GET_CONTENT)
            .putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true)
        startActivityForResult(Intent.createChooser(videoGalleryIntent, "Select a file"), REQUEST_CODE_VIDEO_FROM_GALLERY)
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
        Log.d("touch",event?.action.toString())
//        if (event?.action == MotionEvent.ACTION_UP)
//            popup_dismissed = false
        if (event?.action == MotionEvent.ACTION_DOWN)
        {
            photoPopup.dismiss()
            activity_main_constraintLayout.foreground = null
            turnCards_isEnable(true)
            popup_isActive = false
//            popup_dismissed = true
        }
        return super.onTouchEvent(event)
    }


    override fun onBackPressed() {

        if(popup_isActive) {
            photoPopup.dismiss()
            activity_main_constraintLayout.foreground = null
            turnCards_isEnable(true)
        }
        else
            return super.onBackPressed()
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
                    photoIntent.putExtra("photosUri_stringArrayList", uri_stringArrayList)
                    startActivity(photoIntent)
                }

                REQUEST_CODE_PHOTO_CAPTURE -> {
                    val photoIntent = Intent(this, PhotoActivity::class.java)
                    uri_stringArrayList.add(newPhotoUri.toString())
                    photoIntent.putExtra("photosUri_stringArrayList", uri_stringArrayList)
                    startActivity(photoIntent)
                }

                REQUEST_CODE_GIF ->{
                    val gifIntent = Intent(this,GifActivity::class.java)

                    if (data?.clipData!=null)
                    {
                        for (i in 0 until data.clipData!!.itemCount )
                        {
                            uri_stringArrayList.add(data.clipData!!.getItemAt(i).uri.toString())
                        }
                    }
                    else if (data?.data!=null)
                    {
                        uri_stringArrayList.add(data.data!!.toString())
                    }
                    gifIntent.putExtra("uris",uri_stringArrayList)
                    startActivity(gifIntent)
                }

                REQUEST_CODE_VIDEO_FROM_GALLERY ->{
                    val videoIntent = Intent(this,VideoActivity::class.java)
                    if (data?.clipData!=null)
                    {
                        for (i in 0 until data.clipData!!.itemCount)
                        {
                            uri_stringArrayList.add(data.clipData!!.getItemAt(i).uri.toString())
                        }
                    }
                    else if (data?.data!=null)
                    {
                        uri_stringArrayList.add(data.data!!.toString())
                    }
                    videoIntent.putExtra("uris",uri_stringArrayList)
                    startActivity(videoIntent)
                }
            }

        }
        makingChooseIntent = false
    }


    fun turnCards_isEnable(state:Boolean)
    {
        photoCard.isClickable = state
        gifCard.isClickable = state
        videoCard.isClickable = state
        exitCard.isClickable = state
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