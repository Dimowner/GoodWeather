/*
 * Copyright 2018 Dmitriy Ponomarenko
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

import com.dimowner.goodweather.Constants
import com.dimowner.goodweather.data.remote.WeatherApi
import com.dimowner.goodweather.data.remote.model.Weather
import io.reactivex.Single
import timber.log.Timber

class RepositoryImpl(
		val weatherApi: WeatherApi
	) : Repository {

	override fun getWeather(): Single<Array<Weather>> {
		Timber.v("getWeather")
		return weatherApi.getWeather("Kyiv", Constants.OPEN_WEATHER_MAP_API_KEY)
	}
}