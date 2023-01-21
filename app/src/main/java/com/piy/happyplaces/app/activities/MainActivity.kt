package com.piy.happyplaces.app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.piy.happyplaces.app.R
import com.piy.happyplaces.app.database.DatabaseHandler
import com.piy.happyplaces.app.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fabAddHappyPlace.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        getHappyPlacesFromLocalDB()
    }

    private fun getHappyPlacesFromLocalDB(){
        val db = DatabaseHandler(this)
        val happyPlaceList: ArrayList<HappyPlaceModel> = db.getHappyPlacesList()

        if(happyPlaceList.size>0){
            for (i in happyPlaceList){
                Log.e("[ENTRIES - TITLE]", i.title)
                Log.e("[ENTRIES - DESCRIPTION]", i.description)
            }
        }
    }
}