package com.vshirodkaer.destinationx.departure.android

import com.vshirodkaer.destinationx.departure.usecase.GetDeparturesFromStation
import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class DeparturesFromStationViewModelTest {

  private val testScheduler = TestScheduler()

  @Before
  fun setup() {
    RxJavaPlugins.setComputationSchedulerHandler { testScheduler }
  }

  @After
  fun cleanup() {
    RxJavaPlugins.reset()
  }

  @Test
  fun `on bind to ui state, get departures succeeds, should get loading followed by all the departures`() {
    // Given
    val viewModel = createViewModel(createGetDeparturesFromStation(getDeparturesSuccess()))

    // When
    val testObserver = viewModel.bindToUiState().test()

    // Then
    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    testObserver
      .assertValueCount(2)
      .assertNoErrors()
      .assertNotComplete()
      .assertValueAt(0, DeparturesUiState.Loading)
      .assertNever(DeparturesUiState.Error)

    val successState = testObserver.values()[1]
    assertTrue(successState is DeparturesUiState.Success)
    val departures = (successState as DeparturesUiState.Success).departures
    assertEquals(4, departures.size)
  }

  @Test
  fun `on bind to ui state, get departures succeeds, should get departures sorted by their departure time according to timezone of the station`() {
    // Given
    val viewModel = createViewModel(createGetDeparturesFromStation(getDeparturesSuccess()))

    // When
    val testObserver = viewModel.bindToUiState().test()

    // Then
    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    val expectedDeparturesUiModel = listOf(bondUiModel, superbUiModel, amazingUiModel, bestUiModel)

    testObserver
      .assertValueCount(2)
      .assertNoErrors()
      .assertNotComplete()
      .assertValueAt(0, DeparturesUiState.Loading)
      .assertValueAt(1, DeparturesUiState.Success(expectedDeparturesUiModel))
      .assertNever(DeparturesUiState.Error)
  }

  @Test
  fun `on bind to ui state, get departures fails, should get loading followed by error state`() {
    // Given
    val viewModel = createViewModel(createGetDeparturesFromStation(getDeparturesFailed()))

    // When
    val testObserver = viewModel.bindToUiState().test()

    // Then
    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    testObserver
      .assertValueCount(2)
      .assertNoErrors()
      .assertNotComplete()
      .assertValueAt(0, DeparturesUiState.Loading)
      .assertValueAt(1, DeparturesUiState.Error)
      .assertNever(DeparturesUiState.Success(listOf(bondUiModel, superbUiModel, amazingUiModel, bestUiModel)))
  }

  @Test
  fun `on configuration change, should execute get departures only once`() {
    // Given
    var getDeparturesExecutionCount = 0
    val getDeparturesResponse = Observable.just(allDepartures).doOnSubscribe { getDeparturesExecutionCount += 1 }

    val viewModel = createViewModel(createGetDeparturesFromStation(getDeparturesResponse))

    // When
    viewModel.bindToUiState().test()
    viewModel.bindToUiState().test()
    viewModel.bindToUiState().test()
    viewModel.bindToUiState().test()

    // Then
    testScheduler.advanceTimeBy(100L, TimeUnit.MILLISECONDS)

    assertEquals(1, getDeparturesExecutionCount)
  }

  @Test
  fun `on retry click, get departures fails, should get error state`() {
    // Given
    val viewModel = createViewModel(createGetDeparturesFromStation(getDeparturesFailed()))

    // When
    val testObserver = viewModel.bindToUiState().test()

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    viewModel.retryGettingDepartures()

    // Then
    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    testObserver
      .assertValueCount(4)
      .assertNotComplete()
      .assertNoErrors()
      .assertValueAt(0, DeparturesUiState.Loading)
      .assertValueAt(1, DeparturesUiState.Error)
      .assertValueAt(2, DeparturesUiState.Loading)
      .assertValueAt(3, DeparturesUiState.Error)
      .assertNever(DeparturesUiState.Success(listOf(bondUiModel, superbUiModel, amazingUiModel, bestUiModel)))
  }

  @Test
  fun `on retry click, get departures succeeds, should get list of departures`() {
    // Given
    var getDeparturesExecutionCount = 1
    val response = Observable.defer {
      if (getDeparturesExecutionCount % 2 == 0) getDeparturesSuccess()
      else getDeparturesFailed()
    }

    val viewModel = createViewModel(createGetDeparturesFromStation(response))

    // When
    val testObserver = viewModel.bindToUiState().test()

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    getDeparturesExecutionCount += 1

    viewModel.retryGettingDepartures()

    // Then
    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    testObserver
      .assertValueCount(4)
      .assertNotComplete()
      .assertNoErrors()
      .assertValueAt(0, DeparturesUiState.Loading)
      .assertValueAt(1, DeparturesUiState.Error)
      .assertValueAt(2, DeparturesUiState.Loading)
      .assertValueAt(3, DeparturesUiState.Success(listOf(bondUiModel, superbUiModel, amazingUiModel, bestUiModel)))
  }

  @Test
  fun `on init and retry click, should execute get departures`() {
    // Given
    var getDeparturesExecutionCount = 0
    val response = Observable.just(allDepartures).doOnSubscribe { getDeparturesExecutionCount += 1 }

    val viewModel = createViewModel(createGetDeparturesFromStation(response))

    // When
    viewModel.bindToUiState().test()

    viewModel.retryGettingDepartures()
    viewModel.retryGettingDepartures()
    viewModel.retryGettingDepartures()
    viewModel.retryGettingDepartures()

    // Then
    assertEquals(5, getDeparturesExecutionCount)
  }

  @Test
  fun `when response returns invalid timezone, departure time should be shown at GMT`() {
    // Given
    val response = Observable.just(
      listOf(
        Departure("545L", "London", 1557740330000, "KIW"),
        Departure("8BNA", "Zurich", 1557740530000, "QOP"),
        Departure("PQIN", "New Orleans", 1557740960000, "NUR")
      )
    )

    val viewModel = createViewModel(createGetDeparturesFromStation(response))

    // When
    val testObserver = viewModel.bindToUiState().test()

    // Then
    val departuresUiState= testObserver.values()[0]

    val expectedDepartures = listOf(
      DeparturesUiModel("London", "545L", "09:38"),
      DeparturesUiModel("Zurich", "8BNA", "09:42"),
      DeparturesUiModel("New Orleans", "PQIN", "09:49")
    )

    assertTrue(departuresUiState is DeparturesUiState.Success)

    val actualDepartures = (departuresUiState as DeparturesUiState.Success).departures

    assertEquals(expectedDepartures, actualDepartures)
  }

  private val may132019NineThirtyEightAmAtGmt = 1557740330000
  private val may132019NineFortyTwoAmAtGmt = 1557740530000
  private val may132019NineFortySevenAmAtGmt = 1557740830000
  private val may132019NineFortyNineAmAtGmt = 1557740960000

  private val amazingDeparture = Departure("001", "amazing", may132019NineFortySevenAmAtGmt, "GMT+01:00")
  private val bestDeparture = Departure("002", "best", may132019NineFortyNineAmAtGmt, "GMT+06:00")
  private val bondDeparture = Departure("007", "bond", may132019NineThirtyEightAmAtGmt, "GMT+07:00")
  private val superbDeparture = Departure("003", "superb", may132019NineFortyTwoAmAtGmt, "GMT+09:00")

  private val allDepartures = listOf(amazingDeparture, bestDeparture, bondDeparture, superbDeparture)

  private fun getDeparturesSuccess() = Observable.timer(15L, TimeUnit.MILLISECONDS, testScheduler).map { allDepartures }

  private fun getDeparturesFailed() = Observable.timer(15L, TimeUnit.MILLISECONDS, testScheduler)
    .flatMap { Observable.error<List<Departure>>(Throwable("Failed to get departures")) }

  private fun createViewModel(getDeparturesFromStation: GetDeparturesFromStation) = DeparturesFromStationViewModel(
    1441, getDeparturesFromStation
  )

  private fun createGetDeparturesFromStation(response: Observable<List<Departure>>) = object : GetDeparturesFromStation {
    override fun execute(stationId: Int): Observable<List<Departure>>  = response
  }

  private val amazingUiModel = DeparturesUiModel("amazing", "001", "10:47")
  private val bestUiModel = DeparturesUiModel("best", "002", "15:49")
  private val bondUiModel = DeparturesUiModel("bond", "007", "16:38")
  private val superbUiModel = DeparturesUiModel("superb", "003", "18:42")
}