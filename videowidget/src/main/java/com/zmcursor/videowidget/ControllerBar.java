package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by ZMcursor on 2017/8/22 0022.
 */

class ControllerBar extends MovableViewGroup {
    private static final String TAG = "AdjustView";

    private BtnView btnPlay, btnVolume, btnFullScreen;
    private SeekBar seekBar;
    private int width, height;
    private ControllerBarListener listener = null;
    private int duration = 0;

    public ControllerBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        height = getResources().getDimensionPixelSize(R.dimen.size40);
        initMove(1, height, true, 3);
        btnPlay = new BtnView(context, R.drawable.ic_play, R.drawable.ic_pause);
        btnVolume = new BtnView(context, R.drawable.ic_volume2, R.drawable.ic_volume_off);
        btnFullScreen = new BtnView(context, R.drawable.ic_fullscreen, R.drawable.ic_fullscreen_exit);
        seekBar = new SeekBar(context, SeekBar.horizontal);
        addView(btnPlay);
        addView(btnVolume);
        addView(btnFullScreen);
        addView(seekBar);
    }

    public void init(int duration) {
        this.duration = duration;
        seekBar.setPercentage(0);
        btnPlay.off();
    }

    public void setControllerBarListener(ControllerBarListener controllerBarListener) {
        if (listener == null) {
//            listener = controllerBarListener;
            btnPlay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnPlay.isDefault) {
                        if (listener.play()) btnPlay.on();
                    } else {
                        listener.pause();
                        btnPlay.off();
                    }
                }
            });
            btnVolume.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnVolume.isDefault) {
                        listener.volumeOff();
                        btnVolume.on();
                    } else {
                        listener.volumeOn();
                        btnVolume.off();
                    }
                }
            });
            btnFullScreen.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.fullScreen(btnFullScreen.isDefault);
                    if (btnFullScreen.isDefault) btnFullScreen.on();
                    else btnFullScreen.off();
                }
            });
            seekBar.setOnSeekListener(new SeekBar.OnSeekListener() {
                @Override
                public void onSeek(float percentage) {
                    listener.seekTo(percentage);
                }
            });
        }
        listener = controllerBarListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        width = MeasureSpec.getSize(widthMeasureSpec);
        btnPlay.measure(height, height);
        btnVolume.measure(height, height);
        btnFullScreen.measure(height, height);
        seekBar.measure(width - height * 3, height);
        setMeasuredDimension(width, height);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout");
        l = 0;
        r = height;
        btnPlay.layout(l, 0, r, height);
        l = r;
        r = l + seekBar.getMeasuredWidth();
        seekBar.layout(l, (height - seekBar.getMeasuredHeight()) / 2, r,
                (height - seekBar.getMeasuredHeight()) / 2 + seekBar.getMeasuredHeight());
        l = r;
        r = l + height;
        btnVolume.layout(l, 0, r, height);
        btnFullScreen.layout(r, 0, width, height);
//        seekBar.layout(height, (height - seekBar.getMeasuredHeight()) / 2,
//                height + seekBar.getMeasuredWidth(),
//                (height - seekBar.getMeasuredHeight()) / 2 + seekBar.getMeasuredHeight());
//        btnVolume.layout(seekBar.getRight(), 0, seekBar.getRight() + height, height);
//        btnFullScreen.layout(btnVolume.getRight(), 0, getWidth(), height);
    }

    @Override
    public void locate(int offset, int l, int t, int r, int b) {
        Log.e(TAG, "locate");
        layout(l, t + offset, r, b + offset);
    }


    public void play() {
        if (btnPlay.isDefault) btnPlay.on();
    }

    public void stop() {
        if (!btnPlay.isDefault) btnPlay.off();
    }

    public void onFullScreen(boolean isFullScreen) {
        if (isFullScreen) btnFullScreen.on();
        else btnFullScreen.off();
    }

    public void seek(float second) {
        seekBar.adjust(second / duration);
    }

    public void seekTo(float position) {
        seekBar.setPercentage(position / duration);
    }

    private class BtnView extends VectorView {

        private Drawable defaultBg, bg;
        private boolean isDefault = true;

        public BtnView(Context context, @DrawableRes int defaultBg, @DrawableRes int bg) {
            super(context, defaultBg);
            this.defaultBg = getBg();
            this.bg = VectorDrawableCompat.create(getResources(), bg, null);
        }

        private void on() {
            setBg(bg);
            isDefault = false;
        }

        private void off() {
            setBg(defaultBg);
            isDefault = true;
        }
//
//        private void shift() {
//            if (isDefault) {
//                setBg(bg);
//                isDefault = false;
//            } else {
//                setBg(defaultBg);
//                isDefault = true;
//            }
//        }
    }

    interface ControllerBarListener {
        boolean play();

        void pause();

        void seekTo(float percentage);

        void volumeOn();

        void volumeOff();

        void fullScreen(boolean isFullScreen);
    }
}
