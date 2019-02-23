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

package com.dimowner.goodweather.data.repository

import com.dimowner.goodweather.data.local.LocalRepository
import com.dimowner.goodweather.data.remote.model.WeatherResponse
import com.dimowner.goodweather.data.local.room.WeatherEntity
import com.dimowner.goodweather.data.remote.RemoteRepository
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepositoryImpl(
		private val localRepository: LocalRepository,
		private val remoteRepository: RemoteRepository
) : Repository {

	override fun getWeather(): Single<WeatherResponse> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun getWeatherToday(city: String): Single<WeatherEntity> {
		return remoteRepository.getWeatherToday(city)
				.subscribeOn(Schedulers.io())
				.flatMap { data ->
					localRepository.cacheWeather(data)
					localRepository.getWeatherToday(city).subscribeOn(Schedulers.io())
				}
	}

	override fun subscribeWeatherToday(city: String): Flowable<WeatherEntity> {
		remoteRepository.getWeatherToday(city)
				.subscribeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheWeather(response)
				}, Timber::e)
		return localRepository.subscribeWeatherToday(city)
//				.timeout(20, TimeUnit.SECONDS)
				.subscribeOn(Schedulers.io())
	}

	override fun subscribeWeatherTwoWeeks(city: String): Flowable<List<WeatherEntity>> {
		remoteRepository.subscribeWeatherTwoWeeks(city)
				.subscribeOn(Schedulers.io())
				.subscribe({ response ->
					localRepository.cacheWeather(response)
				}, Timber::e)
		return localRepository.subscribeWeatherTwoWeeks(city)
				.subscribeOn(Schedulers.io())
	}

	override fun cacheWeather(entity: List<WeatherEntity>) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun cacheWeather(entity: WeatherEntity) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
