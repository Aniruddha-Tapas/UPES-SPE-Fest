package com.myapps.upesse.upes_spefest.events;


public class CardItem {

    private int mImgEvent;
    private int mTitleEvent;
    private int mTitleDesc;
    private int mContact1Event;
    private int mContact2Event;
    private int mContact3Event;
    private int mPhoneNo1;
    private int mPhoneNo2;
    private int mPhoneNo3;

    public int getmImgEvent() {
        return mImgEvent;
    }

    public int getmTitleEvent() {
        return mTitleEvent;
    }

    public int getmTitleDesc() {
        return mTitleDesc;
    }

    public int getmContact1Event() {
        return mContact1Event;
    }

    public int getmContact2Event() {
        return mContact2Event;
    }

    public int getmContact3Event() {
        return mContact3Event;
    }

    public int getmPhoneNo1() {
        return mPhoneNo1;
    }

    public int getmPhoneNo2() {
        return mPhoneNo2;
    }

    public int getmPhoneNo3() {
        return mPhoneNo3;
    }

    public CardItem(int mImgEvent, int mTitleEvent, int mTitleDesc,
                    int mContact1Event, int mContact2Event, int mContact3Event,
                    int mPhoneNo1, int mPhoneNo2, int mPhoneNo3) {
        this.mImgEvent = mImgEvent;
        this.mTitleEvent = mTitleEvent;
        this.mTitleDesc = mTitleDesc;
        this.mContact1Event = mContact1Event;
        this.mContact2Event = mContact2Event;
        this.mContact3Event = mContact3Event;
        this.mPhoneNo1 = mPhoneNo1;
        this.mPhoneNo2 = mPhoneNo2;
        this.mPhoneNo3 = mPhoneNo3;
    }
}
