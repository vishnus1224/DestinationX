package com.vshirodkaer.destinationx.departure.android

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation

class DeparturesViewModelFactory(
  private val stationId: Int,
  private val getDeparturesFromStation: GetDeparturesFromStation
) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return DeparturesFromStationViewModel(stationId, getDeparturesFromStation) as T
  }
}