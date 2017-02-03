package com.myapps.upesse.upes_spefest.ui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.myapps.upesse.upes_spefest.R;
import com.myapps.upesse.upes_spefest.ui.utils.Utils;

//import butterknife.ButterKnife;
//import butterknife.OnClick;


public class FeedContextMenu extends LinearLayout {
    private static final int CONTEXT_MENU_WIDTH = Utils.dpToPx(240);

    private int feedItem = -1;

    private OnFeedContextMenuItemClickListener onItemClickListener;

    public FeedContextMenu(Context context) {
        super(context);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_context_menu, this, true);
        setBackgroundResource(R.drawable.bg_container_shadow);
        setOrientation(VERTICAL);
        setLayoutParams(new LayoutParams(CONTEXT_MENU_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void bindToItem(int feedItem) {
        this.feedItem = feedItem;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        findViewById(R.id.btnDelete).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onDeleteClick();
            }
        });
        findViewById(R.id.btnSavePhoto).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onSavePhotoClick();
            }
        });
        /*
        findViewById(R.id.btnCopyShareUrl).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onCopyShareUrlClick();
            }
        });
        */
        findViewById(R.id.btnCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                onCancelClick();
            }
        });

        //ButterKnife.bind(this);
    }

    public void dismiss() {
        ((ViewGroup) getParent()).removeView(FeedContextMenu.this);
    }

    //@OnClick(R.id.btnReport)
    public void onDeleteClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onDeleteClick(feedItem);
        }
    }

    //@OnClick(R.id.btnSharePhoto)
    public void onSavePhotoClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onSavePhotoClick(feedItem);
        }
    }

    /*
    //@OnClick(R.id.btnCopyShareUrl)
    public void onCopyShareUrlClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onCopyShareUrlClick(feedItem);
        }
    }
    */

    //@OnClick(R.id.btnCancel)
    public void onCancelClick() {
        if (onItemClickListener != null) {
            onItemClickListener.onCancelClick(feedItem);
        }
    }

    public void setOnFeedMenuItemClickListener(OnFeedContextMenuItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnFeedContextMenuItemClickListener {
        public void onDeleteClick(int feedItem);

        public void onSavePhotoClick(int feedItem);

        //public void onCopyShareUrlClick(int feedItem);

        public void onCancelClick(int feedItem);
    }
}