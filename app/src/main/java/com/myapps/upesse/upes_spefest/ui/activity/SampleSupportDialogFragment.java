package com.myapps.upesse.upes_spefest.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.Models.Post;
import com.myapps.upesse.upes_spefest.ui.utils.GlideUtil;
import com.myapps.upesse.upes_spefest.ui.view.FeedContextMenuManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Simple fragment with blur effect behind.
 */
public class SampleSupportDialogFragment extends SupportBlurDialogFragment {

    /**
     * Bundle key used to start the blur dialog with a given scale factor (float).
     */
    private static final String BUNDLE_KEY_DOWN_SCALE_FACTOR = "bundle_key_down_scale_factor";

    /**
     * Bundle key used to start the blur dialog with a given blur radius (int).
     */
    private static final String BUNDLE_KEY_BLUR_RADIUS = "bundle_key_blur_radius";

    /**
     * Bundle key used to start the blur dialog with a given dimming effect policy.
     */
    private static final String BUNDLE_KEY_DIMMING = "bundle_key_dimming_effect";

    /**
     * Bundle key used to start the blur dialog with a given debug policy.
     */
    private static final String BUNDLE_KEY_DEBUG = "bundle_key_debug_effect";

    /**
     * Bundle key used for blur effect on action bar policy.
     */
    private static final String BUNDLE_KEY_BLURRED_ACTION_BAR = "bundle_key_blurred_action_bar";

    /**
     * Bundle key used for RenderScript
     */
    private static final String BUNDLE_KEY_USE_RENDERSCRIPT = "bundle_key_use_renderscript";


    /***************************************************/

    /**
     * Bundle key used for main Image
     */
    private static final String BUNDLE_KEY_SET_PHOTO = "bundle_key_set_photo";

    /**
     * Bundle key used for Caption
     */
    private static final String BUNDLE_KEY_SET_CAPTION = "bundle_key_set_caption";

    /**
     * Bundle key used for Timestamp
     */
    private static final String BUNDLE_KEY_SET_TIMESTAMP = "bundle_key_set_timestamp";

    /**
     * Bundle key used for Author_Name
     */
    private static final String BUNDLE_KEY_SET_NAME = "bundle_key_set_name";

    /**
     * Bundle key used for DP
     */
    private static final String BUNDLE_KEY_SET_DP = "bundle_key_set_dp";

    /**
     * Bundle key used for postKey
     */
    private static final String BUNDLE_KEY_SET_POST_KEY = "bundle_key_set_post_key";

    /**
     * Bundle key used for position
     */
    private static final String BUNDLE_KEY_SET_POSITION = "bundle_key_set_position";

    /**
     * Bundle key used for post
     */
    private static final String BUNDLE_KEY_SET_UID = "bundle_key_set_uid";


    boolean storage;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;
    private boolean mUseRenderScript;
    private String thumbURL;
    private String caption;
    private String timestamp;
    private String full_name;
    private String profile_picture;
    private String firstPostKey;
    private int position;
    private String uid;

    public enum LikeStatus {LIKED, NOT_LIKED}

    private static final int POST_TEXT_MAX_LINES = 6;

    /*
    private ImageView mLikeIcon;
    private ImageView mPhotoView;
    private ImageView mIconView;
    private TextView mAuthorView;
    private TextView mPostTextView;
    private TextView mTimestampView;
    private TextSwitcher mNumLikesView;
    public ImageButton btnMore;
    */

