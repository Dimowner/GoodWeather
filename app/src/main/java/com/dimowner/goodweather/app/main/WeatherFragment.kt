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

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.dimowner.goodweather.R
import com.dimowner.goodweather.GWApplication
import kotlinx.android.synthetic.main.fragment_weather_details.*
import javax.inject.Inject

class WeatherFragment : Fragment(), WeatherContract.View {

	companion object {
		fun newInstance(): WeatherFragment {
			return WeatherFragment()
		}
	}

	@Inject
	lateinit var presenter: WeatherContract.UserActionsListener

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_weather_details, container, false)
		return view
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		GWApplication.get(view.context).applicationComponent().inject(this)
		presenter.bindView(this)
		presenter.loadWeather()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		presenter.unbindView()
	}

	override fun showDate(date: String) {
		txtDate.text = date
	}

	override fun showTemperature(temp: String) {
		txtTemp.text = temp
	}

	override fun showWind(wind: String) {
		txtWind.text = wind
	}

	override fun showHumidity(humidity: String) {
		txtHumidity.text = humidity
	}

	override fun showPressure(pressure: String) {
		txtPressure.text = pressure
	}

	override fun showWeatherIconRes(resId: Int) {
		weatherIcon.setImageResource(resId)
	}

	override fun showProgress() {
		progress.visibility = View.VISIBLE
	}

	override fun hideProgress() {
		progress.visibility = View.GONE
	}

	override fun showError(message: String) {
		Toast.makeText(activity?.applicationContext, message, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, message, Snackbar.LENGTH_LONG).show()
	}

	override fun showError(resId: Int) {
		Toast.makeText(activity?.applicationContext, resId, Toast.LENGTH_LONG).show()
//		Snackbar.make(container, resId, Snackbar.LENGTH_LONG).show()
	}
}
