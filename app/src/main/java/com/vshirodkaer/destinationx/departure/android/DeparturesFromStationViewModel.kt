package com.vshirodkaer.destinationx.departure.android

import android.arch.lifecycle.ViewModel
import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation
import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import com.vshirodkaer.destinationx.util.addTo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*

/**
 * State of UI to be shown on the departures screen.
 */
sealed class DeparturesUiState {
  object Loading : DeparturesUiState()
  object Error : DeparturesUiState()
  data class Success(val departures: List<DeparturesUiModel>) : DeparturesUiState()
}

data class DeparturesUiModel(
  val direction: String,
  val lineNumber: String,
  val departureTime: String
)

/**
 * Representation of the current state of execution of the use case.
 */
private sealed class UseCaseState {
  object Loading : UseCaseState()
  data class Error(val throwable: Throwable) : UseCaseState()
  data class Success(val departures: List<Departure>) : UseCaseState()
}

/**
 * Provides [DeparturesUiState] that clients can bind to.
 * The departures are sorted in ascending order of departure time.
 */
class DeparturesFromStationViewModel(
  private val stationId: Int,
  private val getDeparturesFromStation: GetDeparturesFromStation
) : ViewModel() {

  private val disposables = CompositeDisposable()

  private val useCaseStateProvider = BehaviorSubject.create<UseCaseState>()

  init {
    executeUseCase()
  }

  fun bindToUiState(): Observable<DeparturesUiState> = useCaseStateProvider.map { useCaseState ->
    when (useCaseState) {
      is UseCaseState.Loading -> DeparturesUiState.Loading
      is UseCaseState.Error -> DeparturesUiState.Error
      is UseCaseState.Success -> DeparturesUiState.Success(
        useCaseState.departures
          .sortedBy(::departureTime)
          .map(::toUiModel)
      )
    }
  }

  private fun executeUseCase() {
    getDeparturesFromStation
      .execute(stationId)
      .doOnSubscribe { useCaseStateProvider.onNext(UseCaseState.Loading) }
      .subscribe(::onGetDeparturesSuccess, ::onGetDeparturesFailed, ::onGetDeparturesComplete)
      .addTo(disposables)
  }

  private fun onGetDeparturesSuccess(departures: List<Departure>) {
    useCaseStateProvider.onNext(UseCaseState.Success(departures))
  }

  private fun onGetDeparturesFailed(throwable: Throwable) {
    useCaseStateProvider.onNext(UseCaseState.Error(throwable))
  }

  private fun onGetDeparturesComplete() {
    // Do nothing
  }

  override fun onCleared() {
    super.onCleared()
    useCaseStateProvider.onComplete()
    disposables.dispose()
  }
}


private fun toUiModel(departure: Departure): DeparturesUiModel = DeparturesUiModel(
  direction = departure.direction,
  lineNumber = departure.lineNumber,
  departureTime = run {
    val departureTimeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    departureTimeFormatter.timeZone = TimeZone.getTimeZone(departure.stationTimeZone)
    departureTimeFormatter.format(Date(departure.departureTime))
  }
)

private fun departureTime(departure: Departure): Long = departure.departureTime