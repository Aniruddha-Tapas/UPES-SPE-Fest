package com.myapps.upesse.upes_spefest.events;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapps.upesse.upes_spefest.R;

import java.util.ArrayList;
import java.util.List;

public class ConfPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<ConfItem> mData;
    private float mBaseElevation;

    public ConfPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(ConfItem item) {
        mViews.add(null);
        mData.add(item);
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.activity_conferences, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardViewC);

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }

    private void bind(ConfItem item, View view) {
        TextView titleConf = (TextView) view.findViewById(R.id.titleConf);
        TextView descConf = (TextView) view.findViewById(R.id.descConf);

        titleConf.setText(item.getmTitleConf());
        descConf.setText(item.getmDescConf());
    }

}
