/*
 * Copyright 2019 Dmitriy Ponomarenko
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor
 * license agreements. See the NOTICE file distributed with this work for
 * additional information regarding copyright ownership. The ASF licenses this
 * file to you under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.dimowner.goodweather.data

import android.content.Context
import android.content.SharedPreferences
import com.dimowner.goodweather.AppConstants
import com.ironz.binaryprefs.BinaryPreferencesBuilder
import com.ironz.binaryprefs.Preferences
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import kotlin.properties.Delegates

/**
 * Created on 16.01.2018.
 * @author Dimowner
 */
class PrefsImpl constructor(context: Context) : Prefs {

	companion object {
		const val PREF_NAME = "com.dimowner.goodweather.data.Prefs"

		const val PREF_KEY_IS_FIRST_RUN = "is_first_run"
		const val PREF_KEY_TEMP_FORMAT = "temp_format"
		const val PREF_KEY_WIND_FORMAT = "wind_format"
		const val PREF_KEY_PRESSURE_FORMAT = "pressure_format"
		const val PREF_KEY_TIME_FORMAT = "time_format"
		const val PREF_KEY_CITY = "city"
		const val PREF_KEY_LATITUDE = "latitude"
		const val PREF_KEY_LONGITUDE = "longitude"
		const val PREF_KEY_INITIAL_SETTINGS_APPLIED = "is_initial_settings_applied"
		const val PREF_KEY_LOCATION_SELECTED = "is_location_selected"
		const val PREF_KEY_IS_WEATHER_BY_COORDINATES = "is_weather_by_coordinates"
	}

//	private var preferences: SharedPreferences by Delegates.notNull()
	private var preferences: Preferences by Delegates.notNull()
	private var flowable: Flowable<String> by Delegates.notNull()

	init {
//		this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
		this.preferences = BinaryPreferencesBuilder(context)
				.name(PREF_NAME)
				.build()

		flowable = Flowable.create({
			preferences.registerOnSharedPreferenceChangeListener { sharedPrefs, key ->
				it.onNext(key)
			}
		}, BackpressureStrategy.LATEST)
	}

	override fun isFirstRun(): Boolean {
		return !preferences.contains(PREF_KEY_IS_FIRST_RUN) || preferences.getBoolean(PREF_KEY_IS_FIRST_RUN, false)
	}

