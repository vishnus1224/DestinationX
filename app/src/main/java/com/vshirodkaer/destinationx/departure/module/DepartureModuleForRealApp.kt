package com.vshirodkaer.destinationx.departure.module

import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation
import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStationImpl
import com.vshirodkaer.destinationx.network.module.NetworkModule
import com.vshirodkaer.destinationx.timetable.webservice.StationTimeTableWebService

class DepartureModuleForRealApp(networkModule: NetworkModule) : DepartureModule {

  override val getDeparturesFromStation: GetDeparturesFromStation = GetDeparturesFromStationImpl(
    networkModule.retrofit.create(StationTimeTableWebService::class.java)
  )

}