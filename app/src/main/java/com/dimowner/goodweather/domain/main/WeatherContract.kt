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

package com.dimowner.goodweather.domain.main

import com.dimowner.goodweather.data.local.room.WeatherEntity
import com.dimowner.goodweather.Contract

interface WeatherContract {

	interface View : Contract.View {

		fun showDate(date: String)

		fun showTemperature(temp: String)

		fun showWind(wind: String)

		fun showPressure(pressure: String)

		fun showHumidity(humidity: String)

		fun showWeatherIcon(url: String)

		fun showWeatherIconRes(resId: Int)

		fun showTwoWeeksWeather(list: List<WeatherEntity>)

		fun setTemperatureFormat(format: Int)
	}

	interface UserActionsListener : Contract.UserActionsListener<WeatherContract.View> {

		fun locate()

		fun updateWeather(type: Int)

		fun updateWeatherTwoWeeks()

		fun updateTemperatureFormat()
	}
}