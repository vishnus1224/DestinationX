package com.vshirodkaer.destinationx.departure.android

import android.content.Intent
import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.PositionAssertions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.vshirodkaer.destinationx.R
import com.vshirodkaer.destinationx.application.ModuleBasedApplication
import com.vshirodkaer.destinationx.departure.module.DepartureModuleForInstrumentationApp
import com.vshirodkaer.destinationx.departure.usecase.entity.Departure
import io.reactivex.Observable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

class DeparturesActivityTest {

  @Rule
  @JvmField
  val activityRule = ActivityTestRule(DeparturesActivity::class.java, true, false)

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
  fun onBindToUiStateReturnsSuccessShowListOfDepartures() {
    injectResponse(getDeparturesSuccess())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyDeparturesRecyclerIsDisplayed()

    verifyErrorViewIsNotDisplayed()
  }

  @Test
  fun onBindToUiStateReturnsFailureShowErrorMessageAndRetryButton() {
    injectResponse(getDeparturesFailed())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyErrorStateIsDisplayed()

    verifyDeparturesRecyclerIsNotDisplayed()
  }

  @Test
  fun onRetryButtonClickUiStateReturnsSuccessShowListOfDepartures() {
    var getDeparturesExecutionCounter = 1
    val response = Observable.defer {
      if (getDeparturesExecutionCounter % 2 == 0) getDeparturesSuccess()
      else getDeparturesFailed()
    }

    injectResponse(response)

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyErrorStateIsDisplayed()

    verifyDeparturesRecyclerIsNotDisplayed()

    getDeparturesExecutionCounter += 1

    clickRetryButton()

    testScheduler.advanceTimeBy(5L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyDeparturesRecyclerIsDisplayed()

    verifyErrorViewIsNotDisplayed()
  }

  @Test
  fun onRetryButtonClickUiStateReturnsFailureShowErrorMessageAndRetryButtonAgain() {
    injectResponse(getDeparturesFailed())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyErrorStateIsDisplayed()

    verifyDeparturesRecyclerIsNotDisplayed()

    clickRetryButton()

    testScheduler.advanceTimeBy(5L, TimeUnit.MILLISECONDS)
    verifyProgressBarIsDisplayed()

    testScheduler.advanceTimeBy(10L, TimeUnit.MILLISECONDS)
    verifyErrorStateIsDisplayed()

    verifyDeparturesRecyclerIsNotDisplayed()
  }

  @Test
  fun lineNumberShouldBeShownBelowDirection() {
    injectResponse(getDeparturesSuccess())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    onView(withText("001")).check(isCompletelyBelow(withText("amazing")))
  }

  @Test
  fun leftEdgesOfLineNumberAndDirectionShouldBeAligned() {
    injectResponse(getDeparturesSuccess())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    onView(withText("001")).check(isLeftAlignedWith(withText("amazing")))
  }

  @Test
  fun directionShouldNotOverlapDepartureTime() {
    injectResponse(getDeparturesSuccess())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    onView(withText("amazing")).check(isCompletelyLeftOf(withText("10:47")))
  }

  @Test
  fun bottomOfDirectionAndDepartureTimeShouldBeAligned() {
    injectResponse(getDeparturesSuccess())

    activityRule.launchActivity(Intent())

    testScheduler.advanceTimeBy(20L, TimeUnit.MILLISECONDS)

    onView(withText("amazing")).check(isBottomAlignedWith(withText("10:47")))
  }

  private fun clickRetryButton() = onView(withId(R.id.departures_error_retry_button)).perform(click())

  private fun verifyErrorStateIsDisplayed() = run {
    verifyErrorViewIsDisplayed()
    verifyErrorMessageIsDisplayed()
    verifyRetryButtonIsDisplayed()
  }

  private fun verifyProgressBarIsDisplayed() = verifyViewIsDisplayed(R.id.departures_progress_bar)

  private fun verifyDeparturesRecyclerIsDisplayed() = verifyViewIsDisplayed(R.id.departures_from_station_recycler)

  private fun verifyDeparturesRecyclerIsNotDisplayed() = verifyViewIsNotDisplayed(R.id.departures_from_station_recycler)

  private fun verifyErrorMessageIsDisplayed() = verifyViewIsDisplayed(R.id.departures_error_message)

  private fun verifyErrorViewIsDisplayed() = verifyViewIsDisplayed(R.id.departures_error_view)

  private fun verifyErrorViewIsNotDisplayed() = verifyViewIsNotDisplayed(R.id.departures_error_view)

  private fun verifyRetryButtonIsDisplayed() = verifyViewIsDisplayed(R.id.departures_error_retry_button)

  private fun verifyViewIsDisplayed(resId: Int) = onView(withId(resId)).check(matches(isDisplayed()))

  private fun verifyViewIsNotDisplayed(resId: Int) = onView(withId(resId)).check(matches(not(isDisplayed())))

  private fun injectResponse(response: Observable<List<Departure>>) {

    val application: ModuleBasedApplication = InstrumentationRegistry.getTargetContext().applicationContext as ModuleBasedApplication

    val departureModule: DepartureModuleForInstrumentationApp =
      application.modules.departureModule as DepartureModuleForInstrumentationApp

    departureModule.departures = response
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

}