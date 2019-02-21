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

package com.dimowner.goodweather.domain.location

import com.dimowner.goodweather.data.remote.model.location.GeocodeResultResponse
import com.google.android.gms.location.places.AutocompletePrediction
import java.util.ArrayList

class LocationMapper {

	companion object {

		fun predictionsToList(predictions: List<AutocompletePrediction>): List<String> {
			val list = ArrayList<String>()
			for (i in predictions.indices) {
				list.add(predictions[i].getPrimaryText(null).toString())
			}
			return list
		}

		fun geocodeToLocation(geocodeResultResponse: GeocodeResultResponse): Location {
			val location = geocodeResultResponse.geometry.location
			return Location(geocodeResultResponse.formattedAddress,
					location.lat,
					location.lng)
		}
	}
}