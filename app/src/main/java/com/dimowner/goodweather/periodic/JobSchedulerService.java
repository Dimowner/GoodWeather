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

package com.dimowner.goodweather.periodic;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;

import com.dimowner.goodweather.GWApplication;
import com.dimowner.goodweather.data.Prefs;
import com.dimowner.goodweather.data.repository.Repository;

import javax.inject.Inject;

import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

@TargetApi(21)
public class JobSchedulerService extends JobService {

	private static boolean isPeriodic = true;

	@Inject
	Repository repository;
	@Inject
	Prefs prefs;

	@Override
	public boolean onStartJob(JobParameters params) {
		GWApplication.Companion.get(getApplicationContext()).applicationComponent().inject(this);
		repository.getWeatherToday(prefs.getCity())
				.subscribeOn(Schedulers.io())
				.subscribe(data -> {
				}, Timber::e);

		if (isPeriodic) {
			UpdateManager.scheduleJob(getApplicationContext());
		}
		return true;
	}

	@Override
	public boolean onStopJob(JobParameters params) {
		return true;
	}

	public static boolean isPeriodic() {
		return isPeriodic;
	}

	public static void setPeriodic(boolean b) {
		isPeriodic = b;
	}
}
