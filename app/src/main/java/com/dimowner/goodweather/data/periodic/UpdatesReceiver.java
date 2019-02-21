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

package com.dimowner.goodweather.data.periodic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dimowner.goodweather.GWApplication;
import com.dimowner.goodweather.data.Prefs;
import com.dimowner.goodweather.data.repository.Repository;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class UpdatesReceiver  extends BroadcastReceiver {

	@Inject Repository repository;
	@Inject Prefs prefs;

	@Override
	public void onReceive(Context context, Intent intent) {
		GWApplication.Companion.get(context).applicationComponent().inject(this);
		repository.getWeatherToday(prefs.getCity())
					.subscribeOn(Schedulers.io())
					.subscribe(data -> {}, Timber::e);

		repository.getWeatherTomorrow(prefs.getCity())
				.subscribeOn(Schedulers.io())
				.subscribe(data -> {}, Timber::e);
	}

	public static final String UPDATES_RECEIVER_ACTION = "com.dimowner.goodweather.data.UPDATES_RECEIVER_ACTION";
}
