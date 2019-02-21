package com.dimowner.goodweather.data

interface Prefs {

	fun isFirstRun(): Boolean

	fun firstRunExecuted()

	fun switchTimeFormatt(): Int

	fun switchWindFormat(): Int

	fun switchPressureFormat(): Int

	fun switchTempFormat(): Int

	fun getTempFormat(): Int

	fun getWindFormat(): Int

	fun getPressureFormat(): Int

	fun getTimeFormat(): Int

	fun saveCity(city: String)

	fun getCity(): String

	fun saveLatitude(lat: Double)

	fun getLatitude(): Double

	fun saveLongitude(lng: Double)

	fun getLongitude(): Double
}
