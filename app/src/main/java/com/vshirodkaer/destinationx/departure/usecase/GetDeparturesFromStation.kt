package com.vshirodkaer.destinationx.departure.usecase

import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import io.reactivex.Observable

/**
 * Use case for getting a list of departures from a station given its id.
 */
interface GetDeparturesFromStation {
  fun execute(stationId: Int): Observable<List<Departure>>
}