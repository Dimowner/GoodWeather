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

package com.dimowner.goodweather.data.remote

import com.dimowner.goodweather.data.remote.model.WeatherListResponse
import com.dimowner.goodweather.data.remote.model.WeatherResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created on 21.01.2018.
 * @author Dimowner
 */
interface WeatherApi {

	@GET("weather")
	fun getWeather(@Query("q") city: String, @Query("APPID") apiKey: String): Single<WeatherResponse>

	@GET("weather")
	fun getWeather(@Query("lat") lat: String, @Query("lon") lon: String, @Query("APPID") apiKey: String): Single<WeatherResponse>

	@GET("forecast/daily")
	fun getWeatherFewDays(
			@Query("q") city: String,
			@Query("cnt") cnt: Int,
			@Query("APPID") apiKey: String): Single<WeatherListResponse>

	@GET("forecast/daily")
	fun getWeatherFewDays(
			@Query("lat") lat: String,
			@Query("lon") lon: String,
			@Query("cnt") cnt: Int,
			@Query("APPID") apiKey: String): Single<WeatherListResponse>
}