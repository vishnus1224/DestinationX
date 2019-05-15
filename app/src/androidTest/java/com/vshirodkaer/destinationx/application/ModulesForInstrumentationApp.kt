package com.vshirodkaer.destinationx.application

import com.vshirodkaer.destinationx.departure.module.DepartureModule
import com.vshirodkaer.destinationx.departure.module.DepartureModuleForInstrumentationApp
import com.vshirodkaer.destinationx.network.NetworkModuleForInstrumentationApp
import com.vshirodkaer.destinationx.network.module.NetworkModule

object ModulesForInstrumentationApp : Modules {

  override val networkModule: NetworkModule
    get() = NetworkModuleForInstrumentationApp

  override val departureModule: DepartureModule
    get() = DepartureModuleForInstrumentationApp

}