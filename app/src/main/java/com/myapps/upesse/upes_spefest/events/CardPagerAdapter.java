package com.myapps.upesse.upes_spefest.events;

import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.myapps.upesse.upes_spefest.R;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter {

    private List<CardView> mViews;
    private List<CardItem> mData;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mData = new ArrayList<>();
        mViews = new ArrayList<>();
    }

    public void addCardItem(CardItem item) {
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
                .inflate(R.layout.events_adapter, container, false);
        container.addView(view);
        bind(mData.get(position), view);
        CardView cardView = (CardView) view.findViewById(R.id.cardView);

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

    private void bind(CardItem item, View view) {
        ImageView imgEvent = (ImageView)view.findViewById(R.id.imgEvent);
        TextView titleEvent = (TextView) view.findViewById(R.id.titleEvent);
        TextView descEvent = (TextView) view.findViewById(R.id.descEvent);
        TextView contact1Event = (TextView) view.findViewById(R.id.contact1Event);
        TextView contact2Event = (TextView) view.findViewById(R.id.contact2Event);
        TextView contact3Event = (TextView) view.findViewById(R.id.contact3Event);
        TextView phoneNo1 = (TextView) view.findViewById(R.id.phoneNo1);
        TextView phoneNo2 = (TextView) view.findViewById(R.id.phoneNo2);
        TextView phoneNo3 = (TextView) view.findViewById(R.id.phoneNo3);


        imgEvent.setImageResource(item.getmImgEvent());
        titleEvent.setText(item.getmTitleEvent());
        descEvent.setText(item.getmTitleDesc());
        contact1Event.setText(item.getmContact1Event());
        contact2Event.setText(item.getmContact2Event());
        contact3Event.setText(item.getmContact3Event());
        phoneNo1.setText(item.getmPhoneNo1());
        phoneNo2.setText(item.getmPhoneNo2());
        phoneNo3.setText(item.getmPhoneNo3());

    }

}
