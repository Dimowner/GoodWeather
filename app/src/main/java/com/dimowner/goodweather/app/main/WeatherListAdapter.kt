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

package com.dimowner.goodweather.app.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dimowner.goodweather.AppConstants
import com.dimowner.goodweather.R
import com.dimowner.goodweather.data.local.room.WeatherEntity
import com.dimowner.goodweather.util.TimeUtils
import com.dimowner.goodweather.util.WeatherUtils

class WeatherListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private var data: List<WeatherEntity> = ArrayList()

	private var itemClickListener: ItemClickListener? = null
	private var temperatureFormat = AppConstants.TEMP_FORMAT_CELSIUS

	private var timeFormat = AppConstants.TIME_FORMAT_24H

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
		return ItemViewHolder(v)
	}

	override fun onBindViewHolder(h: RecyclerView.ViewHolder, position: Int) {
		val pos = h.adapterPosition
		val holder = h as ItemViewHolder
		holder.name.text = TimeUtils.formatTime(data[pos].dt * 1000, timeFormat)
		holder.description.text = data[pos].description
		holder.txtTemp.text = WeatherUtils.formatTemp(data[pos].temp, temperatureFormat)
		holder.image.setImageResource(WeatherUtils.weatherIconCodeToResource(data[pos].icon))

		holder.view.setOnClickListener { v -> itemClickListener?.onItemClick(v, pos) }
	}

	override fun getItemCount(): Int {
		return data.size
	}

	fun setData(data: List<WeatherEntity>) {
		this.data = data
		notifyDataSetChanged()
	}

	fun updateTemperatureFormat(temperatureFormat: Int) {
		this.temperatureFormat = temperatureFormat
		notifyItemRangeChanged(0, data.size)
	}

	fun updateTimeFormat(timeFormat: Int) {
		this.timeFormat = timeFormat
		notifyItemRangeChanged(0, data.size)
	}

	fun setItemClickListener(itemClickListener: ItemClickListener) {
		this.itemClickListener = itemClickListener
	}

	internal inner class ItemViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
		var name: TextView = view.findViewById(R.id.list_item_name)
		var description: TextView = view.findViewById(R.id.list_item_description)
		var image: ImageView = view.findViewById(R.id.list_item_image)
		var txtTemp: TextView = view.findViewById(R.id.list_item_temp)
	}

	interface ItemClickListener {
		fun onItemClick(view: View, position: Int)
	}
}