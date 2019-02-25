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

package com.dimowner.goodweather.app.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.SpannableStringBuilder
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.goodweather.AppConstants
import com.dimowner.goodweather.R
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.app.licences.LicenceActivity
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

class SettingsActivity : AppCompatActivity(), MetricsContract.View {

	@Inject
	lateinit var presenter: MetricsContract.UserActionsListener

	private val VERSION_UNAVAILABLE = "N/A"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_settings)

//		btnNavUp.setOnClickListener { finish() }

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		pnlWind.setOnClickListener { presenter.switchWind() }
		pnlTemperature.setOnClickListener { presenter.switchTemperature() }
		pnlPressure.setOnClickListener { presenter.switchPressure() }
		pnlTimeFormat.setOnClickListener { presenter.switchTimeFormat() }
		btnLicences.setOnClickListener { startActivity(Intent(applicationContext, LicenceActivity::class.java))}
		btnRequest.setOnClickListener { requestFeature() }
		btnRate.setOnClickListener { rateApp() }

		presenter.bindView(this)

		txtAbout.text = getAboutContent()
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
		if (item != null && item.itemId == android.R.id.home) {
			finish()
		}
		return super.onOptionsItemSelected(item)
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

	private fun requestFeature() {
		val i = Intent(Intent.ACTION_SEND)
		i.type = "message/rfc822"
		i.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(AppConstants.REQUESTS_RECEIVER))
		i.putExtra(Intent.EXTRA_SUBJECT,
				"[" + resources.getString(R.string.app_name) + "] - " + resources.getString(R.string.request)
		)
		try {
			startActivity(Intent.createChooser(i, resources.getString(R.string.send_email)))
		} catch (ex: android.content.ActivityNotFoundException) {
			showError(R.string.email_clients_not_found)
		}
	}

	private fun rateApp() {
		try {
			val rateIntent = rateIntentForUrl("market://details")
			startActivity(rateIntent)
		} catch (e: ActivityNotFoundException) {
			val rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details")
			startActivity(rateIntent)
		}
	}

	private fun rateIntentForUrl(url: String): Intent {
		val intent = Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, applicationContext.packageName)))
		var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
		flags = if (Build.VERSION.SDK_INT >= 21) {
			flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
		} else {
			flags or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET
		}
		intent.addFlags(flags)
		return intent
	}

	private fun getAboutContent(): SpannableStringBuilder {
		// Get app version;
		val packageName = packageName
		var versionName: String
		versionName = try {
			val info = packageManager.getPackageInfo(packageName, 0)
			info.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			VERSION_UNAVAILABLE
		}

		// Build the about body view and append the link to see OSS licenses
		val aboutBody = SpannableStringBuilder()
		aboutBody.append(Html.fromHtml(getString(R.string.about_body, versionName)))
		return aboutBody
	}

}