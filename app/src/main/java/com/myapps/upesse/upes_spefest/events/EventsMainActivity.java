package com.myapps.upesse.upes_spefest.events;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.activity.BaseActivity;
import com.myapps.upesse.upes_spefest.ui.utils.Utils;

public class EventsMainActivity extends BaseActivity
        //implements View.OnClickListener,CompoundButton.OnCheckedChangeListener
{

    private Button mButton;
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;
    //private CardFragmentPagerAdapter mFragmentCardAdapter;
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
        //mButton = (Button) findViewById(R.id.cardTypeBtn);
        //((CheckBox) findViewById(R.id.checkBox)).setOnCheckedChangeListener(this);
        //mButton.setOnClickListener(this);

        mCardAdapter = new CardPagerAdapter();
        /*
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.text_1));
        */
        mCardAdapter.addCardItem(new CardItem(R.drawable.c6, R.string.event_title_1, R.string.event_desc_1,
                R.string.event1_contact1, R.string.event1_contact2, R.string.event1_contact3,
                R.string.event1_phoneno1, R.string.event1_phoneno2, R.string.event1_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c2, R.string.event_title_2, R.string.event_desc_2,
                R.string.event2_contact1, R.string.event2_contact2, R.string.event2_contact3,
                R.string.event2_phoneno1, R.string.event2_phoneno2, R.string.event2_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c4, R.string.event_title_3, R.string.event_desc_3,
                R.string.event3_contact1, R.string.event3_contact2, R.string.event3_contact3,
                R.string.event3_phoneno1, R.string.event3_phoneno2, R.string.event3_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c5, R.string.event_title_4, R.string.event_desc_4,
                R.string.event4_contact1, R.string.event4_contact2, R.string.event4_contact3,
                R.string.event4_phoneno1, R.string.event4_phoneno2, R.string.event4_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c7, R.string.event_title_5, R.string.event_desc_5,
                R.string.event5_contact1, R.string.event5_contact2, R.string.event5_contact3,
                R.string.event5_phoneno1, R.string.event5_phoneno2, R.string.event5_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c1, R.string.event_title_6, R.string.event_desc_6,
                R.string.event6_contact1, R.string.event6_contact2, R.string.event6_contact3,
                R.string.event6_phoneno1, R.string.event6_phoneno2, R.string.event6_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c8, R.string.event_title_7, R.string.event_desc_7,
                R.string.event7_contact1, R.string.event7_contact2, R.string.event7_contact3,
                R.string.event7_phoneno1, R.string.event7_phoneno2, R.string.event7_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c9, R.string.event_title_8, R.string.event_desc_8,
                R.string.event8_contact1, R.string.event8_contact2, R.string.event8_contact3,
                R.string.event8_phoneno1, R.string.event8_phoneno2, R.string.event8_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c10, R.string.event_title_9, R.string.event_desc_9,
                R.string.event9_contact1, R.string.event9_contact2, R.string.event9_contact3,
                R.string.event9_phoneno1, R.string.event9_phoneno2, R.string.event9_phoneno3));

        mCardAdapter.addCardItem(new CardItem(R.drawable.c3, R.string.event_title_10, R.string.event_desc_10,
                R.string.event10_contact1, R.string.event10_contact2, R.string.event10_contact3,
                R.string.event10_phoneno1, R.string.event10_phoneno2, R.string.event10_phoneno3));

        //mFragmentCardAdapter = new CardFragmentPagerAdapter(getSupportFragmentManager(),dpToPixels(2, this));

        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);
        //mFragmentCardShadowTransformer = new ShadowTransformer(mViewPager, mFragmentCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);
        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }

    /*
    @Override
    public void onClick(View view) {
        if (!mShowingFragments) {
            mButton.setText("Views");
            mViewPager.setAdapter(mFragmentCardAdapter);
            mViewPager.setPageTransformer(false, mFragmentCardShadowTransformer);
        } else {
            mButton.setText("Fragments");
            mViewPager.setAdapter(mCardAdapter);
            mViewPager.setPageTransformer(false, mCardShadowTransformer);
        }

        mShowingFragments = !mShowingFragments;
    }


    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        mCardShadowTransformer.enableScaling(b);
        mFragmentCardShadowTransformer.enableScaling(b);
    }
    */

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
