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

import android.content.Context
import android.os.Bundle
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.places.PlacesProvider
import com.dimowner.goodweather.util.round
import com.google.android.gms.common.api.GoogleApiClient
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

class LocationPresenter(
		private val placesProvider: PlacesProvider,
		private val prefs: Prefs
) : LocationContract.UserActionsListener {

	private val MAP_ZOOM = 11f
	private var view: LocationContract.View? = null
	private var isCitySelected: Boolean = false
	private val compositeDisposable: CompositeDisposable = CompositeDisposable()

	override fun bindView(view: LocationContract.View) {
		this.view = view
		placesProvider.connect(
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
			view.showMapMarker(prefs.getLatitude(), prefs.getLongitude())
			view.animateCamera(prefs.getLatitude(), prefs.getLongitude(), MAP_ZOOM)
		}
		view.enableButtonApply(false)
	}

	override fun unbindView() {
		view = null
		compositeDisposable.clear()
		placesProvider.disconnect()
	}

	override fun locate() {
		compositeDisposable.add(
				placesProvider.findCurrentLocation()
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe({ location ->
							isCitySelected = true
							saveLocation(location.address, location.lat, location.lng)
							view?.showMapMarker(location.lat, location.lng)
							view?.animateCamera(prefs.getLatitude(), prefs.getLongitude(), MAP_ZOOM)
							view?.showPredictions(emptyList())
							val city = prefs.getCity()
							view?.showSelectedCity(city)
							view?.enableButtonApply(city.isNotBlank())
						}, { Timber.e(it) }))
	}

	override fun findCity(city: String) {
		if (!isCitySelected) {
			compositeDisposable.add(
					placesProvider.findPlaceRx(city)
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe({
								view?.showPredictions(it)
							}, { Timber.e(it) }))
		}
	}

	override fun findCity(context: Context, lat: Double, lng: Double) {
		compositeDisposable.add(
				placesProvider.fromLocationRx(context, lat, lng)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe({ location ->
							Timber.v("Locations = %s", location.toString())
							isCitySelected = true
							saveLocation(location.address, location.lat, location.lng)
							view?.showMapMarker(location.lat, location.lng)
							view?.showPredictions(emptyList())
							val city = prefs.getCity()
							view?.showSelectedCity(city)
							view?.enableButtonApply(city.isNotBlank())
						}, { Timber.e(it) }))
	}

	override fun findLocationForCity(city: String) {
		isCitySelected = true
		view?.showSelectedCity(city)
		compositeDisposable.add(
				placesProvider.findLocationForCityName(city)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe({ location ->
							view?.showMapMarker(location.lat, location.lng)
							view?.animateCamera(location.lat, location.lng, MAP_ZOOM)
							saveLocation(location.address, location.lat, location.lng)
							view?.enableButtonApply(prefs.getCity().isNotBlank())
						}, { t ->
							Timber.e(t)
							view?.showError(if (t.message != null) t.message!! else "Error on find location for city")
						}))
	}

	override fun setLocationSelected() {
		if (!prefs.isLocationSelected()) {
			prefs.setLocationSelected()
		}
	}

	override fun setCitySelected(b: Boolean) {
		isCitySelected = b
	}

	private fun saveLocation(city: String, lat: Double, lng: Double) {
		prefs.saveLatitude(lat)
		prefs.saveLongitude(lng)
		if (city.isNotBlank()) {
			prefs.saveCity(city)
			prefs.setWeatherByCoordinates(false)
		} else {
			prefs.saveCity(lat.round() + ", " + lng.round())
			prefs.setWeatherByCoordinates(true)
		}
	}

}
