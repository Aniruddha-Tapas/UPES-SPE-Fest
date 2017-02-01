package com.myapps.upesse.upes_spefest.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import butterknife.BindView;
//import butterknife.OnClick;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
//import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.auth.MainActivity;
import com.myapps.upesse.upes_spefest.ui.Models.Post;
import com.myapps.upesse.upes_spefest.ui.utils.Utils;
import com.myapps.upesse.upes_spefest.ui.view.FeedContextMenu;
import com.myapps.upesse.upes_spefest.ui.view.FeedContextMenuManager;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import com.myapps.upesse.upes_spefest.ui.activity.BasicImageDownloader.ImageError;


import static com.firebase.ui.auth.ui.ExtraConstants.EXTRA_IDP_RESPONSE;

public class InstaMainActivity extends BaseDrawerActivity implements PostsFragment.OnPostSelectedListener,
        FeedContextMenu.OnFeedContextMenuItemClickListener {
    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    Toolbar toolbar;
    TextView homeFeed;
    FloatingActionButton fabCreate;
    CoordinatorLayout clContent;
    ImageButton btnMore;

    boolean storage;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private boolean pendingIntroAnimation;
    private RecyclerView my_recycler_view;
    private ProgressDialog pd;

    private FirebaseUser user;

    private MenuItem searchItem;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;
    private ArrayList<String> queries;
    private ArrayList<String> followers;

    ListView list;
    ListViewAdapter adapter;
    private ArrayList<String> userNames;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instamain);
        fabCreate = (FloatingActionButton) findViewById(R.id.btnCreate);
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onTakePhotoClick();
            }
        });
        clContent = (CoordinatorLayout) findViewById(R.id.content);
        my_recycler_view = (RecyclerView) findViewById(R.id.my_recycler_view);
        homeFeed = (TextView) findViewById(R.id.homeFeed);

        user = FirebaseAuth.getInstance().getCurrentUser();
        followers = new ArrayList<String>();

        FirebaseUtil.getPeopleRef().child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("following")) {
                    PostsFragment.setToast(false);
                    //Toast.makeText(getApplicationContext(), String.valueOf(dataSnapshot.getChildrenCount()), Toast.LENGTH_LONG).show();
                } else {
                    PostsFragment.setToast(true);

                    Snackbar sb = Snackbar.make(findViewById(R.id.content), R.string.homefeedwarning, Snackbar.LENGTH_LONG)
                            .setAction("CLOSE", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
                    View snackbarView = sb.getView();
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setMaxLines(4);
                    sb.show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (isConnected()) {
            Snackbar sb = Snackbar.make(findViewById(R.id.content), "Connected *_*", Snackbar.LENGTH_SHORT)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
            sb.show();
            if (!(isOnline())) {
                Snackbar.make(findViewById(R.id.content), "No internet access!", Snackbar.LENGTH_LONG)
                        .setAction("CLOSE", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                        .show();
            }
        }

        if (!(isOnline() && isConnected())) {
            Snackbar.make(findViewById(R.id.content), "No internet access!", Snackbar.LENGTH_LONG)
                    .setAction("CLOSE", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                    .show();
            homeFeed.setVisibility(View.VISIBLE);
            homeFeed.bringToFront();
            homeFeed.setText("Please check your Internet connection.");
        } else {
            PostsFragment.showDialog(this);
        }

        /*
        if (FirebaseUtil.getPeopleRef().child(user.getUid()).child("following").getKey() != null) {
            PostsFragment.setToast(false);
            Toast.makeText(this, "Following", Toast.LENGTH_LONG).show();
        } else {
            PostsFragment.setToast(true);
        }
        */

        /*
        FirebaseUtil.getPeopleRef().child(user.getUid()).child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PostsFragment.setToast(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        */
        setupFeed();

        toolbar = getToolbar();
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        Toast.makeText(getApplicationContext(), "Toast 1", Toast.LENGTH_SHORT).show();

        /*

        //SEARCH VIEW
        //suggestions = new SearchRecentSuggestions(this, SuggestionProvider.AUTHORITY,SuggestionProvider.MODE);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //Log.d("onCreateOptionsMenu() searchManager: {}", searchManager);

        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setEnabled(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(true);
                //addTextChangedListener
        //searchView.setMaxWidth(1000);
        searchView.setMaxWidth(Utils.getScreenWidth(this));

        Toast.makeText(getApplicationContext(), "Toast 2", Toast.LENGTH_SHORT).show();

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);

        // Collapse the search menu when the user hits the back key
        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                //log.trace("onFocusChange(): " + hasFocus);
                if (!hasFocus)
                    showSearch(false);
            }
        });

        try {
            // This sets the cursor
            // resource ID to 0 or @null
            // which will make it visible
            // on white background
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");

            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);

        } catch (Exception e) {
        }
        */

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        /*
        for (int i = 0; i < 10; i++) {
            String query = SuggestionProvider.generateRandomSuggestion();
            suggestions.saveRecentQuery(query, null);
        }
        */


        DatabaseReference ref = FirebaseUtil.getBaseRef();
        DatabaseReference peopleref = FirebaseUtil.getPeopleRef();
        //Get datasnapshot at your "users" root node
        //peopleref.addListenerForSingleValueEvent(


        Toast.makeText(getApplicationContext(), "Toast 3", Toast.LENGTH_SHORT).show();

        peopleref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        //ArrayList<String> userlist = collectUsersList((Map<String, Object>) dataSnapshot.getValue());

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            //CoLists.add(String.valueOf(dsp.getValue()));
                            //Toast.makeText(getApplicationContext(), dsp.getValue().toString(), Toast.LENGTH_SHORT).show();
                        }

                        //queries = collectUsersList((Map<String, Object>) dataSnapshot.getValue());
                        collectUsersList((Map<String, Object>) dataSnapshot.getValue());
                        Toast.makeText(getApplicationContext(), "Collect Users List Done", Toast.LENGTH_LONG).show();
                        //searchView.setEnabled(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        //list = (ListView) findViewById(R.id.listview);
        /*
        // Pass results to ListViewAdapter Class
        if(queries!=null || !(queries.isEmpty())) {
            adapter = new ListViewAdapter(this, queries);
        }

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
                if(adapter!=null)
                    adapter.filter(newText);
                return false;
            }
        });

        */

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


        /*
        if(!(queries == null)){
            if(!(queries.isEmpty())){
                for (int i = 0; i < queries.size(); i++) {
                    suggestions.saveRecentQuery(queries.get(i), null);
                    Toast.makeText(this, i + queries.get(i), Toast.LENGTH_SHORT).show();
                }
            }
        }
        */

        /*
        ArrayList<String> queries =new ArrayList<String>();
        queries.addAll(SuggestionProvider.getFirebaseUsers());
        */

        /*
        if(queries!=null)
            Toast.makeText(this,queries.size(),Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this,"queries null",Toast.LENGTH_SHORT).show();
            */

        /*
        for (int i = 0; i < queries.size(); i++) {
            suggestions.saveRecentQuery(queries.get(i), null);
            Toast.makeText(this,i + queries.get(i),Toast.LENGTH_SHORT).show();
        }
        */

        /*
        //String query = SuggestionProvider.generateRandomSuggestion();
        ArrayList<String> queries = SuggestionProvider.getFirebaseUsers();
        for (int i = 0; i < queries.size(); i++) {
            suggestions.saveRecentQuery(queries.get(i), null);
        }
        */

        Toast.makeText(getApplicationContext(), "Toast 4", Toast.LENGTH_SHORT).show();

    }

    //private ArrayList<String> collectUsersList(Map<String, Object> people) {
    private void collectUsersList(Map<String, Object> people) {

        userNames = new ArrayList<String>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : people.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get UserNames and append to list
            String uname = (String) singleUser.get("displayName");
            if (!((uname == "Anonymous") || (uname.equals("Anonymous")))) {
                //userNames.add(uname);
                userNames.add(uname);
                //Toast.makeText(this, uname, Toast.LENGTH_SHORT).show();
                //SuggestionProvider.addFirebaseUsers(uname);

            }
        }

        Toast.makeText(this, String.valueOf(userNames.size()), Toast.LENGTH_SHORT).show();

        //SuggestionProvider.setFirebase_users(userNames);
        if (!(userNames == null)) {
            if (!(userNames.isEmpty())) {
                for (int i = 0; i < userNames.size(); i++) {
                    //suggestions.saveRecentQuery(userNames.get(i), null);
                    //Toast.makeText(this, i + " " + userNames.get(i), Toast.LENGTH_SHORT).show();

                }

                /*
                mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                mActionBarDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
                mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawerLayout.openDrawer(GravityCompat.START); // WITHOUT finish(); + finish();
                    }
                });


                setSearchView(userNames);
                mSearchView.setArrowOnly(false);
                // mSearchView.setGoogleIcons();
                //customSearchView();


                // Pass results to ListViewAdapter Class
                adapter = new ListViewAdapter(this, userNames);

                // Binds the Adapter to the ListView
                list.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Toast.makeText(getApplicationContext(), newText, Toast.LENGTH_LONG).show();
                        if(adapter!=null)
                            adapter.filter(newText);
                        list.invalidate();
                        return true;
                    }
                });
                */

            }
        }

        //return userNames;
    }

    /*
    protected void setSearchView(List<SearchItem> userNames) {
        mSearchView = (com.lapism.searchview.SearchView) findViewById(R.id.searchView);
        if (mSearchView != null) {
            mSearchView.setHint("Search");
            mSearchView.setOnQueryTextListener(new com.lapism.searchview.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    getData(query, 0);
                    mSearchView.close(false);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            mSearchView.setOnOpenCloseListener(new com.lapism.searchview.SearchView.OnOpenCloseListener() {
                @Override
                public boolean onOpen() {
                    return true;
                }

                @Override
                public boolean onClose() {
                    return true;
                }
            });
            mSearchView.setVoiceText("Set permission on Android 6.0+ !");
            mSearchView.setOnVoiceClickListener(new com.lapism.searchview.SearchView.OnVoiceClickListener() {
                @Override
                public void onVoiceClick() {
                    // permission
                }
            });

            /*
            List<SearchItem> suggestionsList = new ArrayList<>();
            suggestionsList.add(new SearchItem("search1"));
            suggestionsList.add(new SearchItem("search2"));
            suggestionsList.add(new SearchItem("search3"));
            */

            /*
            SearchAdapter searchAdapter = new SearchAdapter(this, userNames);
            searchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                    String query = textView.getText().toString();
                    getData(query, position);
                    mSearchView.close(false);
                }
            });
            mSearchView.setAdapter(searchAdapter);

            /*suggestionsList.add(new SearchItem("search12"));
            suggestionsList.add(new SearchItem("search22"));
            suggestionsList.add(new SearchItem("search32"));
            searchAdapter.notifyDataSetChanged();*/
            /*
            List<SearchFilter> filter = new ArrayList<>();
            filter.add(new SearchFilter("Filter1", true));
            filter.add(new SearchFilter("Filter2", true));
            mSearchView.setFilters(filter);
            //use mSearchView.getFiltersStates() to consider filter when performing search

        }
    }



    protected void customSearchView() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && mSearchView != null) {
            mSearchView.setVersion(extras.getInt(EXTRA_KEY_VERSION));
            mSearchView.setVersionMargins(extras.getInt(EXTRA_KEY_VERSION_MARGINS));
            mSearchView.setTheme(extras.getInt(EXTRA_KEY_THEME), true);
            mSearchView.setQuery(extras.getString(EXTRA_KEY_TEXT), false);
            // mSearchView.setTextOnly();
        }

    }
    @CallSuper
    protected void getData(String text, int position) {

        mHistoryDatabase.addItem(new SearchItem(text));

        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        intent.putExtra(EXTRA_KEY_VERSION, com.lapism.searchview.SearchView.VERSION_TOOLBAR);
        intent.putExtra(EXTRA_KEY_VERSION_MARGINS, com.lapism.searchview.SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
        intent.putExtra(EXTRA_KEY_THEME, com.lapism.searchview.SearchView.THEME_LIGHT);
        intent.putExtra(EXTRA_KEY_TEXT, text);
        startActivity(intent);

        Toast.makeText(getApplicationContext(), text + ", position: " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == com.lapism.searchview.SearchView.SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String searchWrd = results.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    if (mSearchView != null) {
                        mSearchView.setQuery(searchWrd, true);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/


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

        if ((isOnline() && isConnected())) {
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0 || tab.getText() == "FEED") {
                        homeFeed.setVisibility(View.INVISIBLE);
                    } else if (tab.getPosition() == 1 || tab.getText() == "HOME") {
                        if (PostsFragment.isToast()) {
                            homeFeed.setVisibility(View.VISIBLE);
                            //homeFeed.bringToFront();
                        } else
                            homeFeed.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0 || tab.getText() == "FEED") {
                        homeFeed.setVisibility(View.INVISIBLE);
                    } else if (tab.getPosition() == 1 || tab.getText() == "HOME") {
                        if (PostsFragment.isToast())
                            homeFeed.setVisibility(View.VISIBLE);
                        else
                            homeFeed.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0 || tab.getText() == "FEED") {
                        homeFeed.setVisibility(View.INVISIBLE);
                    } else if (tab.getPosition() == 1 || tab.getText() == "HOME") {
                        if (PostsFragment.isToast())
                            homeFeed.setVisibility(View.VISIBLE);
                        else
                            homeFeed.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        //searchItem = menu.add(android.R.string.search_go);
        //searchItem.setIcon(R.drawable.ic_search_white_48dp);


        //searchItem = menu.findItem(R.id.searchViewDemo);
        //MenuItemCompat.setActionView(searchItem, mSearchView);
        //MenuItemCompat.setShowAsAction(searchItem,MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);


        //menu.add(0, R.id.menu_about, 0, R.string.lbl_about);

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
        } else if (id == R.id.searchViewDemo) {
            //mSearchView.open(true, item);
            if (userNames != null) {
                final SearchUsersFragment fragment1
                        = SearchUsersFragment.newInstance(
                        //mBlurRadiusSeekbar.getProgress() + 1,
                        10,
                        //(mDownScaleFactorSeekbar.getProgress() / 10f) + 2,
                        5,
                        //mDimmingEnable.isChecked(),
                        true,
                        //mDebugMode.isChecked(),
                        false,
                        //mBlurredActionBar.isChecked(),
                        true,
                        //mUseRenderScript.isChecked()
                        false,

                        userNames
                );

                Toast.makeText(this, String.valueOf(userNames.size()), Toast.LENGTH_SHORT).show();
                fragment1.show(getSupportFragmentManager(), "blur_sample");
            } else {
                Toast.makeText(this, "Loading all users. Please wait.", Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);

    }

    /*
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //log.warn("onNewIntent() :{}", intent);
        showSearch(false);
        Bundle extras = intent.getExtras();
        final String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
        final String query = String.valueOf(extras.get(SearchManager.QUERY));

        //log.debug("query: {} user_query: {}", query, userQuery);
        Toast.makeText(this, "query: " + query + " user_query: " + userQuery, Toast.LENGTH_SHORT).show();

        DatabaseReference ref = FirebaseUtil.getBaseRef();
        DatabaseReference peopleref = FirebaseUtil.getPeopleRef();
        peopleref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //iterate through each user, ignoring their UID
                        Map<String, Object> people = (Map<String, Object>) dataSnapshot.getValue();
                        for (Map.Entry<String, Object> entry : people.entrySet()) {
                            //Get user map
                            Map singleUser = (Map) entry.getValue();
                            //Get UserNames and append to list
                            String uname = (String) singleUser.get("displayName");
                            if ((((uname == query) || (uname == userQuery)
                                    || (uname.equals(query)) || (uname.equals(userQuery))))) {
                                ///userNames.add(uname);
                                //Toast.makeText(this, uname, Toast.LENGTH_SHORT).show();
                                //SuggestionProvider.addFirebaseUsers(uname);
                                String unid = entry.getKey();
                                //Toast.makeText(getApplicationContext(), "UID : " + unid, Toast.LENGTH_SHORT).show();
                                if (unid != null) {
                                    Context context = InstaMainActivity.this;
                                    Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
                                    userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME,
                                            unid);
                                    context.startActivity(userDetailIntent);
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    protected void showSearch(boolean visible) {
        if (visible) {
            MenuItemCompat.expandActionView(searchItem);


            //list.setVisibility(View.VISIBLE);
            //list.bringToFront();
            //list.setEnabled(true);
            //list.setItemsCanFocus(true);


        }
        else
            MenuItemCompat.collapseActionView(searchItem);
            //list.setVisibility(View.GONE);
    }


    @Override
    public boolean onSearchRequested() {
        //log.trace("onSearchRequested();");
        showSearch(true);

        // dont show the built-in search dialog
        return false;
    }
    */


    public boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
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
    public void onDeleteClick(int feedItem) {
        String postUID = FeedContextMenuManager.getCurrentPostUid();
        if ((user.getUid().contentEquals(postUID)) || (user.getUid() == postUID)) {

            Toast.makeText(InstaMainActivity.this, "That's my post.", Toast.LENGTH_LONG).show();
            try {
                final DatabaseReference ref = FirebaseUtil.getBaseRef();
                new AlertDialog.Builder(this)
                        .setTitle("Confirm Delete")
                        .setMessage("Do you really want to delete this post?")
                        .setIcon(R.drawable.delete_forever)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Post post = FeedContextMenuManager.getCurrentPost();
                                String postKey = FeedContextMenuManager.getCurrentPostKey();
                                FirebaseUtil.getPostsRef().child(postKey).removeValue();
                                FirebaseUtil.getPeopleRef().child(user.getUid()).child("posts").child(postKey).removeValue();
                                final String finalPostKey = postKey;
                                for (int i = 0; i < followers.size(); i++) {
                                    FirebaseUtil.getFeedRef().child(String.valueOf(followers.get(i))).child(postKey).removeValue();
                                    //Toast.makeText(getApplicationContext(), "For: " + String.valueOf(followers.get(i)), Toast.LENGTH_LONG).show();
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
                                FeedContextMenuManager.getInstance().hideContextMenu();
                                Toast.makeText(InstaMainActivity.this, "Post Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        /*
        Toast.makeText(this,user.getDisplayName(),Toast.LENGTH_LONG).show();
        Toast.makeText(this,user.getEmail(),Toast.LENGTH_LONG).show();
        Toast.makeText(this,user.getUid(),Toast.LENGTH_LONG).show();
        if(user.getPhotoUrl() == null) {
            //user.getPhotoUrl()
            Uri uri = Uri.parse("android.resource://"+this.getPackageName()+"/"+R.drawable.ic_person_outline_black);
            Toast.makeText(this,uri.toString(),Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, user.getPhotoUrl().toString(), Toast.LENGTH_LONG).show();
        }
        //FeedContextMenuManager.getInstance().hideContextMenu();
        */
    }

    private void DeletePosts(Map<String, Object> posts, String finalPostKey) {
        List<String> userNames = new ArrayList<>();
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : posts.entrySet()) {
            //Get user map
            Map singlePost = (Map) entry.getValue();
            //String uname = (String) singlePost.get(finalPostKey);
            if (singlePost.get(finalPostKey) != null) {
                singlePost.remove(finalPostKey);
            }
        }
    }

    @Override
    public void onSavePhotoClick(int feedItem) {
        //FeedContextMenuManager.getInstance().hideContextMenu();
        //Toast.makeText(InstaMainActivity.this, Integer.toString(feedItem), Toast.LENGTH_SHORT).show();
        //Toast.makeText(InstaMainActivity.this, FeedContextMenuManager.getCurrentImgUrl(), Toast.LENGTH_SHORT).show();


        String imgURL = FeedContextMenuManager.getCurrentImgUrl();
        String fileName = "SAVED_IMG-" + new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) + ".jpg";

        if (storage) {
            downloadImageFromUri(imgURL, fileName);
        } else {
            Toast.makeText(this, "External storage permission is necessary", Toast.LENGTH_LONG).show();
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

        pd = new ProgressDialog(this);
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(false);
        pd.setIndeterminate(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //pd.setProgressDrawable(getResources().getDrawable(R.drawable.progress));
        pd.setMessage("Saving image...");
        pd.setMax(100);
        pd.show();

        final BasicImageDownloader downloader = new BasicImageDownloader(new BasicImageDownloader.OnImageLoaderListener() {
            @Override
            public void onError(ImageError error) {
                Toast.makeText(InstaMainActivity.this, "Error code " + error.getErrorCode() + ": " +
                        error.getMessage(), Toast.LENGTH_LONG).show();
                error.printStackTrace();
                /*
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
                */
                pd.dismiss();
            }

            @Override
            public void onProgressChange(int percent) {
                /*
                pbLoading.setProgress(percent);
                tvPercent.setText(percent + "%");
                */
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
                        Toast.makeText(InstaMainActivity.this, "Image saved as: " + myImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBitmapSaveError(ImageError error) {
                        Toast.makeText(InstaMainActivity.this, "Error code " + error.getErrorCode() + ": " +
                                error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }


                }, mFormat, false);

                /*
                tvPercent.setVisibility(View.GONE);
                pbLoading.setVisibility(View.GONE);
                */
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
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
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
    public void onCancelClick(int feedItem) {
        FeedContextMenuManager.getInstance().hideContextMenu();
    }


    @Override
    public void onMoreClick(View v, int position, String thumb_url, String uid, Post post, String postKey) {
        boolean result = checkPermission(InstaMainActivity.this);
        storage = result;
        FeedContextMenuManager.getInstance().toggleContextMenuFromView(v, position, this);
        FeedContextMenuManager.setCurrentPost(post);
        FeedContextMenuManager.setCurrentPostKey(postKey);
        FeedContextMenuManager.setCurrentImgUrl(thumb_url);
        FeedContextMenuManager.setCurrentPostUid(uid);
        String postUID = FeedContextMenuManager.getCurrentPostUid();
        if (!((user.getUid().contentEquals(postUID)) || (user.getUid() == postUID))) {
            findViewById(R.id.btnDelete).setVisibility(View.GONE);
        }

    }

    public void onTakePhotoClick() {
        if (user == null || user.isAnonymous()) {
            Toast.makeText(InstaMainActivity.this, "You must sign-in to post.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent newPostIntent = new Intent(InstaMainActivity.this, NewPostActivity.class);
        startActivity(newPostIntent);
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


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    /*
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("InstaMain Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
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
