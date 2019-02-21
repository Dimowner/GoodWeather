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

package com.dimowner.goodweather.data.local.room

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface WeatherDao {

	@Query("SELECT * FROM weather WHERE type = 1")
	fun getWeatherToday(): Single<WeatherEntity>

	@Query("SELECT * FROM weather WHERE type = 1")
	fun subscribeWeatherToday(): Flowable<WeatherEntity>

	@Query("SELECT * FROM weather WHERE type = 2")
	fun subscribeWeatherTomorrow(): Flowable<WeatherEntity>

	@Query("SELECT * FROM weather WHERE type = 2")
	fun getWeatherTomorrow(): Single<WeatherEntity>

	@Query("SELECT * FROM weather WHERE type = 3")
	fun subscribeWeatherTwoWeeks(): Flowable<List<WeatherEntity>>

	@Query("SELECT * FROM weather WHERE type = :weatherType")
	fun subscribeWeather(weatherType: Int): Flowable<List<WeatherEntity>>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertWeather(item: WeatherEntity)

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	fun insertAll(vararg items: WeatherEntity)

	@Query("DELETE FROM weather WHERE type = :weatherType")
	fun delete(weatherType: Int)
}
