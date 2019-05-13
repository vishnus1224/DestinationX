package com.vshirodkaer.destinationx.departure.usecase.entity

data class Departure(
  val lineNumber: String,
  val direction: String,
  val departureTime: Long,
  val stationTimeZone: String
)