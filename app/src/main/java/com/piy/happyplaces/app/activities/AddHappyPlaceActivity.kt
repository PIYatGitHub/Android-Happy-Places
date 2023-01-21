package com.piy.happyplaces.app.activities

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
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import com.piy.happyplaces.app.ManagePermissions
import com.piy.happyplaces.app.R
import com.piy.happyplaces.app.database.DatabaseHandler
import com.piy.happyplaces.app.models.HappyPlaceModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream


class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener {
    private val calendar = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private val PermissionsRequestCode = 123
    private lateinit var manageMediaPermissions: ManagePermissions
    private lateinit var manageCameraPermissions: ManagePermissions

    private var saveImageToInernalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0


    companion object {
        private const val GALLERY_PICK_IMAGE = 1
        private const val PICK_IMAGE_CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
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

        updateDateInView() //add today by default
        etDate.setOnClickListener(this)
        tvAddImage.setOnClickListener(this)
        btnSave.setOnClickListener(this)
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
            R.id.btnSave -> {
                when {
                    etTitle.text.isNullOrEmpty() -> showToast("Please enter a title")
                    etDescription.text.isNullOrEmpty() -> showToast("Please enter a description")
                    etLocation.text.isNullOrEmpty() -> showToast("Please enter a location")
                    saveImageToInernalStorage == null -> showToast("Please select an image")
                    else -> {

                        val data = HappyPlaceModel(
                            0,
                            etTitle.text.toString(),
                            saveImageToInernalStorage.toString(),
                            etDescription.text.toString(),
                            etDate.text.toString(),
                            etLocation.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val dbHanler = DatabaseHandler(this)
                        val insertResult  = dbHanler.addHappyPlace(data)
                        if(insertResult > 0){
                            showToast("Inserted successfully.")
                            finish()
                        }
                    }
                }


            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(
            this@AddHappyPlaceActivity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpeg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return Uri.parse(file.absolutePath)
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
                        saveImageToInernalStorage = saveImageToInternalStorage(selectedImgBitmap)
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
                saveImageToInernalStorage = saveImageToInternalStorage(thumbnail)
            }
        }
    }


}