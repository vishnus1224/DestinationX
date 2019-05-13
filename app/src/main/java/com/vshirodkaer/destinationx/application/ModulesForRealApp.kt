package com.vshirodkaer.destinationx.application

import com.vshirodkaer.destinationx.departure.module.DepartureModule
import com.vshirodkaer.destinationx.departure.module.DepartureModuleForRealApp
import com.vshirodkaer.destinationx.network.module.NetworkModule
import com.vshirodkaer.destinationx.network.module.NetworkModuleForRealApp

object ModulesForRealApp : Modules {

  override val networkModule: NetworkModule = NetworkModuleForRealApp

  override val departureModule: DepartureModule = DepartureModuleForRealApp(networkModule)

}