package com.vshirodkaer.destinationx.application

import com.vshirodkaer.destinationx.departure.module.DepartureModule
import com.vshirodkaer.destinationx.network.module.NetworkModule

interface Modules {
  val networkModule: NetworkModule
  val departureModule: DepartureModule
}