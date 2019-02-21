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

package com.dimowner.goodweather.app.location

import android.os.Bundle
import com.dimowner.goodweather.data.Prefs
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class LocationPresenter(
		private val locationDataModel: LocationProvider,
		private val prefs: Prefs
) : LocationContract.UserActionsListener {

	private var view: LocationContract.View? = null
	private var isCitySelected: Boolean = false

	override fun bindView(view: LocationContract.View) {
		this.view = view
		locationDataModel.connect(
				object : GoogleApiClient.ConnectionCallbacks {
					override fun onConnected(bundle: Bundle?) {
						Timber.v("onConnected")
					}

					override fun onConnectionSuspended(i: Int) {
						Timber.v("onConnectionSuspended")
					}
				}
		)

		view.showSelectedCity(prefs.getCity())
		if (prefs.getLatitude() != 0.0 && prefs.getLongitude() != 0.0) {
			view.showMapMarker(Location("", prefs.getLatitude(), prefs.getLongitude()))
		}
	}

	override fun unbindView() {
		view = null
		locationDataModel.disconnect()
	}

	override fun locate() {
		locationDataModel.findLocation()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ location ->
					view?.showMapMarker(location)
					prefs.saveLatitude(location.lat)
					prefs.saveLongitude(location.lng)
					isCitySelected = true
					view?.showSelectedCity(location.address)
					prefs.saveCity(location.address)
				}, { Timber.e(it) })
	}

	override fun findCity(city: String) {
		if (!isCitySelected) {
			locationDataModel.findPlace(city)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe({
						view?.showPredictions(it)
					}, { Timber.e(it) })
		}
	}

	override fun findLocationForCity(city: String) {
		isCitySelected = true
		view?.showSelectedCity(city)
		prefs.saveCity(city)
		locationDataModel.findLocationForCityName(city)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({ location ->
					view?.showMapMarker(location)
					prefs.saveLatitude(location.lat)
					prefs.saveLongitude(location.lng)
				}, { t ->
					Timber.e(t)
					view?.showError(if (t.message != null) t.message!! else "Error on find location for city")
				})
	}

	override fun setCitySelected(b: Boolean) {
		isCitySelected = b
	}
}