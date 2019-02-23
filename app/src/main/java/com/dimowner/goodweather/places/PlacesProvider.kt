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

package com.dimowner.goodweather.places

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import com.google.android.gms.location.places.Places
import com.patloew.rxlocation.RxLocation
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

class PlacesProvider(private val context: Context) {

	private val TIME_OUT_LOCATION: Long = 120
	private val TIME_OUT_PLACES: Long = 45

	private var filter: AutocompleteFilter = AutocompleteFilter.Builder()
			.setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
			.build()

	private var buffer: AutocompletePredictionBuffer? = null
	private var disposable: Disposable? = null
	private var googleApi: GoogleApiClient? = null

	fun connect(callbacks: GoogleApiClient.ConnectionCallbacks) {
		if (googleApi != null) {
			if (!googleApi!!.isConnected || !googleApi!!.isConnecting) googleApi!!.reconnect()
			return
		}

		googleApi = GoogleApiClient.Builder(context)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(callbacks)
				.addOnConnectionFailedListener { result ->
					Timber.e("Error on connect to places services: code: %s, message: %s",
							result.errorMessage, result.errorCode)
					var msg: String? = null
					when (result.errorCode) {
						ConnectionResult.SUCCESS -> msg = "Success"
						ConnectionResult.SERVICE_MISSING -> msg = "Service missing"
						ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED -> msg = "Service version update required"
						ConnectionResult.SERVICE_DISABLED -> msg = "Service disabled"
						ConnectionResult.SIGN_IN_REQUIRED -> msg = "Sign in required"
						ConnectionResult.INVALID_ACCOUNT -> msg = "Invalid account"
						ConnectionResult.RESOLUTION_REQUIRED -> msg = "Resolution required"
						ConnectionResult.NETWORK_ERROR -> msg = "Network error"
						ConnectionResult.INTERNAL_ERROR -> msg = "Internal error"
						ConnectionResult.SERVICE_INVALID -> msg = "Service invalid"
						ConnectionResult.DEVELOPER_ERROR -> msg = "Developer error"
						ConnectionResult.LICENSE_CHECK_FAILED -> msg = "Licence check failed"
						ConnectionResult.CANCELED -> msg = "Canceled"
						ConnectionResult.TIMEOUT -> msg = "Timeout"
						ConnectionResult.INTERRUPTED -> msg = "Interrupted"
						ConnectionResult.API_UNAVAILABLE -> msg = "Api unavailable"
						ConnectionResult.SIGN_IN_FAILED -> msg = "Sign in failed"
						ConnectionResult.SERVICE_UPDATING -> msg = "Service updating"
						ConnectionResult.SERVICE_MISSING_PERMISSION -> msg = "Service missing permission"
						ConnectionResult.RESTRICTED_PROFILE -> msg = "Restricted profile"
					}
					Timber.e(msg ?: "")
				}
				.build()

		googleApi!!.connect()
	}

	fun disconnect() {
		googleApi?.disconnect()
		googleApi = null
	}

	@SuppressLint("MissingPermission")
	fun findCurrentLocation(): Maybe<Location> {
		val rxLocation = RxLocation(context)
		val locationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(5000)
		return rxLocation
				.settings()
				.checkAndHandleResolution(locationRequest, TIME_OUT_LOCATION, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
				.flatMapMaybe { aBoolean ->
					if (aBoolean) {
						rxLocation.location()
								.updates(locationRequest)
								.subscribeOn(Schedulers.io())
								.timeout(TIME_OUT_LOCATION, TimeUnit.SECONDS)
								.firstElement()
					} else {
						Maybe.empty()
					}
				}
				.map { location ->
					val list = fromLocation(context, location.latitude, location.longitude)
					if (list.isNotEmpty()) {
						Location(list[0].locality, location.latitude, location.longitude)
					} else {
						Location("", location.latitude, location.longitude)
					}

				}.subscribeOn(Schedulers.io())
	}

	fun findPlaceRx(address: String): Single<List<String>> {

		disposable?.dispose()
		if (address.isEmpty()) {
			return Single.just(emptyList())
		}

		return Single.fromCallable { findPlace(address) }
					.doOnSubscribe { disposable -> this.disposable = disposable }
					.subscribeOn(Schedulers.io())
	}

	fun findPlace(address: String): List<String> {
		if (address.isEmpty()) {
			return emptyList()
		}

		val result = Places.GeoDataApi.getAutocompletePredictions(googleApi!!, address, null, filter)

		val predictions = ArrayList<AutocompletePrediction>()
		val buffer = result.await(TIME_OUT_PLACES, TimeUnit.SECONDS)

		if (this.buffer != null) this.buffer!!.release()
		this.buffer = buffer

		for (autocompletePrediction in buffer) {
			predictions.add(autocompletePrediction)
		}
		return PlacesMapper.predictionsToList(predictions)
	}

	fun fromLocationRx(context: Context, lat: Double, lng: Double): Single<Location> {
		disposable?.dispose()

		return Single.fromCallable { fromLocation(context, lat, lng) }
				.map {
					if (it.isNotEmpty()) {
						Location(it[0].locality ?: "", lat, lng)
					} else {
						Location("", lat, lng)
					}
				}
				.doOnSubscribe { disposable -> this.disposable = disposable }
				.subscribeOn(Schedulers.io())
	}

	fun fromLocation(context: Context, lat: Double, lng: Double): List<Address> {
		if (Geocoder.isPresent()) {
			val geocoder = Geocoder(context)
			return try {
				geocoder.getFromLocation(lat, lng, 1)
			} catch (e: IOException) {
				Timber.e(e)
				emptyList()
			}
		} else {
			Timber.e("Geocoder is not present")
			return emptyList()
		}
	}

	fun findLocationForCityName(city: String): Single<Location> {
		return Single.fromCallable {
			if (Geocoder.isPresent()) {
				val geocoder = Geocoder(context)
				try {
					val result: List<Address> = geocoder.getFromLocationName(city, 1)
					if (result.isNotEmpty()) {
						Location(result[0].locality ?: "", result[0].latitude, result[0].longitude)
					} else {
						Timber.e("Nothing found")
						Location("", 0.0, 0.0)
					}
				} catch (e: IOException) {
					Timber.e(e)
					Location("", 0.0, 0.0)
				}
			} else {
				Timber.e("Geocoder is not present")
				Location("", 0.0, 0.0)
			}
		}.subscribeOn(Schedulers.io())
	}
}
