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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import timber.log.Timber;

public class UpdateManager {

	private static final int PERIODIC_UPDATE_ID = 524;

	public static void startPeriodicUpdates(Context context) {
		Timber.d("startPeriodicUpdates");
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intentToFire = new Intent(UpdatesReceiver.UPDATES_RECEIVER_ACTION);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(
					context, PERIODIC_UPDATE_ID, intentToFire, PendingIntent.FLAG_CANCEL_CURRENT);

			if (alarmManager != null) {
				alarmManager.setInexactRepeating(
						AlarmManager.ELAPSED_REALTIME_WAKEUP,
						SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HALF_DAY,
						AlarmManager.INTERVAL_DAY, alarmIntent
				);
			}
		} else {
			scheduleJob(context);
		}
	}

	public static void stopPeriodicUpdates(Context context) {
		Timber.d("stopPeriodicUpdates");
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			AlarmManager alarmManager =
					(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intentToFire = new Intent(
					UpdatesReceiver.UPDATES_RECEIVER_ACTION);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(
					context, PERIODIC_UPDATE_ID, intentToFire, PendingIntent.FLAG_CANCEL_CURRENT);
			if (alarmManager != null) {
				alarmManager.cancel(alarmIntent);
			}
		} else {
			JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
			if (scheduler != null) {
				scheduler.cancel(PERIODIC_UPDATE_ID);
				JobSchedulerService.setPeriodic(false);
			}
		}
	}

	public static boolean checkPeriodicUpdatesRunning(Context context) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			return (PendingIntent.getBroadcast(context, PERIODIC_UPDATE_ID,
					new Intent(UpdatesReceiver.UPDATES_RECEIVER_ACTION),
					PendingIntent.FLAG_NO_CREATE) != null);
		} else {
			return JobSchedulerService.isPeriodic();
		}
	}

	@TargetApi(21)
	static void scheduleJob(Context context) {
		if (!JobSchedulerService.isPeriodic()) {
			JobSchedulerService.setPeriodic(true);
		}

		ComponentName serviceComponent = new ComponentName(context, JobSchedulerService.class);
		JobInfo.Builder builder = new JobInfo.Builder(PERIODIC_UPDATE_ID, serviceComponent);
		builder.setMinimumLatency(AlarmManager.INTERVAL_HALF_DAY);
		builder.setOverrideDeadline(AlarmManager.INTERVAL_DAY);
		builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
		//builder.setRequiresDeviceIdle(true);
		//builder.setRequiresCharging(true);
		JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
		if (jobScheduler != null) {
			jobScheduler.schedule(builder.build());
		}
	}
}
