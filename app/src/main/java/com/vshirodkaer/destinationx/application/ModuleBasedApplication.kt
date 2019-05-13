package com.vshirodkaer.destinationx.application

import android.app.Application

abstract class ModuleBasedApplication : Application() {
  abstract val modules: Modules
}