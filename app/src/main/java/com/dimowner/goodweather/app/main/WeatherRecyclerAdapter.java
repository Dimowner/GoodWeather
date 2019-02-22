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

package com.dimowner.goodweather.app.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dimowner.goodweather.AppConstants;
import com.dimowner.goodweather.R;
import com.dimowner.goodweather.data.local.room.WeatherEntity;
import com.dimowner.goodweather.util.TimeUtils;
import com.dimowner.goodweather.util.WeatherUtils;

import java.util.ArrayList;
import java.util.List;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private List<WeatherEntity> mShowingData;

	private ItemClickListener itemClickListener;

	private int temperatureFormat = AppConstants.TEMP_FORMAT_CELSIUS;

	public WeatherRecyclerAdapter() {
		mShowingData = new ArrayList<>();
	}

	class ItemViewHolder extends RecyclerView.ViewHolder {
		TextView name;
		TextView description;
		ImageView image;
		TextView txtTemp;
		View view;

		ItemViewHolder(View itemView) {
			super(itemView);
			this.view = itemView;
			this.name = itemView.findViewById(R.id.list_item_name);
			this.description = itemView.findViewById(R.id.list_item_description);
			this.image = itemView.findViewById(R.id.list_item_image);
			this.txtTemp = itemView.findViewById(R.id.list_item_temp);
		}
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
		return new ItemViewHolder(v);
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int position) {
		int pos = h.getAdapterPosition();
		ItemViewHolder holder = (ItemViewHolder) h;
		holder.name.setText(TimeUtils.formatTime(mShowingData.get(pos).getDt() * 1000, AppConstants.TIME_FORMAT_24H));
		holder.description.setText(mShowingData.get(pos).getDescription());
		holder.txtTemp.setText(WeatherUtils.formatTemp(mShowingData.get(pos).getTemp(), temperatureFormat));
//		Glide.with(holder.view.getContext())
//				.load(Constants.WEATHER_ICON_URL + mShowingData.get(pos).getIcon() + Constants.PNG)
//				.apply(RequestOptions.circleCropTransform())
//				.listener(new RequestListener<Drawable>() {
//					@Override
//					public boolean onLoadFailed(@Nullable GlideException e, Object model,
//														 Target<Drawable> target, boolean isFirstResource) {
//						holder.image.setImageResource(R.drawable.loadscreen);
//						return false;
//					}
//
//					@Override
//					public boolean onResourceReady(Drawable resource, Object model,
//															 Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
//						return false;
//					}
//				})
//				.into(holder.image);
		holder.image.setImageResource(WeatherUtils.weatherIconCodeToResource(mShowingData.get(pos).getIcon()));

		holder.view.setOnClickListener(v -> {
			if (itemClickListener != null) {
				itemClickListener.onItemClick(v, pos);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mShowingData.size();
	}

	public void setData(List<WeatherEntity> data) {
		this.mShowingData = data;
		notifyDataSetChanged();
	}

	public void setTemperatureFormat(int temperatureFormat) {
		this.temperatureFormat = temperatureFormat;
	}

	public void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public interface ItemClickListener {
		void onItemClick(View view, int position);
	}
}
