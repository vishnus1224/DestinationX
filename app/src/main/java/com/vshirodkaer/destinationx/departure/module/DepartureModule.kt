package com.vshirodkaer.destinationx.departure.module

import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation

interface DepartureModule {
  val getDeparturesFromStation: GetDeparturesFromStation
}