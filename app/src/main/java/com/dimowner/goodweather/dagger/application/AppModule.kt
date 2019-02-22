/*
 * Copyright 2019 Dmitriy Ponomarenko
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

package com.dimowner.goodweather.dagger.application

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Room
import androidx.room.migration.Migration
import android.content.Context
import com.dimowner.goodweather.data.Prefs
import com.dimowner.goodweather.data.local.LocalRepository
import com.dimowner.goodweather.data.local.room.AppDatabase
import com.dimowner.goodweather.data.remote.GeocodeRestClient
import com.dimowner.goodweather.data.remote.RemoteRepository
import com.dimowner.goodweather.data.remote.RestClient
import com.dimowner.goodweather.data.remote.WeatherRestClient
import com.dimowner.goodweather.data.repository.Repository
import com.dimowner.goodweather.data.repository.RepositoryImpl
import com.dimowner.goodweather.app.location.LocationProvider
import com.dimowner.goodweather.app.main.WeatherContract
import com.dimowner.goodweather.app.main.WeatherPresenter
import com.dimowner.goodweather.app.settings.MetricsContract
import com.dimowner.goodweather.app.settings.MetricsPresenter
import com.dimowner.goodweather.app.welcome.WelcomePresenter
import com.dimowner.goodweather.data.PrefsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created on 20.01.2018.
 * @author Dimowner
 */
@Module
class AppModule(
		var appContext: Context
) {

	@Provides
	@Singleton
	internal fun provideContext(): Context {
		return appContext
	}

	@Provides
	@Singleton
	internal fun providePrefs(context: Context): Prefs {
		return PrefsImpl(context)
	}

	@Provides
	@Singleton
	internal fun provideRestClient(): RestClient {
		return RestClient()
	}

//	@Provides
//	@Singleton
//	internal fun provideRepository(restClient: RestClient): Repository {
//		return RepositoryImpl(restClient.weatherApi)
//	}

	@Provides
	@Singleton
	internal fun provideWeatherRestClient(): WeatherRestClient {
		return WeatherRestClient()
	}

	@Provides
	@Singleton
	internal fun provideGeocodeRestClient(): GeocodeRestClient {
		return GeocodeRestClient()
	}

	@Provides
	internal fun provideWelcomePresenter(prefs: Prefs, context: Context): WelcomePresenter {
		return WelcomePresenter(prefs, context)
	}

	@Provides
	internal fun provideMetricsPresenter(prefs: Prefs, context: Context): MetricsContract.UserActionsListener {
		return MetricsPresenter(prefs, context)
	}

	@Provides
	internal fun provideWeatherPresenter(repository: Repository, prefs: Prefs, context: Context): WeatherContract.UserActionsListener {
		return WeatherPresenter(repository, prefs, context)
	}

	@Provides
	@Singleton
	internal fun provideLocalRepository(appDatabase: AppDatabase): LocalRepository {
		return LocalRepository(appDatabase)
	}

	@Provides
	@Singleton
	internal fun provideRemoteRepository(restClient: WeatherRestClient): RemoteRepository {
		return RemoteRepository(restClient.weatherApi)
	}

	@Provides
	@Singleton
	internal fun provideRepository(localRepository: LocalRepository,
								   remoteRepository: RemoteRepository): Repository {
		return RepositoryImpl(localRepository, remoteRepository)
	}

	@Provides
	@Singleton
	internal fun provideLocationProvider(context: Context, geocodeRestClient: GeocodeRestClient): LocationProvider {
		return LocationProvider(context, geocodeRestClient.geocodeApi)
	}

	@Provides
	@Singleton
	internal fun provideAppDatabase(context: Context): AppDatabase {
		return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "weather_db")
//				.fallbackToDestructiveMigration()
				.addMigrations(MIGRATION_1_2)
				.build()
	}

	/**
	 * Migrate from:
	 * version 1 - using the SQLiteDatabase API
	 * to
	 * version 2 - using Room
	 */
	private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
		override fun migrate(database: SupportSQLiteDatabase) {
			//Migration code here
		}
	}
}
