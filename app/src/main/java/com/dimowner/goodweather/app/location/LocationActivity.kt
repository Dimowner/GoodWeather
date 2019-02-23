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

package com.dimowner.goodweather.app.location

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.dimowner.goodweather.app.main.MainActivity
import kotlinx.android.synthetic.main.activity_location.*
import timber.log.Timber
import javax.inject.Inject
import android.widget.ArrayAdapter
import android.view.MotionEvent
import java.util.*
import android.content.Context
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.R
import com.dimowner.goodweather.dagger.location.LocationModule
import com.dimowner.goodweather.util.AndroidUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.collections.ArrayList


class LocationActivity : AppCompatActivity(), LocationContract.View {

	val REQ_CODE_LOCATION = 303

	@Inject
	lateinit var presenter: LocationContract.UserActionsListener

	private val adapter: MySimpleArrayAdapter by lazy { MySimpleArrayAdapter(Collections.emptyList(), this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_location)

		GWApplication.get(applicationContext)
				.applicationComponent()
				.plus(LocationModule())
				.injectLocationActivity(this)

		presenter.bindView(this)

		list.adapter = adapter
		list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			presenter.findLocationForCity(adapter.getItem(position))

			// Hide keyboard:
			val v = this.currentFocus
			if (v != null) {
				val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
				imm.hideSoftInputFromWindow(v.windowToken, 0)
			}

			adapter.clear()
		}

		//Disable list scrolling
		list.setOnTouchListener { v, event -> event.action == MotionEvent.ACTION_MOVE }

		fab.setOnClickListener {
			if (checkLocatePermission()) {
				presenter.locate()
			}
		}

		mapView.onCreate(null)
		btnApply.setOnClickListener {
			startActivity(Intent(applicationContext, MainActivity::class.java))
			presenter.setLocationSelected()
			finish()
		}

		mapView.getMapAsync { map ->
			map.setOnMapClickListener { point ->
				run {
					presenter.findCity(applicationContext, point.latitude, point.longitude)
				}
			}
		}

		inputCity.setOnClickListener { presenter.setCitySelected(false) }
		inputCity.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(p0: Editable?) {}
			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
				if (p0 != null && p0.isNotBlank()) {
					Timber.v("onTextChanged: %s", p0.toString())
					presenter.findCity(p0.toString())
				} else {
					Timber.v("Empty address str")
					adapter.setItems(emptyList())
				}
			}
		})
	}

	private fun checkLocatePermission(): Boolean {
		if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQ_CODE_LOCATION)
				return false
			}
		}
		return true
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if (requestCode == REQ_CODE_LOCATION && grantResults.isNotEmpty()
				&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			presenter.locate()
		}
	}

	override fun showSelectedCity(city: String) {
		inputCity.setText(city)
		inputCity.requestFocus()
		inputCity.setSelection(inputCity.text.length)
	}

	override fun onStart() {
		super.onStart()
		mapView.onStart()
	}

	override fun onResume() {
		super.onResume()
		mapView.onResume()
	}

	override fun onPause() {
		super.onPause()
		mapView.onPause()
	}

	override fun onStop() {
		super.onStop()
		mapView.onStop()
	}

	override fun onDestroy() {
		super.onDestroy()
		presenter.unbindView()
		mapView.onDestroy()
	}

	override fun showMapMarker(lat: Double, lng: Double) {
		mapView.getMapAsync { map ->
			putMarker(map, lat, lng)
		}
	}

	override fun animateCamera(latitude: Double, longitude: Double, zoom: Float) {
		mapView.getMapAsync { map ->
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom))
		}
	}

	private fun putMarker(map: GoogleMap, lat: Double, lng: Double) {
		map.clear()
		map.addMarker(MarkerOptions().position(LatLng(lat, lng)).icon(
				AndroidUtils.bitmapDescriptorFromVector(applicationContext, R.drawable.ic_map_marker)))
	}

	override fun showPredictions(data: List<String>) {
		adapter.setItems(data)
	}

	override fun enableButtonApply(enable: Boolean) {
		btnApply.isEnabled = enable
	}

	override fun showProgress() {}
	override fun hideProgress() {}
	override fun showError(message: String) {}
	override fun showError(resId: Int) {}

	inner class MySimpleArrayAdapter(private var values: List<String>, context: Context) : ArrayAdapter<String>(context, -1, values) {

		override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
			if (convertView == null) {
				val inflater = context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
				val textView = inflater.inflate(R.layout.list_item_prediction, parent, false) as TextView
				textView.text = values[position]

				return textView
			} else {
				(convertView as TextView).text = values[position]
				return convertView
			}
		}

		override fun getCount(): Int {
			return values.size
		}

		fun setItems(items: List<String>) {
			values = items
			notifyDataSetChanged()
		}

		override fun getItem(pos: Int): String {
			return values[pos]
		}

		override fun clear() {
			values = ArrayList()
			notifyDataSetChanged()
		}
	}
}
