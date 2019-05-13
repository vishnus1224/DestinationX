package com.vshirodkaer.destinationx.departure.android.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vshirodkaer.destinationx.R
import com.vshirodkaer.destinationx.departure.android.DeparturesUiModel

object DeparturesFromStationAdapter : RecyclerView.Adapter<DeparturesFromStationAdapter.DeparturesFromStationViewHolder>() {

  private val dataSet = mutableListOf<DeparturesUiModel>()

  fun showDepartures(departures: List<DeparturesUiModel>) {
    dataSet.clear()
    dataSet.addAll(departures)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) = DeparturesFromStationViewHolder(
    LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_departures_from_station, viewGroup, false)
  )

  override fun getItemCount(): Int = dataSet.size

  override fun onBindViewHolder(holder: DeparturesFromStationViewHolder, position: Int) {
    val departure = dataSet[position]

    holder.departureTime.text = departure.departureTime
    holder.departureDirection.text = departure.direction
    holder.departureLineNumber.text = departure.lineNumber
  }

  class DeparturesFromStationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val departureTime: TextView = view.findViewById(R.id.adapter_departures_time)
    val departureDirection: TextView = view.findViewById(R.id.adapter_departures_direction)
    val departureLineNumber: TextView = view.findViewById(R.id.adapter_departures_line_number)
  }
}