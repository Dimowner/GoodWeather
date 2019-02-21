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

package com.dimowner.goodweather.data.remote.model

import com.google.gson.annotations.SerializedName

/**
 * Created on 27.01.2018.
 * @author Dimowner
 */
data class Sys(
		@SerializedName("type")
		val type: Int,
		@SerializedName("id")
		val id: Long,
		@SerializedName("message")
		val message: Double,
		@SerializedName("country")
		val country: String,
		@SerializedName("sunrise")
		val sunrise: Long,
		@SerializedName("sunset")
		val sunset: Long
)
