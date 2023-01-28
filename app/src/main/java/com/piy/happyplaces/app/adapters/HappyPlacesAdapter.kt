package com.piy.happyplaces.app.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.piy.happyplaces.app.R
import com.piy.happyplaces.app.models.HappyPlaceModel
import kotlinx.android.synthetic.main.item_happy_place.view.*

open class HappyPlacesAdapter (private val context: Context,
private val list: ArrayList<HappyPlaceModel>
): RecyclerView.Adapter<RecyclerView.ViewHolder> (){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return HappyPlacesViewHolder(
           LayoutInflater.from(context).inflate(
               R.layout.item_happy_place,
               parent,
               false
           )
       )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      var model = list[position]
        if(holder is HappyPlacesViewHolder) {
            holder.itemView.ivRoundPlaceImage.setImageURI(Uri.parse(model.image))
            holder.itemView.tvTitle.text = model.title
            holder.itemView.tvDescription.text = model.description
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class HappyPlacesViewHolder(view: View): RecyclerView.ViewHolder(view)
}