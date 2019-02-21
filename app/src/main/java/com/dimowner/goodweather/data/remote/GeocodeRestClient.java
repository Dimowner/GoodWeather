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

package com.dimowner.goodweather.data.remote;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

public class GeocodeRestClient {

	private static final String GOOGLE_API_URL = "https://maps.googleapis.com";

	private GeocodeApi geocodeApi;

	public GeocodeRestClient() {
		createGeocodeApi();
	}

	private void createGeocodeApi() {
		Timber.v("createGeocodeApi");
		OkHttpClient.Builder builder = new OkHttpClient.Builder()
				.followRedirects(true)
				.addInterceptor(new HttpLoggingInterceptor()
						.setLevel(HttpLoggingInterceptor.Level.BODY));

		Gson gson = new GsonBuilder()
				.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
				.setDateFormat("yyyy-MM-dd HH:mm:ss")
				.create();

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(GOOGLE_API_URL)
				.addConverterFactory(GsonConverterFactory.create(gson))
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.client(builder.build())
				.build();

		geocodeApi = retrofit.create(GeocodeApi.class);
	}

	public GeocodeApi getGeocodeApi() {
		return geocodeApi;
	}
}
