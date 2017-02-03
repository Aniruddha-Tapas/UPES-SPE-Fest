/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.myapps.upesse.upes_spefest.ui.utils;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.myapps.upesse.upes_spefest.R;

public class GlideUtil {
    public static void loadImage(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if(context!=null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            assert context != null;
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.blue_grey_500));

            Glide.with(context)
                    .load(url)
                    .placeholder(cd)
                    .crossFade()
                    .centerCrop()
                    .into(imageView);
        }
    }

    public static void loadProfileIcon(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if(context!=null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_person_outline_black)
                    .dontAnimate()
                    .fitCenter()
                    .into(imageView);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void loadImage(String url, ImageView mPhotoView, final ProgressBar mProgress) {
        Context context = mPhotoView.getContext();
        if(context!=null || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)) {
            assert context != null;
            ColorDrawable cd = new ColorDrawable(ContextCompat.getColor(context, R.color.blue_grey_500));

            mProgress.setEnabled(true);
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgress.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .placeholder(cd)
                    .crossFade()
                    .centerCrop()
                    .into(mPhotoView);
        }
    }
}