package com.vshirodkaer.destinationx.timetable.model

data class StationTimeTableResponse(
  val timetable: StationTimeTableJson
)

data class StationTimeTableJson(
  val departures: List<DepartureJson>
)

data class DepartureJson(
  val line_code: String,
  val direction: String,
  val datetime: DateTimeJson
)

data class DateTimeJson(
  val timestamp: Long,
  val tz: String
)