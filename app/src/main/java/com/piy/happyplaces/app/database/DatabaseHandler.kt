package com.piy.happyplaces.app.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import com.piy.happyplaces.app.models.HappyPlaceModel

class DatabaseHandler(context: Context): SQLiteOpenHelper(context, DATABASE_NAME,
null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_VERSION = 1
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
       val CREATE_HAPPY_PLACE_TABE = ("CREATE TABLE " + TABLE_HAPPY_PLACE + "(" +
               KEY_ID + " INTEGER PRIMARY KEY, "
               + KEY_TITLE + " TEXT, "
               + KEY_IMAGE + " TEXT, "
               + KEY_DESCRIPTION + " TEXT, "
               + KEY_DATE + " TEXT, "
              +  KEY_LOCATION + " TEXT, "
               + KEY_LATITUDE + " TEXT, "
               + KEY_LONGITUDE + " TEXT )"
               )

        println("SQL IS $CREATE_HAPPY_PLACE_TABE")
        db?.execSQL(CREATE_HAPPY_PLACE_TABE)
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


    @SuppressLint("Range")
    fun getHappyPlacesList (): ArrayList<HappyPlaceModel> {
        val db = this.writableDatabase
        val happyPlaceList=  ArrayList<HappyPlaceModel>()
        val query = "SELECT * from $TABLE_HAPPY_PLACE"

        try {
          val cursor: Cursor = db.rawQuery(query, null)
            if(cursor.moveToFirst()){
                do {
                    val place = HappyPlaceModel(
                        cursor.getInt(cursor.getColumnIndex(KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(KEY_TITLE)),
                        cursor.getString(cursor.getColumnIndex(KEY_IMAGE)),
                        cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)),
                        cursor.getString(cursor.getColumnIndex(KEY_DATE)),
                        cursor.getString(cursor.getColumnIndex(KEY_LOCATION)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LATITUDE)),
                        cursor.getDouble(cursor.getColumnIndex(KEY_LONGITUDE)),

                    )
                    happyPlaceList.add(place)
                }while (cursor.moveToNext())
            }

            cursor.close()
        }catch (E: SQLiteException){
            db.execSQL(query)
            return  ArrayList()
        }

        return  happyPlaceList
    }

}