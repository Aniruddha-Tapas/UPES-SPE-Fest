package com.myapps.upesse.upes_spefest.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.myapps.upesse.upes_spefest.auth.MainActivity;
import com.myapps.upesse.upes_spefest.events.ConferencesActivity;
import com.myapps.upesse.upes_spefest.events.EventsMainActivity;
import com.myapps.upesse.upes_spefest.events.ScheduleActivity;
import com.myapps.upesse.upes_spefest.ui.utils.GlideUtil;

//import butterknife.BindDimen;
//import butterknife.BindView;

import com.myapps.upesse.upes_spefest.R;

import java.util.HashMap;
import java.util.Map;

public class BaseDrawerActivity extends BaseActivity
//        implements MenuItem.OnMenuItemClickListener
{

    /*
    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;
    @BindView(R.id.vNavigation)
    NavigationView vNavigation;

    @BindDimen(R.dimen.global_menu_avatar_size)
    int avatarSize;
    */
    DrawerLayout drawerLayout;
    NavigationView vNavigation;
    int avatarSize;
    /*
    protected ActionBarDrawerToggle mActionBarDrawerToggle = null;
    protected SearchView mSearchView = null;
    */

    //@BindString(R.string.user_profile_photo)
    //String profilePhoto;
    //private MenuItem inboxMenuItem;

    //Cannot be bound via Butterknife, hosting view is initialized later (see setupHeader() method)
    private ImageView ivMenuUserProfilePhoto;
    private TextView ivMenuUserName;

    private ImageButton fb,insta,youtube;


    //Firebase stuff
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 103;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    private static final String TAG_HOME = "menu_feed";
    private static final String TAG_PHOTOS = "photos";
    private static final String TAG_MOVIES = "movies";
    private static final String TAG_NOTIFICATIONS = "notifications";
    private static final String TAG_SETTINGS = "settings";
    public static String CURRENT_TAG = TAG_HOME;



    @Override
    public void setContentView(int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);

        ViewGroup viewGroup = (ViewGroup) findViewById(R.id.flContentRoot);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        // Initialize authentication and set up callbacks
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(MainActivity.createIntent(this));
            finish();
            return;
        }

        bindViews();
        drawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        /*
        setupToolbar();

        if (drawerLayout  != null && getToolbar() != null) {
            mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, getToolbar(), R.string.open, R.string.close) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (mSearchView != null && mSearchView.isSearchOpen()) {
                        mSearchView.close(true);
                    }
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);

                }
            };
            drawerLayout.addDrawerListener(mActionBarDrawerToggle);
            mActionBarDrawerToggle.syncState();
        }
        */

        vNavigation = (NavigationView)findViewById(R.id.vNavigation);
        avatarSize = R.dimen.global_menu_avatar_size;
        fb = (ImageButton)findViewById(R.id.fb);
        insta = (ImageButton)findViewById(R.id.insta);
        youtube = (ImageButton)findViewById(R.id.youtube);

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "FB", Toast.LENGTH_LONG).show();
                Intent faceb = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/upes.spe/"));
                startActivity(faceb);
            }
        });
        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "FB", Toast.LENGTH_LONG).show();
                Intent ins = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/spe_upessc/"));
                startActivity(ins);
            }
        });
        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "FB", Toast.LENGTH_LONG).show();

                Intent yt = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/UC-ChMz7E9sL86L7PpxYV9tg"));
                startActivity(yt);
            }
        });


        setupHeader();

        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_feed:
                        navItemIndex = 0;
                        drawerLayout.closeDrawer(Gravity.LEFT,true);
                        //Toast.makeText(getApplicationContext(), "Feed", Toast.LENGTH_LONG).show();
                        //CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.menu_upload:
                        navItemIndex = 1;
                        //CURRENT_TAG = TAG_PHOTOS;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user == null || user.isAnonymous()) {
                            Toast.makeText(getApplicationContext(), "You must sign-in to post.", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Intent newPostIntent = new Intent(getApplicationContext(), NewPostActivity.class);
                        startActivity(newPostIntent);
                        break;
                    case R.id.menu_schedule:
                        navItemIndex = 7;
                        startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
                        //drawer.closeDrawers();
                        return true;
                    case R.id.menu_events:
                        navItemIndex = 2;
                        //CURRENT_TAG = TAG_MOVIES;
                        startActivity(new Intent(getApplicationContext(), EventsMainActivity.class));
                        //drawer.closeDrawers();
                        return true;
                    case R.id.menu_conferences:
                        navItemIndex = 3;
                        //CURRENT_TAG = TAG_NOTIFICATIONS;
                        startActivity(new Intent(getApplicationContext(), ConferencesActivity.class));
                        //drawer.closeDrawers();
                        return true;
                    case R.id.about:
                        navItemIndex = 5;
                        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                        //drawer.closeDrawers();
                        return true;
                    case R.id.menu_signout:
                        navItemIndex = 6;
                        signOutConfirmation();
                        break;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                //loadHomeFragment();

                return true;
            }
        });

        /*
        if (mActionBarDrawerToggle != null) {
            mActionBarDrawerToggle.syncState();
        }
        */
    }


    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //startActivity(MainActivity.createIntent(ProfileActivity.this));
                            startActivity(MainActivity.createIntent(BaseDrawerActivity.this));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), getString(R.string.sign_out_failed), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        if (getToolbar() != null) {
            getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
        }
    }

/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }
*/


    private void signOutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Sign Out")
                .setMessage("Do you really want to sign out?")
                .setIcon(R.drawable.nav_logout)
                .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(BaseDrawerActivity.this, "Signed Out!", Toast.LENGTH_SHORT).show();
                        signOut();
                    }
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void setupHeader() {
        View headerView = vNavigation.getHeaderView(0);
        ivMenuUserProfilePhoto = (ImageView) headerView.findViewById(R.id.ivMenuUserProfilePhoto);
        ivMenuUserName = (TextView) headerView.findViewById(R.id.ivMenuUserName);
        headerView.findViewById(R.id.vGlobalMenuHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onGlobalMenuHeaderClick(v);
            }
        });

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser.getDisplayName() != null) {
            ivMenuUserName.setText(firebaseUser.getDisplayName());
        }

        if (firebaseUser.getPhotoUrl() != null) {
            try{
                GlideUtil.loadProfileIcon(firebaseUser.getPhotoUrl().toString(), ivMenuUserProfilePhoto);
            }catch (Exception e){}
        }
        Map<String, Object> updateValues = new HashMap<>();
        updateValues.put("displayName", firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "Anonymous");
        updateValues.put("photoUrl", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);

        FirebaseUtil.getPeopleRef().child(firebaseUser.getUid()).updateChildren(
                updateValues,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
                        if (firebaseError != null) {
                            Toast.makeText(BaseDrawerActivity.this,
                                    "Couldn't save user data: " + firebaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void onGlobalMenuHeaderClick(final View v) {
        drawerLayout.closeDrawer(Gravity.LEFT);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                int[] startingLocation = new int[2];
                v.getLocationOnScreen(startingLocation);
                startingLocation[0] += v.getWidth() / 2;
                //UserProfileActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                UserDetailActivity.startUserProfileFromLocation(startingLocation, BaseDrawerActivity.this);
                */
                Intent userDetailIntent = new Intent(getApplicationContext(), UserDetailActivity.class);
                userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, mAuth.getCurrentUser().getUid());

                startActivity(userDetailIntent);

                overridePendingTransition(0, 0);
            }
        }, 200);
    }

}
