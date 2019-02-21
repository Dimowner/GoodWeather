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

package com.dimowner.goodweather

/**
 * Created on 20.01.2018.
 * @author Dimowner
 */
class AppConstants {
	companion object {
		//TODO: hide from public availability
		const val OPEN_WEATHER_MAP_API_KEY = "c694cf3ed462a56acc43e6fa2037bbbd"

//		const val OPEN_WEATHER_MAP_API_KEY = "09e6be8ca3991509ff0e08494e0238f7"

		const val WEATHER_ICON_URL = "http://openweathermap.org/img/w/"

		const val PNG = ".png"

		const val TEMP_FORMAT_CELSIUS = 1
		const val TEMP_FORMAT_FAHRENHEIT = 2

		const val TIME_FORMAT_24H = 11
		const val TIME_FORMAT_12H = 12

		const val WIND_FORMAT_METER_PER_HOUR = 21
		const val WIND_FORMAT_MILES_PER_HOUR = 22

		const val PRESSURE_FORMAT_PHA = 31
		const val PRESSURE_FORMAT_MM_HG = 32
	}
}
