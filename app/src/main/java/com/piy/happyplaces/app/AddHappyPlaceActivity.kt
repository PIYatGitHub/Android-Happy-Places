package com.piy.happyplaces.app

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_add_happy_place.*
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.app.Activity
import android.provider.MediaStore
import java.io.IOException


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions


    companion object {
        private const val GALLERY_PICK_IMAGE = 1
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
                        1 -> {
                            Toast.makeText(
                                this@AddHappyPlaceActivity,
                                "Camera picker coming soon",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

        managePermissions = ManagePermissions(this, list, PermissionsRequestCode)
        managePermissions.checkPermissions()
        if (managePermissions.isPermissionsGranted() == 0) {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_PICK_IMAGE)
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

                    } catch (exception: IOException){
                        exception.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity,
                        "Could not get image from gallery.",
                        Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }


}