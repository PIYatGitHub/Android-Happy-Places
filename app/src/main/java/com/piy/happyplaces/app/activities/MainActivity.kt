package com.piy.happyplaces.app.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.piy.happyplaces.app.R
import com.piy.happyplaces.app.adapters.HappyPlacesAdapter
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

    private fun getHappyPlacesFromLocalDB() {
        val db = DatabaseHandler(this)
        val happyPlaceList: ArrayList<HappyPlaceModel> = db.getHappyPlacesList()

        if (happyPlaceList.size > 0) {
            rvHappyPlacesList.visibility = View.VISIBLE
            tvNoItems.visibility = View.GONE
            setupHappyPlacesRecyclerView(happyPlaceList)
        } else{
            rvHappyPlacesList.visibility = View.GONE
            tvNoItems.visibility = View.VISIBLE
        }
    }

    private fun setupHappyPlacesRecyclerView(happyPlaceList: ArrayList<HappyPlaceModel>) {
        rvHappyPlacesList.layoutManager = LinearLayoutManager(this)
        rvHappyPlacesList.setHasFixedSize(true)
        val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
        rvHappyPlacesList.adapter = placesAdapter
    }
}