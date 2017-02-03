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

package com.myapps.upesse.upes_spefest.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.utils.GlideUtil;

public class PostViewHolder extends RecyclerView.ViewHolder{

    private final View mView;
    private PostClickListener mListener;
    public DatabaseReference mPostRef;
    public ValueEventListener mPostListener;


    public enum LikeStatus { LIKED, NOT_LIKED }
    private final ImageView mLikeIcon;
    private static final int POST_TEXT_MAX_LINES = 6;
    private ImageView mPhotoView;
    private ProgressBar mProgress;
    private ImageView mIconView;
    private TextView mAuthorView;
    private TextView mPostTextView;
    private TextView mTimestampView;
    private TextSwitcher mNumLikesView;
    //private ImageButton btnMore;
    public ImageButton btnMore;
    public String mPostKey;
    public ValueEventListener mLikeListener;
    private PostsFragment.OnPostSelectedListener onFeedItemClickListener;

    public PostViewHolder(final View itemView) {
        super(itemView);
        mView = itemView;
        mPhotoView = (ImageView) itemView.findViewById(R.id.ivFeedCenter);
        mIconView = (ImageView) mView.findViewById(R.id.post_author_icon);
        mAuthorView = (TextView) mView.findViewById(R.id.post_author_name);
        mPostTextView = (TextView) itemView.findViewById(R.id.post_text);
        mTimestampView = (TextView) itemView.findViewById(R.id.post_timestamp);
        mNumLikesView = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
        btnMore = (ImageButton)itemView.findViewById(R.id.btnMore);
        mProgress = (ProgressBar) itemView.findViewById(R.id.pbImageLoading);


        itemView.findViewById(R.id.btnComments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.showComments();
            }
        });

        mLikeIcon = (ImageView) itemView.findViewById(R.id.btnLike);
        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.toggleLike();
            }
        });
    }

    public void setPhoto(String url) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                GlideUtil.loadImage(url, mPhotoView, mProgress);
            }
            else{
                GlideUtil.loadImage(url, mPhotoView);
            }
        }catch (Exception e){}

    }

    public void setIcon(String url, final String authorId) {
        if(url.equals("NO_PROFILE_PICTURE") || url == "NO_PROFILE_PICTURE"){
            mIconView.setImageResource(R.drawable.ic_person_outline_black);
        }
        else {
            try {
                GlideUtil.loadProfileIcon(url, mIconView);
            } catch (Exception e) {}
        }
        mIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });

    }

    public void setAuthor(String author, final String authorId) {
        if (author == null || author.isEmpty()) {
            author = mView.getResources().getString(R.string.user_info_no_name);
        }
        mAuthorView.setText(author);
        mAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    private void showUserDetail(String authorId) {
        Context context = mView.getContext();
        //Intent userDetailIntent = new Intent(context, UserProfileActivity.class);
        //userDetailIntent.putExtra(UserProfileActivity.USER_ID_EXTRA_NAME, authorId);

        Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
        userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, authorId);

        context.startActivity(userDetailIntent);
    }


    public void setText(final String text) {
        if (text == null || text.isEmpty()) {
            mPostTextView.setVisibility(View.GONE);
            return;
        } else {
            mPostTextView.setVisibility(View.VISIBLE);
            mPostTextView.setText(text);
            mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
            mPostTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPostTextView.getMaxLines() == POST_TEXT_MAX_LINES) {
                        mPostTextView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
                    }
                }
            });
        }
    }

    public void setTimestamp(String timestamp) {
        mTimestampView.setText(timestamp);
    }

    public void setNumLikes(long numLikes) {
        String suffix = numLikes == 1 ? " like" : " likes";
        mNumLikesView.setText(numLikes + suffix);
    }

    public void setPostClickListener(PostClickListener listener) {
        mListener = listener;
    }

    public void setLikeStatus(LikeStatus status, Context context) {
        mLikeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                status == LikeStatus.LIKED ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey));
    }


    public interface PostClickListener {
        void showComments();
        void toggleLike();
    }
}