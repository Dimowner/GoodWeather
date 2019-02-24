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

import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.data.PrefsImpl
import com.dimowner.goodweather.data.repository.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class WeatherListPresenter(
		private val repository: Repository,
		private val prefs: Prefs) : WeatherListContract.UserActionsListener {

	private var view: WeatherListContract.View? = null

	private val disposable: CompositeDisposable by lazy { CompositeDisposable() }

	override fun bindView(view: WeatherListContract.View) {
		this.view = view

		disposable.add(prefs.subscribePreferenceChanges().subscribe({ key ->
			when (key) {
				PrefsImpl.PREF_KEY_TIME_FORMAT -> view.updateTimeFormat(prefs.getTimeFormat())
				PrefsImpl.PREF_KEY_TEMP_FORMAT -> view.updateTemperatureFormat(prefs.getTempFormat())
			}
		}, { Timber.e(it)}))

		view.updateTimeFormat(prefs.getTimeFormat())
		view.updateTemperatureFormat(prefs.getTempFormat())
	}

	override fun unbindView() {
		this.view = null
		this.disposable.dispose()
	}

	override fun loadWeatherList() {
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
}
