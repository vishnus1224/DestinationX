package com.vshirodkaer.destinationx.timetable

import com.vshirodkaer.destinationx.timetable.model.StationTimeTableResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface StationTimeTableWebService {
  @GET("/mobile/v1/network/station/{id}/timetable")
  fun getStationTimeTable(@Path("id") stationId: Int): Observable<StationTimeTableResponse>
}