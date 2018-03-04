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

package com.dimowner.goodweather.presentation.ui

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.R
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.data.repository.Repository
import com.dimowner.goodweather.util.TimeUtils
import com.dimowner.goodweather.util.WeatherUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

	//TODO: Fix TouchLayout move child only when touch it.
	//TODO: Show progress bar in cloud
	//TODO: Swipe sun to update data
	//TODO: After start scale up icon
	//TODO: Do not request weather from server more often than 5 min

	@Inject lateinit var prefs: Prefs

	@Inject lateinit var repository: Repository

	lateinit var disposable: Disposable

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		Timber.v("onCreate")
//		toast("Hello world")

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		Timber.v("isFirsRun = " + prefs.isFirstRun())
//		prefs.firstRunExecuted()
//		Timber.v("isFirsRun = " + prefs.isFirstRun())

		Timber.v("getWeather")
		disposable = repository.getWeather()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe({
					Timber.v("WeatherResponse $it")

					txtTemp.text = WeatherUtils.formatTemp(it.main.temp).toString()
					txtDate.text = TimeUtils.formatTime(it.dt*1000)

					txtWind.text = getString(R.string.wind_val, it.wind.speed)
					txtHumidity.text = getString(R.string.humidity_val, it.main.humidity)
					txtPressure.text = getString(R.string.pressure_val, it.main.pressure)

				},{Timber.e(it)})
	}

	fun AppCompatActivity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
		Toast.makeText(applicationContext, message, duration).show()
	}

	override fun onPause() {
		super.onPause()
		disposable.dispose()
	}
}

