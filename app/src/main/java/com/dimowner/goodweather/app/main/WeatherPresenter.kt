/*
 *  Copyright 2019 Dmitriy Ponomarenko
 *
 *  Licensed to the Apache Software Foundation (ASF) under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership. The ASF licenses this
 *  file to you under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License. You may obtain a copy of
 *  the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  License for the specific language governing permissions and limitations under
 *  the License.
 */

package com.dimowner.goodweather.app.main

import android.content.Context
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.data.PrefsImpl
import com.dimowner.goodweather.data.local.room.WeatherEntity
import com.dimowner.goodweather.data.repository.Repository
import com.dimowner.goodweather.util.TimeUtils
import com.dimowner.goodweather.util.WeatherUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class WeatherPresenter(
		private val repository: Repository,
		private val prefs: Prefs,
		private val context: Context) : WeatherContract.UserActionsListener {

	private var view: WeatherContract.View? = null

	private val disposable: CompositeDisposable by lazy { CompositeDisposable() }

	private var weatherEntity: WeatherEntity? = null

	override fun bindView(view: WeatherContract.View) {
		this.view = view

		disposable.add(prefs.subscribePreferenceChanges().subscribe({ key ->
			when (key) {
				PrefsImpl.PREF_KEY_TIME_FORMAT -> view.showDate(TimeUtils.formatTime((weatherEntity?.dt ?: 0) * 1000, prefs.getTimeFormat()))
				PrefsImpl.PREF_KEY_TEMP_FORMAT -> view.showTemperature(WeatherUtils.formatTemp(weatherEntity?.temp ?: 0f, prefs.getTempFormat(), context))
				PrefsImpl.PREF_KEY_WIND_FORMAT -> view.showWind(WeatherUtils.formatWind(weatherEntity?.wind ?: 0f, prefs.getWindFormat(), context))
				PrefsImpl.PREF_KEY_PRESSURE_FORMAT -> view.showPressure(WeatherUtils.formatPressure(weatherEntity?.pressure ?: 0f, prefs.getPressureFormat(), context))
			}
		}, { Timber.e(it)}))
	}

	override fun unbindView() {
		this.view = null
		this.disposable.dispose()
	}

	override fun locate() {
		//TODO: waiting for implementation
	}

	override fun updateWeatherTwoWeeks() {
		view?.showProgress()
		disposable.add(repository.subscribeWeatherTwoWeeks(prefs.getCity())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ d ->
					view?.showTwoWeeksWeather(d)
					view?.hideProgress()
				}, {
					view?.hideProgress()
					Timber.e(it)
					view?.showError(it.message!!)
				}))
	}

	override fun updateTemperatureFormat() {
		view?.setTemperatureFormat(prefs.getTempFormat())
	}

	override fun updateWeather() {
		view?.showProgress()
		disposable.add(repository.subscribeWeatherToday(prefs.getCity())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({
					showData(it)
					view?.hideProgress()
				}, {
					view?.hideProgress()
					Timber.e(it)
					view?.showError(it.message!!)
				}))
	}

	private fun showData(entity: WeatherEntity) {
		weatherEntity = entity

		view?.showDate(TimeUtils.formatTime(entity.dt * 1000, prefs.getTimeFormat()))
		view?.showTemperature(WeatherUtils.formatTemp(entity.temp, prefs.getTempFormat(), context))

		view?.showWind(WeatherUtils.formatWind(entity.wind, prefs.getWindFormat(), context))
		view?.showHumidity(WeatherUtils.formatHumidity(entity.humidity, context))
		view?.showPressure(WeatherUtils.formatPressure(entity.pressure, prefs.getPressureFormat(), context))

//		view?.showWeatherIcon(Constants.WEATHER_ICON_URL + entity.icon + Constants.PNG)
		view?.showWeatherIconRes(WeatherUtils.weatherIconCodeToResource(entity.icon))
	}
}