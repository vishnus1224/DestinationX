package com.vshirodkaer.destinationx.departure.module

import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation
import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import io.reactivex.Observable

object DepartureModuleForInstrumentationApp : DepartureModule {

  lateinit var departures: Observable<List<Departure>>

  override val getDeparturesFromStation: GetDeparturesFromStation
    get() = GetDepartureFromStationForInstrumentation(departures)
}

private class GetDepartureFromStationForInstrumentation(
  private val response: Observable<List<Departure>>
) : GetDeparturesFromStation {

  override fun execute(stationId: Int): Observable<List<Departure>> = response

}