    /**
     * Retrieve a new instance of the sample fragment.
     *
     * @param radius            blur radius.
     * @param downScaleFactor   down scale factor.
     * @param dimming           dimming effect.
     * @param debug             debug policy.
     * @param mBlurredActionBar blur affect on actionBar policy.
     * @param useRenderScript   use of RenderScript
     * @param thumbUrl          main image
     * @param caption           caption
     * @param timestamp         timestamp
     * @param full_name         full_name
     * @param profile_picture   profile_picture
     * @param firstPostKey      post_key
     * @param position          position
     * @param uid               uid
     * @return well instantiated fragment.
     */
    public static SampleSupportDialogFragment newInstance(int radius,
                                                          float downScaleFactor,
                                                          boolean dimming,
                                                          boolean debug,
                                                          boolean mBlurredActionBar,
                                                          boolean useRenderScript,
                                                          String thumbUrl,
                                                          String caption,
                                                          String timestamp,
                                                          String full_name,
                                                          String profile_picture,
                                                          String firstPostKey,
                                                          int position,
                                                          String uid) {
        SampleSupportDialogFragment fragment = new SampleSupportDialogFragment();
        Bundle args = new Bundle();
        args.putInt(
                BUNDLE_KEY_BLUR_RADIUS,
                radius
        );
        args.putFloat(
                BUNDLE_KEY_DOWN_SCALE_FACTOR,
                downScaleFactor
        );
        args.putBoolean(
                BUNDLE_KEY_DIMMING,
                dimming
        );
        args.putBoolean(
                BUNDLE_KEY_DEBUG,
                debug
        );
        args.putBoolean(
                BUNDLE_KEY_BLURRED_ACTION_BAR,
                mBlurredActionBar
        );
        args.putBoolean(
                BUNDLE_KEY_USE_RENDERSCRIPT,
                useRenderScript
        );
        args.putString(
                BUNDLE_KEY_SET_PHOTO,
                thumbUrl
        );
        args.putString(
                BUNDLE_KEY_SET_CAPTION,
                caption
        );
        args.putString(
                BUNDLE_KEY_SET_TIMESTAMP,
                timestamp
        );
        args.putString(
                BUNDLE_KEY_SET_NAME,
                full_name
        );
        args.putString(
                BUNDLE_KEY_SET_DP,
                profile_picture
        );
        args.putString(
                BUNDLE_KEY_SET_POST_KEY,
                firstPostKey
        );
        args.putInt(
                BUNDLE_KEY_SET_POSITION,
                position
        );
        args.putString(
                BUNDLE_KEY_SET_UID,
                uid
        );

        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Bundle args = getArguments();
        mRadius = args.getInt(BUNDLE_KEY_BLUR_RADIUS);
        mDownScaleFactor = args.getFloat(BUNDLE_KEY_DOWN_SCALE_FACTOR);
        mDimming = args.getBoolean(BUNDLE_KEY_DIMMING);
        mDebug = args.getBoolean(BUNDLE_KEY_DEBUG);
        mBlurredActionBar = args.getBoolean(BUNDLE_KEY_BLURRED_ACTION_BAR);
        mUseRenderScript = args.getBoolean(BUNDLE_KEY_USE_RENDERSCRIPT);
        thumbURL = args.getString(BUNDLE_KEY_SET_PHOTO);
        caption = args.getString(BUNDLE_KEY_SET_CAPTION);
        timestamp = args.getString(BUNDLE_KEY_SET_TIMESTAMP);
        full_name = args.getString(BUNDLE_KEY_SET_NAME);
        profile_picture = args.getString(BUNDLE_KEY_SET_DP);
        firstPostKey = args.getString(BUNDLE_KEY_SET_POST_KEY);
        position = args.getInt(BUNDLE_KEY_SET_POSITION);
        uid = args.getString(BUNDLE_KEY_SET_UID);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final ArrayList<String> followers = new ArrayList<String>();

        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment, null);
        //TextView label = ((TextView) view.findViewById(R.id.textView));
        //label.setMovementMethod(LinkMovementMethod.getInstance());
        //Linkify.addLinks(label, Linkify.WEB_URLS);

