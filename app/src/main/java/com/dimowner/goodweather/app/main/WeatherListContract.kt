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

import com.dimowner.goodweather.app.Contract
import com.dimowner.goodweather.data.local.room.WeatherEntity


interface WeatherListContract {

	interface View : Contract.View {

		fun showTwoWeeksWeather(list: List<WeatherEntity>)

		fun updateTimeFormat(format: Int)

		fun updateTemperatureFormat(format: Int)
	}

	interface UserActionsListener : Contract.UserActionsListener<View> {

		fun loadWeatherList()
	}
}