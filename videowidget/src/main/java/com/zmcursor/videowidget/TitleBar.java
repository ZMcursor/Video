package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.Color;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.PopupMenu;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ZMcursor on 2017/8/22 0022.
 */

class TitleBar extends MovableViewGroup {
    private static final String TAG = "TitleBar";

    private VectorView btnBack, btnMore;
    private TextView titleView;
    private int width, height;
    private TitleBarListener listener = null;
    private PopupMenu moreMenu = null;

    public TitleBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        height = getResources().getDimensionPixelSize(R.dimen.size40);
        initMove(1, height, true, 1);
        btnBack = new VectorView(context, R.drawable.ic_close);
        btnMore = new VectorView(context, R.drawable.ic_more);
        btnMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
        titleView = new TextView(context);
        titleView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        titleView.setGravity(Gravity.CENTER_VERTICAL);
        titleView.setTextSize(16);
        titleView.setTextColor(Color.WHITE);
        titleView.setSingleLine();
        titleView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        titleView.setFocusable(true);
        titleView.setFocusableInTouchMode(true);
        titleView.setText("动的，毕竟ScrollView必须只能有一个直接的子类布局。只要在layout中简单设置几个属性就可以轻松实现。");

        addView(btnBack);
        addView(btnMore);
        addView(titleView);
    }

    public void init(String title) {
        titleView.setText(title);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        width = MeasureSpec.getSize(widthMeasureSpec);
        btnBack.measure(height, height);
        btnMore.measure(height, height);
        titleView.measure(width - height * 2, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout");
        btnBack.layout(0, 0, height, height);
        titleView.layout(height, 0, width - height, height);
        btnMore.layout(width - height, 0, width, height);
    }

    @Override
    public void locate(int offset, int l, int t, int r, int b) {
        Log.e(TAG, "locate");
        layout(l, t - offset, r, b - offset);
    }

    public void setTitleBarListener(final TitleBarListener titleBarListener) {
        this.listener = titleBarListener;
        btnBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.back();
            }
        });
    }

    public void onFullScreen(boolean isFullScreen) {
        if (isFullScreen)
            btnBack.setBg(VectorDrawableCompat.create(getResources(), R.drawable.ic_back, null));
        else btnBack.setBg(VectorDrawableCompat.create(getResources(), R.drawable.ic_more, null));
    }

    private void showMenu() {
        if (moreMenu == null) {
            moreMenu = new PopupMenu(getContext(), btnBack);
            moreMenu.getMenu().add("菜单1");
            moreMenu.getMenu().add("菜单2");
            moreMenu.getMenu().add("菜单3");
            moreMenu.getMenu().add("菜单4");
        }
        moreMenu.show();
    }

    interface TitleBarListener {
        void back();
    }
}
