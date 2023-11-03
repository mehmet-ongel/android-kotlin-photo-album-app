package com.techmania.photoalbum.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.techmania.photoalbum.R
import com.techmania.photoalbum.databinding.ActivityAddImageBinding
import com.techmania.photoalbum.model.MyImages
import com.techmania.photoalbum.util.ControlPermission
import com.techmania.photoalbum.util.ConvertImage
import com.techmania.photoalbum.viewmodel.MyImagesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddImageActivity : AppCompatActivity() {

    lateinit var addImageBinding : ActivityAddImageBinding
    lateinit var activityResultLauncherForSelectImage : ActivityResultLauncher<Intent>
    lateinit var selectedImage : Bitmap
    lateinit var myImagesViewModel : MyImagesViewModel
    var control = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addImageBinding = ActivityAddImageBinding.inflate(layoutInflater)
        setContentView(addImageBinding.root)

        myImagesViewModel = ViewModelProvider(this)[MyImagesViewModel::class.java]

        //register
        registerActivityForSelectImage()

        addImageBinding.imageViewAddImage.setOnClickListener {

            if (ControlPermission.checkPermission(this)){
                //access the images
                val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //startActivityForResult -> Before API 30
                activityResultLauncherForSelectImage.launch(intent)

            }else{
                if (Build.VERSION.SDK_INT >= 33){
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                        1
                    )
                }else{
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            }

        }

        addImageBinding.buttonAdd.setOnClickListener {

            if (control){

                addImageBinding.buttonAdd.text = "Uploading... Please Wait"
                addImageBinding.buttonAdd.isEnabled = false

                GlobalScope.launch(Dispatchers.IO) {

                    val title = addImageBinding.editTextAddTitle.text.toString()
                    val description = addImageBinding.editTextAddDescription.text.toString()
                    val imageAsString = ConvertImage.convertToString(selectedImage)
                    if (imageAsString != null){
                        myImagesViewModel.insert(MyImages(title,description,imageAsString))
                        control = false
                        finish()
                    }else{
                        Toast.makeText(applicationContext,"There is a problem, please select a new image",Toast.LENGTH_SHORT).show()
                    }

                }


            }else{
                Toast.makeText(applicationContext,"Please select a photo",Toast.LENGTH_SHORT).show()
            }



        }

        addImageBinding.toolbarAddImage.setNavigationOnClickListener {
            finish()
        }

    }

    fun registerActivityForSelectImage(){

        activityResultLauncherForSelectImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

            //result of the intent
            val resultCode = result.resultCode
            val imageData = result.data

            if (resultCode == RESULT_OK && imageData != null){

                val imageUri = imageData.data

                imageUri?.let {
                    selectedImage = if (Build.VERSION.SDK_INT >= 28){

                        val imageSource = ImageDecoder.createSource(this.contentResolver,it)
                        ImageDecoder.decodeBitmap(imageSource)

                    }else{
                        MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
                    }

                    addImageBinding.imageViewAddImage.setImageBitmap(selectedImage)
                    control = true
                }



            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            //access the images
            val intent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //startActivityForResult -> Before API 30
            activityResultLauncherForSelectImage.launch(intent)

        }

    }
}