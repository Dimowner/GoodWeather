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

package com.dimowner.goodweather.data

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.Delegates

/**
 * Created on 16.01.2018.
 * @author Dimowner
 */
class Prefs constructor(context: Context){

	private val PREF_NAME = "com.dimowner.goodweather.data.Prefs"

	private val PREF_KEY_IS_FIRST_RUN = "is_first_run"

	private var sharedPreferences : SharedPreferences by Delegates.notNull()

	init {
		this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
	}

	fun isFirstRun(): Boolean {
		return !sharedPreferences.contains(PREF_KEY_IS_FIRST_RUN) || sharedPreferences.getBoolean(PREF_KEY_IS_FIRST_RUN, false)
	}

	fun firstRunExecuted() {
		val editor = sharedPreferences.edit()
		editor.putBoolean(PREF_KEY_IS_FIRST_RUN, false)
		editor.apply()
	}
}