	override fun firstRunExecuted() {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_IS_FIRST_RUN, false)
		editor.apply()
	}

	override fun switchTimeFormat(): Int {
		if (preferences.getInt(PREF_KEY_TIME_FORMAT, AppConstants.TIME_FORMAT_24H) == AppConstants.TIME_FORMAT_24H) {
			preferences.edit().putInt(PREF_KEY_TIME_FORMAT, AppConstants.TIME_FORMAT_12H).apply()
			return AppConstants.TIME_FORMAT_12H
		} else {
			preferences.edit().putInt(PREF_KEY_TIME_FORMAT, AppConstants.TIME_FORMAT_24H).apply()
			return AppConstants.TIME_FORMAT_24H
		}
	}

	override fun switchWindFormat(): Int {
		if (preferences.getInt(PREF_KEY_WIND_FORMAT, AppConstants.WIND_FORMAT_METER_PER_HOUR) == AppConstants.WIND_FORMAT_METER_PER_HOUR) {
			preferences.edit().putInt(PREF_KEY_WIND_FORMAT, AppConstants.WIND_FORMAT_MILES_PER_HOUR).apply()
			return AppConstants.WIND_FORMAT_MILES_PER_HOUR
		} else {
			preferences.edit().putInt(PREF_KEY_WIND_FORMAT, AppConstants.WIND_FORMAT_METER_PER_HOUR).apply()
			return AppConstants.WIND_FORMAT_METER_PER_HOUR
		}
	}

	override fun subscribePreferenceChanges(): Flowable<String> {
		return flowable
	}

	override fun switchPressureFormat(): Int {
		if (preferences.getInt(PREF_KEY_PRESSURE_FORMAT, AppConstants.PRESSURE_FORMAT_PHA) == AppConstants.PRESSURE_FORMAT_MM_HG) {
			preferences.edit().putInt(PREF_KEY_PRESSURE_FORMAT, AppConstants.PRESSURE_FORMAT_PHA).apply()
			return AppConstants.PRESSURE_FORMAT_PHA
		} else {
			preferences.edit().putInt(PREF_KEY_PRESSURE_FORMAT, AppConstants.PRESSURE_FORMAT_MM_HG).apply()
			return AppConstants.PRESSURE_FORMAT_MM_HG
		}
	}

	override fun switchTempFormat(): Int {
		if (preferences.getInt(PREF_KEY_TEMP_FORMAT, AppConstants.TEMP_FORMAT_CELSIUS) == AppConstants.TEMP_FORMAT_CELSIUS) {
			preferences.edit().putInt(PREF_KEY_TEMP_FORMAT, AppConstants.TEMP_FORMAT_FAHRENHEIT).apply()
			return AppConstants.TEMP_FORMAT_FAHRENHEIT
		} else {
			preferences.edit().putInt(PREF_KEY_TEMP_FORMAT, AppConstants.TEMP_FORMAT_CELSIUS).apply()
			return AppConstants.TEMP_FORMAT_CELSIUS
		}
	}

	override fun getTempFormat(): Int {
		return preferences.getInt(PREF_KEY_TEMP_FORMAT, AppConstants.TEMP_FORMAT_CELSIUS)
	}

	override fun getWindFormat(): Int {
		return preferences.getInt(PREF_KEY_WIND_FORMAT, AppConstants.WIND_FORMAT_METER_PER_HOUR)
	}

	override fun getPressureFormat(): Int {
		return preferences.getInt(PREF_KEY_PRESSURE_FORMAT, AppConstants.PRESSURE_FORMAT_PHA)
	}

	override fun getTimeFormat(): Int {
		return preferences.getInt(PREF_KEY_TIME_FORMAT, AppConstants.TIME_FORMAT_24H)
	}

	override fun saveCity(city: String) {
		preferences.edit().putString(PREF_KEY_CITY, city).apply()
	}

	override fun getCity(): String {
		return preferences.getString(PREF_KEY_CITY, "") ?: ""
	}

	override fun isWeatherByCoordinates(): Boolean {
		return preferences.getBoolean(PREF_KEY_IS_WEATHER_BY_COORDINATES, false)
	}

	override fun setWeatherByCoordinates(b: Boolean) {
		preferences.edit().putBoolean(PREF_KEY_IS_WEATHER_BY_COORDINATES, b).apply()
	}

	override fun saveLatitude(lat: Double) {
		preferences.edit().putLong(PREF_KEY_LATITUDE, java.lang.Double.doubleToRawLongBits(lat)).apply()
	}

	override fun getLatitude(): Double {
		return java.lang.Double.longBitsToDouble(preferences.getLong(PREF_KEY_LATITUDE, 0))
	}

	override fun saveLongitude(lng: Double) {
		preferences.edit().putLong(PREF_KEY_LONGITUDE, java.lang.Double.doubleToRawLongBits(lng)).apply()
	}

	override fun getLongitude(): Double {
		return java.lang.Double.longBitsToDouble(preferences.getLong(PREF_KEY_LONGITUDE, 0))
	}

	override fun isInitialSettingApplied(): Boolean {
		return preferences.contains(PREF_KEY_INITIAL_SETTINGS_APPLIED) || preferences.getBoolean(PREF_KEY_INITIAL_SETTINGS_APPLIED, false)
	}

	override fun applyInitialSettings() {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_INITIAL_SETTINGS_APPLIED, false)
		editor.apply()
	}

	override fun isLocationSelected(): Boolean {
		return preferences.contains(PREF_KEY_LOCATION_SELECTED) || preferences.getBoolean(PREF_KEY_LOCATION_SELECTED, false)
	}

	override fun setLocationSelected() {
		val editor = preferences.edit()
		editor.putBoolean(PREF_KEY_LOCATION_SELECTED, false)
		editor.apply()
	}
}
