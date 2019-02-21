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

package com.dimowner.goodweather.util;

import android.content.Context;

import com.dimowner.goodweather.AppConstants;
import com.dimowner.goodweather.R;

/**
 * Created on 27.01.2018.
 * @author Dimowner
 */
public class WeatherUtils {

//	public static float kelvinToCelsius(float tempKelvin) {
//		return tempKelvin - 273.16f;
//	}
//
//	public static String formatTemp(float tempKelvin) {
//		return (int)(tempKelvin - 273.16f) + "°C";
//	}


	public static float kelvinToCelsius(float tempKelvin) {
		return tempKelvin - 273.16f;
	}

	public static String formatTemp(float tempKelvin, int tempFormat) {
		if (tempFormat == AppConstants.TEMP_FORMAT_FAHRENHEIT) {
			return (int) (tempKelvin * 9/5 - 459.67) + "°F";
		} else {
			return (int) (tempKelvin - 273.16f) + "°C";
		}
	}

	public static String formatTemp(float tempKelvin, int tempFormat, Context context) {
		if (tempFormat == AppConstants.TEMP_FORMAT_FAHRENHEIT) {
			return (int) (tempKelvin * 9/5 - 459.67) + context.getResources().getString(R.string.temp_fahrenheit);
		} else {
			return (int) (tempKelvin - 273.16f) + context.getResources().getString(R.string.temp_celsius);
		}
	}

	public static String formatWind(float wind, int format, Context context) {
		if (format == AppConstants.WIND_FORMAT_MILES_PER_HOUR) {
			return (int) (wind * 2.236936) + " " + context.getResources().getString(R.string.wind_miles_hour);
		} else {
			return (int) wind + " " + context.getResources().getString(R.string.wind_meter_sec);
		}
	}

	public static String formatPressure(float pressure, int format, Context context) {
		if (format == AppConstants.PRESSURE_FORMAT_PHA) {
			return (int) pressure + " " + context.getResources().getString(R.string.pressure_pha);
		} else {
			return (int) (pressure / 1.33322387415) + " " + context.getResources().getString(R.string.pressure_mm_hg);
		}
	}

	public static String formatHumidity(float humidity, Context context) {
		return (int)humidity + " " + context.getResources().getString(R.string.humidity_present);
	}

	public static int weatherIconCodeToResource(String code) {
		switch (code) {
			case "01d":
				return R.drawable.d01d;
			case "02d":
				return R.drawable.d02d;
			case "03d":
				return R.drawable.d03d;
			case "04d":
				return R.drawable.d04d;
			case "09d":
				return R.drawable.d09d;
			case "10d":
				return R.drawable.d10d;
			case "11d":
				return R.drawable.d11d;
			case "13d":
				return R.drawable.d13d;
			case "50d":
				return R.drawable.d50d;

			case "01n":
				return R.drawable.d01n;
			case "02n":
				return R.drawable.d02n;
			case "03n":
				return R.drawable.d03n;
			case "04n":
				return R.drawable.d04n;
			case "09n":
				return R.drawable.d09n;
			case "10n":
				return R.drawable.d10n;
			case "11n":
				return R.drawable.d11n;
			case "13n":
				return R.drawable.d13n;
			case "50n":
				return R.drawable.d50n;

			default:
				return R.drawable.weather_loadscreen;
		}
	}
}
