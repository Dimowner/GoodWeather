/*
 *  Copyright 2018 Dmitriy Ponomarenko
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

package com.dimowner.goodweather.data.remote.model

import com.google.gson.annotations.SerializedName

data class WeatherListItem(
		@SerializedName("dt")
		val dt: Long,
		@SerializedName("temp")
		val temp: Temp,
		@SerializedName("pressure")
		val pressure: Float,
		@SerializedName("humidity")
		val humidity: Float,
		@SerializedName("weather")
		val weather: Array<Weather>,
		@SerializedName("speed")
		val speed: Float,
		@SerializedName("deg")
		val deg: Float,
		@SerializedName("clouds")
		val clouds: Float,
		@SerializedName("snow")
		val snow: Float
		)