        ImageView mPhotoView = (ImageView) view.findViewById(R.id.ivFeedCenter);
        ProgressBar pbImageLoading4 = (ProgressBar) view.findViewById(R.id.pbImageLoading4);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                GlideUtil.loadImage(thumbURL, mPhotoView, pbImageLoading4);
            } else {
                GlideUtil.loadImage(thumbURL, mPhotoView);
            }

        } catch (Exception e) {
        }

        ImageView mIconView = (ImageView) view.findViewById(R.id.post_author_icon);
        if (profile_picture == null) {
            mIconView.setImageResource(R.drawable.ic_person_outline_black);
        } else {
            try {
                GlideUtil.loadProfileIcon(profile_picture, mIconView);
            } catch (Exception e) {
            }
        }

        TextView mAuthorView = (TextView) view.findViewById(R.id.post_author_name);
        if (full_name == null || full_name.isEmpty()) {
            full_name = "Anonymous";
        }
        mAuthorView.setText(full_name);

        final TextView mPostTextView = (TextView) view.findViewById(R.id.post_text);
        if (caption == null || caption.isEmpty()) {
            mPostTextView.setVisibility(View.GONE);
        } else {
            mPostTextView.setVisibility(View.VISIBLE);
            mPostTextView.setText(caption);
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

        TextView mTimestampView = (TextView) view.findViewById(R.id.post_timestamp);
        mTimestampView.setText(timestamp);

        final TextSwitcher mNumLikesView = (TextSwitcher) view.findViewById(R.id.tsLikesCounter);

        //ImageButton btnMore = (ImageButton) view.findViewById(R.id.btnMore);

        final ImageView mLikeIcon = (ImageView) view.findViewById(R.id.btnLike);


        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String userKey = FirebaseUtil.getCurrentUserId();
                final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
                postLikesRef.child(firstPostKey).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // User already liked this post, so we toggle like off.
                            postLikesRef.child(firstPostKey).child(userKey).removeValue();
                        } else {
                            postLikesRef.child(firstPostKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError firebaseError) {

                    }
                });
            }
        });

        view.findViewById(R.id.btnComments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), CommentsActivity.class);
                intent.putExtra(CommentsActivity.POST_KEY_EXTRA, firstPostKey);
                intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, 0);
                startActivity(intent);

            }
        });

        ValueEventListener likeListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //setNumLikes(dataSnapshot.getChildrenCount());
                String suffix = dataSnapshot.getChildrenCount() == 1 ? " like" : " likes";
                mNumLikesView.setText(dataSnapshot.getChildrenCount() + suffix);
                if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
                    mLikeIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_heart_red));

                } else {
                    mLikeIcon.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_heart_outline_grey));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        FirebaseUtil.getLikesRef().child(firstPostKey).addValueEventListener(likeListener);


        ImageButton btnMoreSave = (ImageButton) view.findViewById(R.id.btnMoreSave);
        btnMoreSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = checkPermission(view.getContext());
                storage = result;

                String imgURL = thumbURL;
                String fileName = "SAVED_IMG-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg";

                if (storage) {
                    downloadImageFromUri(imgURL, fileName);
                } else {
                    Toast.makeText(getContext(), "External storage permission is necessary", Toast.LENGTH_LONG).show();
                }
            }
        });

        FirebaseUtil.getFollowersRef().child(user.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot postSnapshot, String s) {
                followers.add(postSnapshot.getKey());
                /*
                Toast.makeText(getContext(), String.valueOf(followers.size()), Toast.LENGTH_LONG).show();
                for (int i = 0; i < followers.size(); i++) {
                    Toast.makeText(getContext(), String.valueOf(String.valueOf(followers.get(i))), Toast.LENGTH_LONG).show();
                }
                */

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


        ImageButton btnMoreDelete = (ImageButton) view.findViewById(R.id.btnMoreDelete);
        final String postUID = uid;

        if (!((user.getUid().contentEquals(postUID)) || (user.getUid() == postUID))) {
            btnMoreDelete.setVisibility(View.GONE);
        } else {

            btnMoreDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if ((user.getUid().contentEquals(postUID)) || (user.getUid() == postUID)) {
                        try {
                            new android.support.v7.app.AlertDialog.Builder(getContext())
                                    .setTitle("Confirm Delete")
                                    .setMessage("Do you really want to delete this post?")
                                    .setIcon(R.drawable.delete_forever)
                                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            String postKey = firstPostKey;
                                            Post post = FeedContextMenuManager.getCurrentPost();
                                            //Toast.makeText(view.getContext(), post.getThumb_url(), Toast.LENGTH_LONG).show();
                                            FirebaseUtil.getPostsRef().child(postKey).removeValue();
                                            FirebaseUtil.getPeopleRef().child(user.getUid()).child("posts").child(postKey).removeValue();
                                            final String finalPostKey = postKey;
                                            /*
                                            FirebaseUtil.getFeedRef().addValueEventListener(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            Toast.makeText(getContext(), "Deleting " + finalPostKey, Toast.LENGTH_LONG).show();
                                                            DeletePosts((Map<String, Object>) dataSnapshot.getValue(), finalPostKey);
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                            //handle databaseError
                                                        }
                                                    });
                                                    */
                                            for (int i = 0; i < followers.size(); i++) {
                                                FirebaseUtil.getFeedRef().child(String.valueOf(followers.get(i))).child(postKey).removeValue();
                                                //Toast.makeText(getContext(), "For: " + String.valueOf(followers.get(i)), Toast.LENGTH_LONG).show();
                                                /*
                                                HashMap<String, Object> DelPost = new HashMap<String, Object>();
                                                DelPost.put(postKey, null);
                                                FirebaseUtil.getFeedRef().child(String.valueOf(followers.get(i))).updateChildren(DelPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Deleted " + finalPostKey, Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                                */
                                            }

                                            FirebaseUtil.getLikesRef().child(postKey).removeValue();
                                            FirebaseUtil.getCommentsRef().child(postKey).removeValue();
                                            post = null;
                                            postKey = null;
                                            //Intent intent = new Intent(getContext(), UserDetailActivity.class);
                                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            //startActivity(intent);
                                            //finish();
                                            getActivity().onBackPressed();

                                            Toast.makeText(getContext(), "Post Deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, null).show();

                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Cannot delete other users' posts.", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }

        builder.setView(view);
        return builder.create();
    }

    private void DeletePosts(Map<String, Object> posts, String finalPostKey) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : posts.entrySet()) {
            //Get user map
            Map singlePost = (Map) entry.getValue();
            String uname = (String) singlePost.get(finalPostKey);

            singlePost.remove(finalPostKey);
            Toast.makeText(getContext(), "Deleted " + finalPostKey, Toast.LENGTH_LONG).show();

        }
    }

    private void downloadImageFromUri(String imgURL, final String fileName) {
        final ProgressDialog pd;
        pd = new ProgressDialog(getContext());
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("Saving image...");
        pd.setMax(100);
        pd.show();

        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(BasicImageDownloader.ImageError error) {
                Toast.makeText(getContext(), "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                pd.dismiss();
            }

            @Override
            public void onProgressChange(int percent) {
                pd.setProgress(percent);
            }

            @Override
            public void onComplete(Bitmap result) {
                        /* save the image - I'm gonna use JPEG */
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                        /* don't forget to include the extension into the file name */
                final File myImageFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + "UPES-SPE-Fest-Saved-Images" + File.separator + fileName + "." + mFormat.name().toLowerCase());
                BasicImageDownloader.writeToDisk(myImageFile, result, new BasicImageDownloader.OnBitmapSaveListener() {
                    @Override
                    public void onBitmapSaved() {
                        Toast.makeText(getContext(), "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(BasicImageDownloader.ImageError error) {
                        Toast.makeText(getContext(), "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, false);

                pd.dismiss();
            }
        });
        downloader.download(imgURL, true);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    storage = true;
                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    protected boolean isDebugEnable() {
        return mDebug;
    }

    @Override
    protected boolean isDimmingEnable() {
        return mDimming;
    }

    @Override
    protected boolean isActionBarBlurred() {
        return mBlurredActionBar;
    }

    @Override
    protected float getDownScaleFactor() {
        return mDownScaleFactor;
    }

    @Override
    protected int getBlurRadius() {
        return mRadius;
    }

    @Override
    protected boolean isRenderScriptEnable() {
        return mUseRenderScript;
    }

}
