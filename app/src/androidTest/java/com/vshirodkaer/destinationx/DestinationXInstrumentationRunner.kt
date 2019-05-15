package com.vshirodkaer.destinationx

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner
import com.vshirodkaer.destinationx.application.DestinationXInstrumentationApplication

class DestinationXInstrumentationRunner : AndroidJUnitRunner() {

  override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
    return super.newApplication(cl, DestinationXInstrumentationApplication::class.java.name, context)
  }

}