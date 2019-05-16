# DestinationX

A simple android app that shows departures from a given station.
On the android side of things, ViewModel from android architecture components form the basis of the app.
RxJava is prevalent throughout the main application architechture and is heavily used for writing both UI and unit tests.

The app is split into 5 packages namely : 
# application - contains the android app and its required modules.
# departure - everything related to departures resides here.
# network - the core networking required for the app is included in this package.
# timetable - contains everything related to the station timetable.
# util - common utilities/helpers/sugar belongs here.
 
The *departure* and *network* packages expose *modules* that can be used by *modules* from other parts of the application to communicate with each other.
Each of these modules have an equivalent counterpart in the Instrumentation app for providing fake data.
