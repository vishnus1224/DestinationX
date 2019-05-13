package com.vshirodkaer.destinationx.network.module

import okhttp3.OkHttpClient
import retrofit2.Retrofit

interface NetworkModule {
  val okHttpClient: OkHttpClient
  val retrofit: Retrofit
}