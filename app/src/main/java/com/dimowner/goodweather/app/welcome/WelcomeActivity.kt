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

package com.dimowner.goodweather.app.welcome

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.R
import com.dimowner.goodweather.app.settings.MetricsContract
import com.dimowner.goodweather.app.location.LocationActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import javax.inject.Inject

class WelcomeActivity : AppCompatActivity(), MetricsContract.View {

	@Inject
	lateinit var presenter: WelcomePresenter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_welcome)
		GWApplication.get(applicationContext).applicationComponent().inject(this)

		pnlWind.setOnClickListener { presenter.switchWind() }
		pnlTemperature.setOnClickListener { presenter.switchTemperature() }
		pnlPressure.setOnClickListener { presenter.switchPressure() }
		pnlTimeFormat.setOnClickListener { presenter.switchTimeFormat() }
		btnApply.setOnClickListener {
			startActivity(Intent(applicationContext, LocationActivity::class.java))
			presenter.applyInitialSettings()
			finish()
		}
		presenter.firstRunExecuted()

		presenter.bindView(this)
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun showTemperatureFormat(format: String) {
		txtTempFormat.text = format
	}

	override fun showWindFormat(format: String) {
		txtWind.text = format
	}

	override fun showPressureFormat(format: String) {
		txtPressure.text = format
	}

	override fun showTimeFormat(format: String) {
		txtTimeFormat.text = format
	}

	override fun showProgress() {}
	override fun hideProgress() {}
	override fun showError(message: String) {}
	override fun showError(resId: Int) {}

}
