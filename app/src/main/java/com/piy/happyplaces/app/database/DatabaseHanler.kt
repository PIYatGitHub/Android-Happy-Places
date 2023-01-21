package com.piy.happyplaces.app.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.piy.happyplaces.app.models.HappyPlaceModel

class DatabaseHanler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,
null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION =1
        private const val DATABASE_NAME = "HappyPlacesDatabase"
        private const val TABLE_HAPPY_PLACE = "HappyPlacesTable"
        // all col names
        private const val KEY_ID = "_id"
        private const val KEY_TITLE = "title"
        private const val KEY_IMAGE = "image"
        private const val KEY_DESCRIPTION = "description"
        private const val KEY_DATE = "date"
        private const val KEY_LOCATION = "location"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_LATITUDE = "latitude"
    }

    override fun onCreate(db: SQLiteDatabase?) {
       val CCREATE_HAPPY_PLACE_TABE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + " " +
               KEY_ID + " INTEGER PRIMARY KEY,"
               + KEY_TITLE + "TEXT,"
               + KEY_IMAGE + "TEXT,"
               + KEY_DESCRIPTION + "TEXT,"
               + KEY_DATE + "TEXT,"
              +  KEY_LOCATION + "TEXT,"
               + KEY_LATITUDE + "TEXT,"
               + KEY_LONGITUDE + "TEXT)"
               )

        db?.execSQL(CCREATE_HAPPY_PLACE_TABE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVer: Int, newVer: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_HAPPY_PLACE")
        onCreate(db)
    }

    /**
     * Happy places CRUD
     */
    fun addHappyPlace (happyPlace: HappyPlaceModel): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(KEY_TITLE, happyPlace.title)
        contentValues.put(KEY_IMAGE, happyPlace.image)
        contentValues.put(KEY_DESCRIPTION, happyPlace.description)
        contentValues.put(KEY_DATE, happyPlace.date)
        contentValues.put(KEY_LOCATION, happyPlace.location)
        contentValues.put(KEY_LATITUDE, happyPlace.latitude)
        contentValues.put(KEY_LONGITUDE, happyPlace.longitude)

        val res = db.insert(TABLE_HAPPY_PLACE, null, contentValues)
        db.close()
        return  res
    }

}