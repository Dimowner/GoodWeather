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

package com.dimowner.goodweather.dagger.application

import com.dimowner.goodweather.GWApplication
import com.dimowner.goodweather.dagger.location.LocationComponent
import com.dimowner.goodweather.dagger.location.LocationModule
import com.dimowner.goodweather.data.periodic.JobSchedulerService
import com.dimowner.goodweather.data.periodic.UpdatesReceiver
import com.dimowner.goodweather.ui.main.MainActivity
import com.dimowner.goodweather.ui.main.WeatherDetailsFragment
import com.dimowner.goodweather.ui.main.WeatherTwoWeeksFragment
import com.dimowner.goodweather.ui.settings.SettingsActivity
import com.dimowner.goodweather.ui.welcome.WelcomeActivity
import dagger.Component
import javax.inject.Singleton

/**
 * Created on 20.01.2018.
 * @author Dimowner
 */
@Component(modules = arrayOf(AppModule::class))
@Singleton
interface AppComponent {

	fun inject(app: GWApplication)

	fun inject(activity: MainActivity)

	fun inject(activity: WelcomeActivity)

	fun inject(activity: SettingsActivity)

	fun inject(fragment: WeatherDetailsFragment)

	fun inject(fragment: WeatherTwoWeeksFragment)

	fun inject(receiver: UpdatesReceiver)

	fun inject(service: JobSchedulerService)

	fun plus(detailsModule: LocationModule): LocationComponent

}
