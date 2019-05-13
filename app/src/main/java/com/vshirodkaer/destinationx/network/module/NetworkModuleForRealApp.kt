package com.vshirodkaer.destinationx.network.module

import com.vshirodkaer.destinationx.network.requestinterceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

object NetworkModuleForRealApp : NetworkModule {

  override val okHttpClient: OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(HeaderInterceptor)
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

  override val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("http://api.mobile.staging.mfb.io")
    .client(okHttpClient)
    .addConverterFactory(MoshiConverterFactory.create())
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()

}