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
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import com.dimowner.goodweather.R
import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.periodic.UpdateManager
import com.dimowner.goodweather.app.location.LocationActivity
import com.dimowner.goodweather.app.settings.SettingsActivity
import com.dimowner.goodweather.app.welcome.WelcomeActivity
import kotlinx.android.synthetic.main.activity_main2.*
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

class MainActivity : FragmentActivity(), ViewPager.OnPageChangeListener {

	private val ITEM_TODAY = 0
	private val ITEM_TOMORROW = 1
	private val ITEM_TWO_WEEKS = 2

	@Inject
	lateinit var prefs: Prefs

	private var prevMenuItem: MenuItem? = null

	//TODO: Optimize request count to server
	//TODO: Optimize ViewPager performance

//	private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//		item.isChecked = true
//		when (item.itemId) {
//			R.id.nav_today -> {
//				pager.setCurrentItem(ITEM_TODAY, true)
//			}
//			R.id.nav_tomorrow -> {
//				pager.setCurrentItem(ITEM_TOMORROW, true)
//			}
//			R.id.nav_two_weeks -> {
//				pager.setCurrentItem(ITEM_TWO_WEEKS, true)
//			}
//		}
//		false
//	}

	override fun onCreate(savedInstanceState: Bundle?) {
		setTheme(R.style.AppTheme)
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main2)

//		setSupportActionBar(toolbar)

		GWApplication.get(applicationContext).applicationComponent().inject(this)

		btnSettings.setOnClickListener { startActivity(Intent(applicationContext, SettingsActivity::class.java)) }

		if (prefs.isFirstRun() || !prefs.isInitialSettingApplied()) {
			startActivity(Intent(applicationContext, WelcomeActivity::class.java))
			finish()
		} else if (!prefs.isLocationSelected()) {
			startActivity(Intent(applicationContext, LocationActivity::class.java))
			finish()
		} else {
			val fragments = ArrayList<Fragment>()
			fragments.add(WeatherDetailsFragment.newInstance())
			fragments.add(WeatherTwoWeeksFragment())
			val adapter = MyStatePagerAdapter(supportFragmentManager, fragments)
			pager.adapter = adapter
			pager.addOnPageChangeListener(this)

//			bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
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
//		if (prevMenuItem != null) {
//			prevMenuItem?.isChecked = false
//		}
//		else {
//			bottomNavigation.menu.getItem(0).isChecked = false
//		}
//
//		bottomNavigation.menu.getItem(position).isChecked = true
//		prevMenuItem = bottomNavigation.menu.getItem(position)
	}

//	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//		menuInflater.inflate(R.menu.menu_main, menu)
//		return super.onCreateOptionsMenu(menu)
//	}
//
//	override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//		if (item != null) {
////			if (item.itemId == R.id.action_locate) {
////				//Locate
////				startActivity(Intent(applicationContext, WelcomeActivity::class.java))
////			} else
//			if (item.itemId == R.id.action_settings) {
//				startActivity(Intent(applicationContext, SettingsActivity::class.java))
//			}
//		}
//		return super.onOptionsItemSelected(item)
//	}
//
//	private class MyPagerAdapter internal constructor(
//			fm: FragmentManager,
//			private val fragments: List<Fragment>) : MyStatePagerAdapter(fm) {
//
//		override fun getItem(position: Int): Fragment {
//			return fragments[position]
//		}
//
//		override fun getCount(): Int {
//			return fragments.size
//		}
//	}
}
