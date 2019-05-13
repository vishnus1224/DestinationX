package com.vshirodkaer.destinationx.network.requestinterceptor

import okhttp3.Interceptor
import okhttp3.Response

object HeaderInterceptor : Interceptor {

  override fun intercept(chain: Interceptor.Chain): Response {
    val originalRequest = chain.request()

    val requestWithAuthenticationHeader = originalRequest.newBuilder()
      .addHeader("X-Api-Authentication", "intervIEW_TOK3n")
      .build()

    val response = chain.proceed(requestWithAuthenticationHeader)

    return response
  }
}