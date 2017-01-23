package com.myapps.upesse.upes_spefest.events;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.Button;

import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.activity.BaseActivity;
import com.myapps.upesse.upes_spefest.ui.utils.Utils;

public class ConferencesActivity extends BaseActivity
{

    private Button mButton;
    private ViewPager mViewPager;
    
    private ConfPagerAdapter mConfAdapter;
    private ShadowTransformer mCardShadowTransformer;
    private ShadowTransformer mFragmentCardShadowTransformer;

    private boolean mShowingFragments = false;

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventsmain);
        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        
        mConfAdapter = new ConfPagerAdapter();

        mConfAdapter.addCardItem(new ConfItem(R.string.conf_speakers_title, R.string.conf_speakers));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_1, R.string.conf_desc_1));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_2, R.string.conf_desc_2));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_3, R.string.conf_desc_3));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_4, R.string.conf_desc_4));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_5, R.string.conf_desc_5));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_6, R.string.conf_desc_6));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_7, R.string.conf_desc_7));
        mConfAdapter.addCardItem(new ConfItem(R.string.conf_title_8, R.string.conf_desc_8));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mConfAdapter);
        //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentConfAdapter);

        mViewPager.setAdapter(mConfAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {

        int actionbarSize = Utils.dpToPx(56);
        getToolbar().setTranslationY(-actionbarSize);
        getIvLogo().setTranslationY(-actionbarSize);
        getToolbar().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        getIvLogo().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
    }
}
