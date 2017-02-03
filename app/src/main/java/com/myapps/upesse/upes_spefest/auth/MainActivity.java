package com.myapps.upesse.upes_spefest.auth;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.MainThread;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.AuthUI.IdpConfig;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ui.ResultCodes;
import com.google.android.gms.common.Scopes;
import com.google.firebase.auth.FirebaseAuth;
import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.activity.InstaMainActivity;

import java.util.ArrayList;
import java.util.List;

//import de.quist.app.errorreporter.ExceptionReporter;

public class MainActivity extends AppCompatActivity {

    private static final String FIREBASE_TOS_URL = "https://www.firebase.com/terms/terms-of-service.html";
    private static final int RC_SIGN_IN = 100;
    Button SignIn;
    View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ExceptionReporter reporter = ExceptionReporter.register(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            //startActivity(SignedInActivity.createIntent(this, null));
            startActivity(InstaMainActivity.createIntent(this, null));
            finish();
        }

        setContentView(R.layout.activity_main);

        SignIn = (Button) findViewById(R.id.signin);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sign_In(view);
            }
        });

        mRootView = (View) findViewById(android.R.id.content);

    }

    private void sign_In(View view) {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setTheme(getSelectedTheme())
                        .setLogo(getSelectedLogo())
                        .setProviders(getSelectedProviders())
                        .setTosUrl(getSelectedTosUrl())
                        .setIsSmartLockEnabled(true)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }

        showSnackbar(R.string.unknown_response);
    }

    @MainThread
    private void handleSignInResponse(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            //startActivity(SignedInActivity.createIntent(this, IdpResponse.fromResultIntent(data)));
            startActivity(InstaMainActivity.createIntent(this, IdpResponse.fromResultIntent(data)));
            finish();
            return;
        }

        if (resultCode == RESULT_CANCELED) {
            showSnackbar(R.string.sign_in_cancelled);
            return;
        }

        if (resultCode == ResultCodes.RESULT_NO_NETWORK) {
            showSnackbar(R.string.no_internet_connection);
            return;
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    @MainThread
    @StyleRes
    private int getSelectedTheme() {
        return AuthUI.getDefaultTheme();
    }

    @MainThread
    @DrawableRes
    private int getSelectedLogo() {
            return R.drawable.cover;
    }

    @MainThread
    private List<IdpConfig> getSelectedProviders() {
        List<IdpConfig> selectedProviders = new ArrayList<>();
            selectedProviders.add(new IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());
            selectedProviders.add(
                    new IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER)
                            //.setPermissions(getFacebookPermissions())
                            .build());
            selectedProviders.add(
                    new IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER)
                            //.setPermissions(getGooglePermissions())
                            .build());
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT)
            selectedProviders.add(new IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());

        return selectedProviders;
    }

    @MainThread
    private String getSelectedTosUrl() {
        return FIREBASE_TOS_URL;
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    @MainThread
    private List<String> getFacebookPermissions() {
        List<String> result = new ArrayList<>();
        result.add("user_friends");
        result.add("user_photos");

        return result;
    }

    @MainThread
    private List<String> getGooglePermissions() {
        List<String> result = new ArrayList<>();
        result.add(Scopes.GAMES);
        result.add(Scopes.DRIVE_FILE);

        return result;
    }

    public static Intent createIntent(Context context) {
        Intent in = new Intent();
        in.setClass(context, MainActivity.class);
        return in;
    }
}

