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

package com.dimowner.goodweather.ui.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.MenuItem
import com.dimowner.goodweather.R
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.domain.metrics.MetricsContract
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : Activity(), MetricsContract.View  {

	@Inject lateinit var presenter : MetricsContract.UserActionsListener

	private val VERSION_UNAVAILABLE = "N/A"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)

		btnNavUp.setOnClickListener{finish()}

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		txtWind.setOnClickListener{ presenter.switchWind() }
		txtTempFormat.setOnClickListener{ presenter.switchTemperature() }
		txtPressure.setOnClickListener{ presenter.switchPressure() }
		txtTimeFormat.setOnClickListener{ presenter.switchTimeFormat() }

		presenter.bindView(this)

		btnAbout.setOnClickListener{ showAboutDialog() }
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (item != null && item.itemId == android.R.id.home) {
			finish()
		}
		return super.onOptionsItemSelected(item)
	}

	private fun showAboutDialog() {
		// Get app version
		val versionName: String
		versionName = try {
			val info = this.packageManager.getPackageInfo(packageName, 0)
			info.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			VERSION_UNAVAILABLE
		}

		val aboutBody = SpannableStringBuilder()
		aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)))

		AlertDialog.Builder(this)
				.setTitle(R.string.nav_about)
				.setMessage(aboutBody)
				.setPositiveButton(R.string.btn_ok, { dialog, whichButton -> dialog.dismiss() })
				.create()
				.show()
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
	}

	override fun showTemperatureFormat(format : String) {
		txtTempFormat.text = format
	}

	override fun showWindFormat(format : String) {
		txtWind.text = format
	}

	override fun showPressureFormat(format : String) {
		txtPressure.text = format
	}

	override fun showTimeFormat(format : String) {
		txtTimeFormat.text = format
	}

	override fun showProgress() {}
	override fun hideProgress() {}
	override fun showError(message: String) {}
	override fun showError(resId: Int) {}
}