package com.vshirodkaer.destinationx.network

import com.vshirodkaer.destinationx.network.module.NetworkModule
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object NetworkModuleForInstrumentationApp : NetworkModule {
  override val okHttpClient: OkHttpClient
    get() = TODO("Not required right now.. crash if accessed")
  override val retrofit: Retrofit
    get() = TODO("Not required right now.. crash if accessed")
}