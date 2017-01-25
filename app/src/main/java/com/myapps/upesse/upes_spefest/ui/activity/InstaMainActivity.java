package com.myapps.upessefest2017.ui.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

//import butterknife.BindView;
//import butterknife.OnClick;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.myapps.upessefest2017.R;
import com.myapps.upessefest2017.auth.MainActivity;
import com.myapps.upessefest2017.ui.utils.Utils;
import com.myapps.upessefest2017.ui.view.FeedContextMenu;
import com.myapps.upessefest2017.ui.view.FeedContextMenuManager;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.myapps.upessefest2017.ui.activity.BasicImageDownloader;
import com.myapps.upessefest2017.ui.activity.BasicImageDownloader.ImageError;
import com.myapps.upessefest2017.ui.activity.BasicImageDownloader.OnImageLoaderListener;


import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

public class InstaMainActivity extends BaseDrawerActivity implements PostsFragment.OnPostSelectedListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener
//        , View.OnClickListener
{
    public static final String ACTION_SHOW_LOADING_ITEM = "action_show_loading_item";

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    Toolbar toolbar;

//    @BindView(R.id.rvFeed)
//    RecyclerView rvFeed;
    /*
    @BindView(R.id.btnCreate)
    FloatingActionButton fabCreate;
    @BindView(R.id.content)
    CoordinatorLayout clContent;
    */
    FloatingActionButton fabCreate;
    CoordinatorLayout clContent;
    ImageButton btnMore;

    boolean storage;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    //private FeedAdapter feedAdapter;
    private MenuItem inboxMenuItem;

    private boolean pendingIntroAnimation;

    private  RecyclerView my_recycler_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instamain);
        fabCreate = (FloatingActionButton)findViewById(R.id.btnCreate);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakePhotoClick();
            }
        });
        clContent= (CoordinatorLayout)findViewById(R.id.content);
        my_recycler_view = (RecyclerView)findViewById(R.id.my_recycler_view);

        //btnMore = (ImageButton)findViewById(R.id.btnMore);
        //btnMore.setOnClickListener(this);
        setupFeed();

        toolbar = getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
    }



    private void setupFeed() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };

        ViewPager viewPager = (ViewPager) findViewById(R.id.feeds_view_pager);
        FeedsPagerAdapter adapter = new FeedsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(PostsFragment.newInstance(PostsFragment.TYPE_FEED), "FEED");
        adapter.addFragment(PostsFragment.newInstance(PostsFragment.TYPE_HOME), "HOME");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.feeds_tab_layout);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout) {
            signOutConfirmation();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startIntroAnimation() {
        fabCreate.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        int actionbarSize = Utils.dpToPx(56);
        toolbar.setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        //inboxMenuItem.getActionView().setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);


        fabCreate.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();
    }

    /*
    @Override
    public void onCommentsClick(View v, int position) {
        final Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onMoreClick(View v, int itemPosition) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, itemPosition, this);
    }

    @Override
    public void onProfileClick(View v) {
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        startingLocation[0] += v.getWidth() / 2;
        UserProfileActivity.startUserProfileFromLocation(startingLocation, this);
        overridePendingTransition(0, 0);
    }
*/
    @Override
    public void onReportClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onSharePhotoClick(int feedItem) {
        //FeedContextMenuManager.getInstance().hideContextMenu();
        //Toast.makeText(InstaMainActivity.this, Integer.toString(feedItem), Toast.LENGTH_SHORT).show();
        //Toast.makeText(InstaMainActivity.this, FeedContextMenuManager.getCurrentImgUrl(), Toast.LENGTH_SHORT).show();
        boolean result = checkPermission(InstaMainActivity.this);
        storage = result;

        String imgURL = FeedContextMenuManager.getCurrentImgUrl();
        String fileName = "SAVED_IMG-"+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg";

        if(storage){
            downloadImageFromUri(imgURL,fileName);
        }
        else{
            Toast.makeText(this,"External storage permission is necessary",Toast.LENGTH_LONG).show();
        }

        /*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(FeedContextMenuManager.getCurrentImgUrl());
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        //Path to /data/data/UPES-SPE-Fest-2017/app_data/Saved_Images
        File dir = cw.getDir("Saved_Images", Context.MODE_PRIVATE);
        //Create Saved_Images dir
        String fileName = "SAVED_IMG-"+ new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg";
        final File imgPath = new File(dir,fileName);
        httpsReference.getFile(imgPath).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // Local temp file has been created
                    Toast.makeText(getApplicationContext(),"Image is saved at " + imgPath, Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Toast.makeText(getApplicationContext(),"Sorry, Image not saved!", Toast.LENGTH_LONG).show();
            }
        });
        */
    }

    private void downloadImageFromUri(String imgURL, final String fileName) {
        final TextView tvPercent = (TextView) findViewById(R.id.tvPercent);
        final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.pbImageLoading);
        pbLoading.setVisibility(View.VISIBLE);
        tvPercent.setVisibility(View.VISIBLE);
        pbLoading.bringToFront();
        tvPercent.bringToFront();

        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                Toast.makeText(InstaMainActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
            }

            @Override
            public void onProgressChange(int percent) {
                pbLoading.setProgress(percent);
                tvPercent.setText(percent + "%");
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
                        Toast.makeText(InstaMainActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        Toast.makeText(InstaMainActivity.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, false);

                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
            }
        });
        downloader.download(imgURL, true);
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((android.app.Activity) context, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((android.app.Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    android.support.v7.app.AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
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
    public void onCopyShareUrlClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }

    @Override
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }


    public void onTakePhotoClick() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.isAnonymous()) {
            Toast.makeText(InstaMainActivity.this, "You must sign-in to post.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent newPostIntent = new Intent(InstaMainActivity.this, NewPostActivity.class);
        startActivity(newPostIntent);
    }

    public void showLikedSnackbar() {
        Snackbar.make(clContent, "Liked!", Snackbar.LENGTH_SHORT).show();
    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = new Intent();
        in.setClass(context, InstaMainActivity.class);
        in.putExtra(EXTRA_IDP_RESPONSE, idpResponse);
        return in;
    }

    @Override
    public void onPostComment(String postKey) {
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra(CommentsActivity.POST_KEY_EXTRA, postKey);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }


    @Override
    public void onPostLike(final String postKey) {
        final String userKey = FirebaseUtil.getCurrentUserId();
        final DatabaseReference postLikesRef = FirebaseUtil.getLikesRef();
        postLikesRef.child(postKey).child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User already liked this post, so we toggle like off.
                    postLikesRef.child(postKey).child(userKey).removeValue();
                } else {
                    postLikesRef.child(postKey).child(userKey).setValue(ServerValue.TIMESTAMP);
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });
    }

    @Override
    public void onMoreClick(View v, int position, String thumb_url) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
        FeedContextMenuManager.setCurrentImgUrl(thumb_url);
    }


    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //startActivity(MainActivity.createIntent(ProfileActivity.this));
                            startActivity(MainActivity.createIntent(InstaMainActivity.this));
                            finish();
                        } else {
                            Toast.makeText(InstaMainActivity.this, getString(R.string.sign_out_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signOutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Do you really want to sign out?")
                .setIcon(R.drawable.nav_logout)
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(InstaMainActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                        signOut();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    /*
    @Override
    public void onClick(View view) {
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(view, itemPosition, this);
    }
    */


    class FeedsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public FeedsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
