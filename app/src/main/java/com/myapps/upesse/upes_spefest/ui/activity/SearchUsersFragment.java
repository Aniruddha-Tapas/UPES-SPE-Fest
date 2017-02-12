package com.myapps.upesse.upes_spefest.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapps.upesse.upes_spefest.R;
import com.twitter.sdk.android.core.models.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.tvbarthel.lib.blurdialogfragment.SupportBlurDialogFragment;

/**
 * Simple fragment with blur effect behind.
 */
public class SearchUsersFragment extends SupportBlurDialogFragment {

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

    /**
     * Bundle key used for UserNamesList
     */
    private static final String BUNDLE_KEY_USE_USERLIST = "bundle_key_use_userlist";

    /**
     * Bundle key used for UserDPsList
     */
    private static final String BUNDLE_KEY_USE_USERDPLIST = "bundle_key_use_userdplist";

    private int mRadius;
    private float mDownScaleFactor;
    private boolean mDimming;
    private boolean mDebug;
    private boolean mBlurredActionBar;
    private boolean mUseRenderScript;
    private List<String> userlist;
    private List<String> userDPs;
    ArrayList<SearchUser> usersarraylist;
    SearchUser user;
    ListViewAdapter adapter;

    /**
     * Retrieve a new instance of the sample fragment.
     *
     * @param radius            blur radius.
     * @param downScaleFactor   down scale factor.
     * @param dimming           dimming effect.
     * @param debug             debug policy.
     * @param mBlurredActionBar blur affect on actionBar policy.
     * @param useRenderScript   use of RenderScript
     * @param userlist          use of UserNamesList
     * @param userDPs
     * @return well instantiated fragment.
     */
    public static SearchUsersFragment newInstance(int radius,
                                                  float downScaleFactor,
                                                  boolean dimming,
                                                  boolean debug,
                                                  boolean mBlurredActionBar,
                                                  boolean useRenderScript,
                                                  ArrayList<String> userlist,
                                                  ArrayList<String> userDPs) {
        SearchUsersFragment fragment = new SearchUsersFragment();
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
        args.putBoolean(
                BUNDLE_KEY_USE_RENDERSCRIPT,
                useRenderScript
        );
        args.putStringArrayList(
                BUNDLE_KEY_USE_USERLIST,
                userlist
        );
        args.putStringArrayList(
                BUNDLE_KEY_USE_USERDPLIST,
                userDPs
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
        userlist = args.getStringArrayList(BUNDLE_KEY_USE_USERLIST);
        userDPs = args.getStringArrayList(BUNDLE_KEY_USE_USERDPLIST);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final View view = getActivity().getLayoutInflater().inflate(R.layout.search_users_fragment, null);
        //TextView label = ((TextView) view.findViewById(R.id.textView));
        final EditText search = (EditText) view.findViewById(R.id.search);
        ListView list = (ListView) view.findViewById(R.id.listview);
        ImageView search_back = (ImageView)view.findViewById(R.id.search_back);
        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getActivity().onBackPressed();
                getDialog().dismiss();
            }
        });

        usersarraylist = new ArrayList<SearchUser>();
        for(int i = 0; i< userlist.size(); i++ ){
            SearchUser user = new SearchUser(userlist.get(i),userDPs.get(i));
        }
        // Pass results to ListViewAdapter Class
        if(userlist!=null && !(userlist.isEmpty()) && userDPs!=null && !(userDPs.isEmpty())) {
            for(int i = 0; i< userlist.size(); i++ ){
                user = new SearchUser(userlist.get(i),userDPs.get(i));
                usersarraylist.add(user);
            }
        }

        adapter = new ListViewAdapter(view.getContext(), usersarraylist);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                String text = search.getText().toString().toLowerCase(Locale.getDefault());
                if(adapter!=null)
                    adapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

        });

        ImageView search_clear = (ImageView)view.findViewById(R.id.search_clear);
        search_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setText("");
            }
        });

        /*
        searchEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        builder.setView(view);
        return builder.create();
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
