package com.piy.happyplaces.app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.IOException


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val PermissionsRequestCode = 123
    private lateinit var manageMediaPermissions: ManagePermissions
    private lateinit var manageCameraPermissions: ManagePermissions


    companion object {
        private const val GALLERY_PICK_IMAGE = 1
        private const val PICK_IMAGE_CAMERA = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        setSupportActionBar(toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        etDate.setOnClickListener(this)
        tvAddImage.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.etDate -> {
                println("CLICKING ON DATE PICKER!!!")
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                ).show()
            }
            R.id.tvAddImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems =
                    arrayOf("Select photo from gallery", "Capture photo with phone")
                pictureDialog.setItems(pictureDialogItems) { dialog, which ->
                    when (which) {
                        0 -> choosePhotoFromGallery()
                        1 -> choosePhotoFromCamera()
                    }
                }
                pictureDialog.show()
            }
        }
    }

    private fun updateDateInView() {
        val formatter = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(formatter, Locale.getDefault())
        etDate.setText(sdf.format(calendar.time).toString())
    }

    private fun choosePhotoFromGallery() {
        val list = arrayOf<String>(
            Manifest.permission.READ_MEDIA_IMAGES
        )

        manageMediaPermissions = ManagePermissions(this, list, PermissionsRequestCode)
        manageMediaPermissions.checkPermissions()
        if (manageMediaPermissions.isPermissionsGranted() == 0) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_PICK_IMAGE)
        }
    }


    private fun choosePhotoFromCamera() {
        val list = arrayOf<String>(
            Manifest.permission.CAMERA
        )

        manageCameraPermissions = ManagePermissions(this, list, PermissionsRequestCode)
        manageCameraPermissions.checkPermissions()
        if (manageCameraPermissions.isPermissionsGranted() == 0) {
            val galleryIntent =
                Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(galleryIntent, PICK_IMAGE_CAMERA)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_PICK_IMAGE) {
                if (data != null) {
                    //there is an image
                    val contentUri = data.data
                    try {
                        val selectedImgBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        ivPlaceImage.setImageBitmap(selectedImgBitmap)

                    } catch (exception: IOException) {
                        exception.printStackTrace()
                        Toast.makeText(
                            this@AddHappyPlaceActivity,
                            "Could not get image from gallery.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            if (requestCode == PICK_IMAGE_CAMERA) {
               val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                ivPlaceImage.setImageBitmap(thumbnail)
            }
        }
    }


}