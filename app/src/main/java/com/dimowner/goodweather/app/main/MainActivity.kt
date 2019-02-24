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

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.dimowner.goodweather.R
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.periodic.UpdateManager
import com.dimowner.goodweather.app.location.LocationActivity
import com.dimowner.goodweather.app.settings.SettingsFragment
import com.dimowner.goodweather.app.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main2.*
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {

	companion object {
		const val ITEM_SETTINGS = 0
		const val ITEM_TODAY = 1
		const val ITEM_TWO_WEEKS = 2
	}

	@Inject
	lateinit var prefs: Prefs

	private var prevMenuItem: MenuItem? = null

	//TODO: Optimize request count to server
	//TODO: Optimize ViewPager performance

	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
		item.isChecked = true
		when (item.itemId) {
			R.id.nav_settings -> {
				pager.setCurrentItem(ITEM_SETTINGS, true)
			}
			R.id.nav_today -> {
				pager.setCurrentItem(ITEM_TODAY, true)
			}
			R.id.nav_two_weeks -> {
				pager.setCurrentItem(ITEM_TWO_WEEKS, true)
			}
		}
		false
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main2)

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		if (prefs.isFirstRun() || !prefs.isInitialSettingApplied()) {
			startActivity(Intent(applicationContext, WelcomeActivity::class.java))
			finish()
		} else if (!prefs.isLocationSelected()) {
			startActivity(Intent(applicationContext, LocationActivity::class.java))
			finish()
		} else {
			val fragments = ArrayList<Fragment>()
			fragments.add(SettingsFragment.newInstance())
			fragments.add(WeatherFragment.newInstance())
			fragments.add(WeatherListFragment.newInstance())
			val adapter = MyStatePagerAdapter(supportFragmentManager, fragments)
			pager.adapter = adapter
			pager.currentItem = ITEM_TODAY
			onPageSelected(ITEM_TODAY)
			pager.addOnPageChangeListener(this)

			bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
		}

		if (UpdateManager.checkPeriodicUpdatesRunning(applicationContext)) {
			Timber.v("alarm is running")
//			UpdateManager.stopPeriodicUpdates(applicationContext)
//			UpdateManager.startPeriodicUpdates(applicationContext)
		} else {
			Timber.v("alarm is NOT running")
			UpdateManager.startPeriodicUpdates(applicationContext)
		}

		toolbar.text = prefs.getCity()
		toolbar.setOnClickListener { startActivity(Intent(applicationContext, LocationActivity::class.java)) }
	}

	override fun onResume() {
		super.onResume()
		toolbar.text = prefs.getCity()
	}

	override fun onPageScrollStateChanged(state: Int) {}
	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

	override fun onPageSelected(position: Int) {
		if (prevMenuItem != null) {
			prevMenuItem?.isChecked = false
		}
		else {
			bottomNavigation.menu.getItem(0).isChecked = false
		}

		bottomNavigation.menu.getItem(position).isChecked = true
		prevMenuItem = bottomNavigation.menu.getItem(position)
	}
}
