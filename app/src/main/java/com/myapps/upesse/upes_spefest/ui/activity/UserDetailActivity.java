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
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.utils.Utils;
import com.myapps.upesse.upes_spefest.ui.Models.Person;
import com.myapps.upesse.upes_spefest.ui.Models.Post;
import com.myapps.upesse.upes_spefest.ui.utils.GlideUtil;
import com.myapps.upesse.upes_spefest.ui.view.FeedContextMenuManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import butterknife.BindView;
//import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailActivity extends AppCompatActivity {
    private final String TAG = "UserDetailActivity";
    public static final String USER_ID_EXTRA_NAME = "user_name";
    private RecyclerView mRecyclerGrid;
    private GridAdapter mGridAdapter;
    private ValueEventListener mFollowingListener;
    private ValueEventListener mPersonInfoListener;
    private String mUserId;
    private DatabaseReference mPeopleRef;
    private DatabaseReference mPersonRef;
    private static final int GRID_NUM_COLUMNS = 3;
    private DatabaseReference mFollowersRef;
    private ValueEventListener mFollowersListener;
    private PostsFragment.OnPostSelectedListener mListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        Intent intent = getIntent();
        mUserId = intent.getStringExtra(USER_ID_EXTRA_NAME);

        final ArrayList<String> followedposts = new ArrayList<String>();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        // TODO: Investigate why initial toolbar title is activity name instead of blank.

        mPeopleRef = FirebaseUtil.getPeopleRef();
        final String currentUserId = FirebaseUtil.getCurrentUserId();

        final FloatingActionButton followUserFab = (FloatingActionButton) findViewById(R.id
                .follow_user_fab);


        if (currentUserId != null && mUserId.equals(currentUserId)) {
            followUserFab.setVisibility(View.GONE);
        }

        mFollowingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    followUserFab.setImageDrawable(ContextCompat.getDrawable(
                            UserDetailActivity.this, R.drawable.ic_done_24dp));
                } else {
                    followUserFab.setImageDrawable(ContextCompat.getDrawable(
                            UserDetailActivity.this, R.drawable.ic_person_add_24dp));
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        };

        if (currentUserId != null) {
            mPeopleRef.child(currentUserId).child("following").child(mUserId)
                    .addValueEventListener(mFollowingListener);
        }

        //Followed posts
        FirebaseUtil.getPeopleRef().child(mUserId).child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot postSnapshot, String s) {
                //HashMap<String, Object> addedPost = new HashMap<String, Object>();
                //addedPost.put(postSnapshot.getKey(), null);
                followedposts.add(postSnapshot.getKey());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        followUserFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentUserId == null) {
                    Toast.makeText(UserDetailActivity.this, "You need to sign in to follow someone.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mUserId.equals(currentUserId)) {
                    Toast.makeText(UserDetailActivity.this, "You can't follow your self.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // TODO: Convert these to actually not be single value, for live updating when
                // current user follows.
                mPeopleRef.child(currentUserId).child("following").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> updatedUserData = new HashMap<>();
                        if (dataSnapshot.exists()) {
                            // Already following, need to unfollow

                            for (int i = 0; i < followedposts.size(); i++) {
                                updatedUserData.put("feed/" + currentUserId + "/" + followedposts.get(i), null);
                            }
                            updatedUserData.put("people/" + currentUserId + "/following/" + mUserId, null);
                            updatedUserData.put("followers/" + mUserId + "/" + currentUserId, null);
                            //updatedUserData.put("feed/" + currentUserId + "/" + currentUserId, null);
                        } else {
                            updatedUserData.put("people/" + currentUserId + "/following/" + mUserId, true);
                            updatedUserData.put("followers/" + mUserId + "/" + currentUserId, true);

                        }
                        FirebaseUtil.getBaseRef().updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError firebaseError, DatabaseReference firebase) {
                                if (firebaseError != null) {
                                    Toast.makeText(UserDetailActivity.this, R.string
                                            .follow_user_error, Toast.LENGTH_LONG).show();
                                    Log.d(TAG, getString(R.string.follow_user_error) + "\n" +
                                            firebaseError.getMessage());
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }
        });

        mRecyclerGrid = (RecyclerView) findViewById(R.id.user_posts_grid);
        //userPhotosAdapter = new UserProfileAdapter(this);
        //mRecyclerGrid.setAdapter(userPhotosAdapter);

        /*
        mRecyclerGrid.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
        */

        mGridAdapter = new GridAdapter(this);
        mRecyclerGrid.setAdapter(mGridAdapter);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerGrid.setLayoutManager(layoutManager);
        //mRecyclerGrid.setLayoutManager(new GridLayoutManager(this, GRID_NUM_COLUMNS));

        mPersonRef = FirebaseUtil.getPeopleRef().child(mUserId);
        mPersonInfoListener = mPersonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Person person = dataSnapshot.getValue(Person.class);
                Log.w(TAG, "mPersonRef:" + mPersonRef.getKey());
                CircleImageView userPhoto = (CircleImageView) findViewById(R.id.user_detail_photo);
                try {
                    GlideUtil.loadProfileIcon(person.getPhotoUrl(), userPhoto);
                } catch (Exception e) {
                }

                String name = person.getDisplayName();
                if (name == null) {
                    name = getString(R.string.user_info_no_name);
                }
                collapsingToolbar.setTitle(name);
                if (person.getFollowing() != null) {
                    int numFollowing = person.getFollowing().size();
                    ((TextView) findViewById(R.id.user_num_following))
                            .setText(numFollowing + " following");
                }

                if (person.getPosts() != null) {
                    List<String> paths = new ArrayList<String>(person.getPosts().keySet());
                    mGridAdapter.addPaths(paths);
                    String firstPostKey = paths.get(0);

                    FirebaseUtil.getPostsRef().child(firstPostKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Post post = dataSnapshot.getValue(Post.class);

                            ImageView imageView = (ImageView) findViewById(R.id.backdrop);
                            try {
                                GlideUtil.loadImage(post.getFull_url(), imageView);
                            } catch (Exception e) {
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
        mFollowersRef = FirebaseUtil.getFollowersRef().child(mUserId);
        mFollowersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long numFollowers = dataSnapshot.getChildrenCount();
                ((TextView) findViewById(R.id.user_num_followers))
                        .setText(numFollowers + " follower" + (numFollowers == 1 ? "" : "s"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mFollowersRef.addValueEventListener(mFollowersListener);
    }

    @Override
    protected void onDestroy() {
        if (FirebaseUtil.getCurrentUserId() != null) {
            mPeopleRef.child(FirebaseUtil.getCurrentUserId()).child("following").child(mUserId)
                    .removeEventListener(mFollowingListener);
        }

        mPersonRef.child(mUserId).removeEventListener(mPersonInfoListener);
        mFollowersRef.removeEventListener(mFollowersListener);
        super.onDestroy();
    }

    class GridAdapter extends RecyclerView.Adapter<GridImageHolder> {
        private List<String> mPostPaths;
        private final Context context;
        private final int cellSize;

        public GridAdapter(Context context) {
            mPostPaths = new ArrayList<String>();
            this.context = context;
            this.cellSize = Utils.getScreenWidth(context) / 3;
        }

        @Override
        public GridImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //ImageView imageView = new ImageView(UserDetailActivity.this);
            /*
            int tileDimPx = getPixelsFromDps(100);
            imageView.setLayoutParams(new GridView.LayoutParams(tileDimPx, tileDimPx));
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            //imageView.setPadding(2, 2, 2, 2);
            imageView.setPadding(1,1,1,1);
            */
            final View view = LayoutInflater.from(context).inflate(R.layout.item_photo, parent, false);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) view.getLayoutParams();
            layoutParams.height = cellSize;
            layoutParams.width = cellSize;
            layoutParams.setFullSpan(false);
            //imageView.setLayoutParams(layoutParams);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            view.setLayoutParams(layoutParams);
            //return new UserProfileAdapter.PhotoViewHolder(view);
            //return new GridImageHolder(imageView);
            return new GridImageHolder(view);
        }

        @Override
        public void onBindViewHolder(final GridImageHolder holder, int position) {
            final DatabaseReference ref = FirebaseUtil.getPostsRef().child(mPostPaths.get(position));
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Post post = dataSnapshot.getValue(Post.class);
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            GlideUtil.loadImage(post.getFull_url(), holder.ivPhoto, holder.pbImageLoading2);
                        }
                        else{
                            GlideUtil.loadImage(post.getFull_url(), holder.ivPhoto);
                        }
                    } catch (Exception e) {
                    }

                    holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO: Implement go to post view.
                            //Toast.makeText(UserDetailActivity.this, "Selected: " + holder.getAdapterPosition(),Toast.LENGTH_SHORT).show();
                            //Toast.makeText(getApplicationContext(),post.getThumb_url(),Toast.LENGTH_LONG).show();
                            final int position = holder.getAdapterPosition();
                            final String firstPostKey = mPostPaths.get(position);
                            FeedContextMenuManager.setCurrentPost(post);


                            //BlurDialogFragment
                            final SampleSupportDialogFragment fragment
                                    = SampleSupportDialogFragment.newInstance(
                                    //mBlurRadiusSeekbar.getProgress() + 1,
                                    10,
                                    //(mDownScaleFactorSeekbar.getProgress() / 10f) + 2,
                                    5,
                                    //mDimmingEnable.isChecked(),
                                    false,
                                    //mDebugMode.isChecked(),
                                    false,
                                    //mBlurredActionBar.isChecked(),
                                    true,
                                    //mUseRenderScript.isChecked()
                                    false,

                                    post.getThumb_url(),

                                    post.getText(),

                                    DateUtils.getRelativeTimeSpanString(
                                            (long) post.getTimestamp()).toString(),

                                    post.getAuthor().getFull_name(),

                                    post.getAuthor().getProfile_picture(),

                                    firstPostKey,

                                    position,

                                    post.getAuthor().getUid()

                            );

                            fragment.show(getSupportFragmentManager(), "blur_sample");


                            //fragment.setPhoto(post.getThumb_url());
                            //fragment.setText(post.getText());
                            /*
                            fragment.setTimestamp(DateUtils.getRelativeTimeSpanString(
                                    (long) post.getTimestamp()).toString());

                            Author author = post.getAuthor();
                            fragment.setAuthor(author.getFull_name(), author.getUid());
                            fragment.setIcon(author.getProfile_picture(), author.getUid());

                            ValueEventListener likeListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    fragment.setNumLikes(dataSnapshot.getChildrenCount());
                                    if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                                        fragment.setLikeStatus(PostViewHolder.LikeStatus.LIKED, getApplicationContext());
                                    } else {
                                        fragment.setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, getApplicationContext());
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                            FirebaseUtil.getLikesRef().child(firstPostKey).addValueEventListener(likeListener);
                            fragment.mLikeListener = likeListener;

                            fragment.btnMore.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mListener.onMoreClick(v, position,post.getThumb_url(),post.getAuthor().getUid(), post, firstPostKey);
                                }
                            });


                            fragment.setPostClickListener(new SampleSupportDialogFragment.PostClickListener() {
                                @Override
                                public void showComments() {
                                    Log.d(TAG, "Comment position: " + position);
                                    mListener.onPostComment(firstPostKey);
                                }

                                @Override
                                public void toggleLike() {
                                    Log.d(TAG, "Like position: " + position);
                                    mListener.onPostLike(firstPostKey);
                                }

                            });
                            */

                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e(TAG, "Unable to load grid image: " + firebaseError.getMessage());
                }
            });
        }


        public void addPath(String path) {
            mPostPaths.add(path);
            notifyItemInserted(mPostPaths.size());
        }

        public void addPaths(List<String> paths) {
            int startIndex = mPostPaths.size();
            mPostPaths.addAll(paths);
            notifyItemRangeInserted(startIndex, mPostPaths.size());
        }

        @Override
        public int getItemCount() {
            return mPostPaths.size();
        }

        private int getPixelsFromDps(int dps) {
            final float scale = UserDetailActivity.this.getResources().getDisplayMetrics().density;
            return (int) (dps * scale + 0.5f);
        }
    }


    static class GridImageHolder extends RecyclerView.ViewHolder {

        FrameLayout flRoot;
        ImageView ivPhoto;
        ProgressBar pbImageLoading2;

        public GridImageHolder(View view) {
            super(view);
            ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
            pbImageLoading2 = (ProgressBar)view.findViewById(R.id.pbImageLoading2);
            flRoot = (FrameLayout) view.findViewById(R.id.flRoot);
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}
