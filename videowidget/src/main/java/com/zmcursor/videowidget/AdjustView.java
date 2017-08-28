package com.zmcursor.videowidget;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.View;
//import android.widget.SeekBar;

/**
 * Created by ZMcursor on 2017/8/17 0017.
 */

abstract class AdjustView extends MovableViewGroup implements SeekBar.OnSeekListener {
    private static final String TAG = "AdjustView";

    private View iconView;
    private SeekBar seekBar;
    private int width, height;

    public AdjustView(Context context, @DrawableRes int icon, int direction) {
        super(context);
        seekBar = new SeekBar(context, SeekBar.vertical);
        seekBar.setOnSeekListener(this);
        addView(seekBar);
        iconView = new VectorView(context, icon);
        addView(iconView);
        width = getResources().getDimensionPixelSize(R.dimen.size24);
        initMove(0.5f, width, false, direction);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        height = MeasureSpec.getSize(heightMeasureSpec);
        seekBar.measure(width, height - width);
        iconView.measure(width, width);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout");
        seekBar.layout(0, 0, width, height - width);
        iconView.layout(0, height - width, width, height);
    }


    public SeekBar getSeekBar() {
        return seekBar;
    }

    public void adjust(float px) {
        seekBar.adjust(px / getHeight());
        onSeek(seekBar.getPercentage());
    }
}
