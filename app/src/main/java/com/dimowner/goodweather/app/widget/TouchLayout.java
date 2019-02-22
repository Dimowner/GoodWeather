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

package com.dimowner.goodweather.app.widget;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.FloatPropertyCompat;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import timber.log.Timber;

public class TouchLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {

	private static final int ACTION_NONE = -1;
	private static final int ACTION_DRAG = 1;
	private static final int ACTION_ZOOM = 2;

	private static final int MAX_MOVE = 300; //px
	private static final float MIN_ZOOM = 1f;
	private static final float MAX_ZOOM = 2.0f;

	private static final int MAX_ACCELERATION = 50;

	private SpringAnimation moveAnimationX;
	private SpringAnimation moveAnimationY;
	private SpringAnimation scaleAnimation;

	private int action = ACTION_NONE;
	private float viewScale = 1.0f;
	private float realScale = 1.0f;

	private float realDx = 0;
	private float realDy = 0;

	private float startX = 0f;
	private float startY = 0f;

	private float dx = 0f;
	private float dy = 0f;

	private float x1 = 0;
	private float y1 = 0;
	private float accelerationX = 0;
	private float accelerationY = 0;

	//Converted value from pixels to coefficient used in function which describes move.
	private final float k = (float) (MAX_MOVE / (Math.PI / 2));

	private View childView;

	public TouchLayout(Context context) {
		super(context);
		init(context);
	}

	public TouchLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TouchLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (childView == null) {
			if (getChildAt(0) == null) {
				throw new RuntimeException("TouchView has no child");
			}
			childView = getChildAt(0);
		}

		//Reveal animation on start
//		scaleAnimation = physicsBasedScaleAnimation(childView, 0f);
	}

	private void init(Context context) {
		final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
		setOnTouchListener((view, motionEvent) -> {
			switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_DOWN:
					Timber.v("DOWN");
					performClick();
					action = ACTION_DRAG;
					startX = motionEvent.getX();
					startY = motionEvent.getY();

					if (moveAnimationX != null) {
						if (moveAnimationX.canSkipToEnd()) {
							moveAnimationX.skipToEnd();
						} else {
							moveAnimationX.cancel();
						}
					}
					if (moveAnimationY != null) {
						if (moveAnimationY.canSkipToEnd()) {
							moveAnimationY.skipToEnd();
						} else {
							moveAnimationY.cancel();
						}
					}
					if (scaleAnimation != null) {
						if (scaleAnimation.canSkipToEnd()) {
							scaleAnimation.skipToEnd();
						} else {
							scaleAnimation.cancel();
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (action == ACTION_DRAG) {
						realDx = motionEvent.getX() - startX;
						realDy = motionEvent.getY() - startY;

						dx = (float) (k * Math.atan(realDx / k));
						dy = (float) (k * Math.atan(realDy / k));

						childView.setTranslationX(dx + accelerationX);
						childView.setTranslationY(dy + accelerationY);
						Timber.v("DRAG x = " + dx + " y = " + dy);
					}
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					Timber.v("ZOOM");
					action = ACTION_ZOOM;
					break;
				case MotionEvent.ACTION_POINTER_UP:
					Timber.v("DRAG");
					action = ACTION_NONE;
					break;
				case MotionEvent.ACTION_UP:
					Timber.v("UP");
					if (viewScale == 1) {
						//Spring animation moves child view to start position
						moveAnimationX = new SpringAnimation(childView, DynamicAnimation.TRANSLATION_X, 0);
						moveAnimationX.getSpring().setStiffness(SpringForce.STIFFNESS_LOW)
								.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

						moveAnimationY = new SpringAnimation(childView, DynamicAnimation.TRANSLATION_Y, 0);
						moveAnimationY.getSpring().setStiffness(SpringForce.STIFFNESS_LOW)
								.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

						moveAnimationX.start();
						moveAnimationY.start();
					}
					dx = 0f;
					dy = 0f;
					break;
			}
			scaleDetector.onTouchEvent(motionEvent);
			if (viewScale >= MIN_ZOOM || action == ACTION_ZOOM) {
				childView.setScaleX(viewScale);
				childView.setScaleY(viewScale);
			}
			return true;
		});
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
		Timber.v("onScaleBegin");
		return true;
	}

	@Override
	public boolean onScale(ScaleGestureDetector scaleDetector) {
		float scaleFactor = scaleDetector.getScaleFactor();
		realScale *= scaleFactor;
		viewScale = (MAX_ZOOM * realScale - (MAX_ZOOM - 1)) / realScale;
		viewScale = Math.max(MIN_ZOOM, Math.min(viewScale, MAX_ZOOM));

		Timber.v("onScale" + scaleFactor + " scale = " + viewScale + " realScale = " + realScale);
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector scaleDetector) {
		Timber.v("onScaleEnd");

		//Spring animation scales child view to start size.
		scaleAnimation = physicsBasedScaleAnimation(childView, viewScale);

		viewScale = 1f;
		realScale = 1f;
	}

//	public void setOrientation(float x1, float y1) {
//		this.x1 = -800 * 0.37f * (float) Math.cos(Math.toRadians(90 - x1));
//		this.y1 = 800 * 0.37f * (float) Math.cos(Math.toRadians(90 - y1));
//		Timber.v("setOrientation x1 = " + this.x1 + " y1 = " + this.y1);
//	}

	public void setAcceleration(float x, float y) {
		//This if added for view invalidation optimization to make view less often invalidated.
//		TODO: need better optimization
		Timber.v("x1 = " + (int) x1 + " x = " + (int) (x * 1000) + " y1 = " + (int) y1 + " y = " + (int) (y * 1000));
		if ((int) x1 != (int) (x * 1000) && (int) y1 != (int) (y * 1000)) {
			this.x1 = x * 1000;
			this.y1 = y * 1000;
			this.accelerationX = (float) -(k * Math.atan(x * MAX_ACCELERATION / k));
			this.accelerationY = (float) (k * Math.atan(y * MAX_ACCELERATION / k));
			Timber.v("setAcceleration X = " + this.accelerationX + " Y = " + accelerationY);
			childView.setTranslationX(dx + this.accelerationX);
			childView.setTranslationY(dy + this.accelerationY);
		}
	}

	private SpringAnimation physicsBasedScaleAnimation(View view, float s) {
		view.setScaleX(s);
		view.setScaleY(s);

		FloatPropertyCompat<View> scale = new FloatPropertyCompat<View>("") {
			@Override
			public float getValue(View object) {
				return object.getScaleX();
			}

			@Override
			public void setValue(View object, float value) {
				object.setScaleX(value);
				object.setScaleY(value);
			}
		};

		SpringAnimation animation = new SpringAnimation(view, scale, 1f);
		animation.getSpring().setStiffness(SpringForce.STIFFNESS_LOW)
				.setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);
		animation.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_ALPHA)
				.setStartVelocity(2f);

		animation.start();
		return animation;
	}
}
