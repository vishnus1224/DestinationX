package com.vshirodkaer.destinationx.departure.android

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View.GONE
import android.view.View.VISIBLE
import com.vshirodkaer.destinationx.R
import com.vshirodkaer.destinationx.application.ModuleBasedApplication
import com.vshirodkaer.destinationx.departure.android.adapter.DeparturesFromStationAdapter
import com.vshirodkaer.destinationx.util.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.layout_departures_error.*
import kotlinx.android.synthetic.main.layout_departures_from_station.*
import kotlinx.android.synthetic.main.layout_loading_departures.*

class DeparturesActivity : AppCompatActivity() {

  private val disposables = CompositeDisposable()

  private val departuresAdapter = DeparturesFromStationAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_departures)
    title = getString(R.string.departures_from_station_title)

    initRecycler()

    val departureModule = (application as ModuleBasedApplication).modules.departureModule

    val viewModel = ViewModelProvider(
      this, DeparturesViewModelFactory(10, departureModule.getDeparturesFromStation)
    ).get(DeparturesFromStationViewModel::class.java)

    departures_error_retry_button.setOnClickListener { viewModel.retryGettingDepartures() }

    viewModel.
      bindToUiState()
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(this::renderView)
      .addTo(disposables)
  }

  private fun initRecycler() {
    departures_from_station_recycler.adapter = departuresAdapter
    departures_from_station_recycler.addItemDecoration(
      DividerItemDecoration(this, DividerItemDecoration.VERTICAL).apply {
        setDrawable(ContextCompat.getDrawable(this@DeparturesActivity, R.drawable.departures_recycler_divider)!!)
      }
    )
    departures_from_station_recycler.layoutManager = LinearLayoutManager(this)
  }

  private fun renderView(uiState: DeparturesUiState) = when (uiState) {
    DeparturesUiState.Loading -> {
      hideErrorView()
      hideDepartures()
      showProgressBar()
    }
    DeparturesUiState.Error -> {
      hideProgressBar()
      hideDepartures()
      showErrorView()
    }
    is DeparturesUiState.Success -> {
      hideProgressBar()
      hideErrorView()
      showDepartures(uiState.departures)
    }
  }

  private fun showProgressBar() {
    departures_progress_bar.visibility = VISIBLE
  }

  private fun hideProgressBar() {
    departures_progress_bar.visibility = GONE
  }

  private fun showErrorView() {
    departures_error_view.visibility = VISIBLE
  }

  private fun hideErrorView() {
    departures_error_view.visibility = GONE
  }

  private fun showDepartures(departures: List<DeparturesUiModel>) {
    departures_from_station_recycler.visibility = VISIBLE
    departuresAdapter.showDepartures(departures)
  }

  private fun hideDepartures() {
    departures_from_station_recycler.visibility = GONE
  }

  override fun onDestroy() {
    super.onDestroy()
    disposables.dispose()
  }
}
