package com.vshirodkaer.destinationx.departure.usecase

import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import com.vshirodkaer.destinationx.timetable.webservice.StationTimeTableWebService
import com.vshirodkaer.destinationx.timetable.webservice.model.DepartureJson
import com.vshirodkaer.destinationx.timetable.webservice.model.StationTimeTableResponse
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

/**
 * When executed, fetches the time table for a station and returns the departures.
 */
class GetDeparturesFromStationImpl(
  private val stationTimeTableService: StationTimeTableWebService
) : GetDeparturesFromStation {

  override fun execute(stationId: Int): Observable<List<Departure>> =
    stationTimeTableService
      .getStationTimeTable(stationId)
      .map(::responseToListOfDepartures)
      .subscribeOn(Schedulers.io())
}

private fun responseToListOfDepartures(timeTableResponse: StationTimeTableResponse): List<Departure> =
  timeTableResponse
    .timetable
    .departures
    .map(::jsonToEntity)

private fun jsonToEntity(departureJson: DepartureJson) = Departure(
  lineNumber = departureJson.line_code,
  direction = departureJson.direction,
  departureTime = departureJson.datetime.timestamp,
  stationTimeZone = departureJson.datetime.tz
)