package com.weather.android.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.weather.android.R
import com.weather.android.logic.model.Place
import com.weather.android.ui.weather.WeatherActivity
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.place_item.view.*

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>) :
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.placeName
        val placeAddress: TextView = view.placeAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent, false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.absoluteAdapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            if (activity is WeatherActivity){
                activity.drawerLayout.closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
            } else {
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            fragment.startActivity(intent)
            fragment.activity?.finish()
        }
            fragment.viewModel.savePlace(place)
        }
            return holder
    }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val place = placeList[position]
            holder.placeName.text = place.name
            holder.placeAddress.text = place.address
        }

        override fun getItemCount(): Int = placeList.size
    }