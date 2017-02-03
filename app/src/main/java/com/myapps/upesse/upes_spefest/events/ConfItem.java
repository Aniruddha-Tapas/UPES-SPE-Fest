package com.myapps.upesse.upes_spefest.events;

public class ConfItem {

    private int mTitleConf;
    private int mDescConf;

    public ConfItem(int mTitleConf, int mDescConf) {
        this.mTitleConf = mTitleConf;
        this.mDescConf = mDescConf;
    }

    public int getmTitleConf() {
        return mTitleConf;
    }

    public int getmDescConf() {
        return mDescConf;
    }
}
