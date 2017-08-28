package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ZMcursor on 2017/8/22 0022.
 */

public class VideoControllerView extends ViewGroup implements View.OnTouchListener {
    private static final String TAG = "VideoControllerView";

    private TitleBar titleBar;
    private ControllerBar controllerBar;
    private VolumeAdjustView volumeView;
    private BrightnessAdjustView brightnessView;
    private int width, height, spacing;
    private boolean isFullScreen = false, isPlayed = false;
    private boolean showFast = false, canAdjustVolume = true;
    private int seekSecond = 0;
    private char touchEvent = 'o';
    private float x, y;
    private Drawable fastForward, fastRewind;
    private Bitmap bg;

    private VideoControllerListener listener = null;

    public VideoControllerView(Context context) {
        this(context, null);
    }

    public VideoControllerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoControllerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        spacing = getResources().getDimensionPixelSize(R.dimen.spacing8);
        setOnTouchListener(this);
        fastForward = VectorDrawableCompat.create(getResources(), R.drawable.ic_fast_forward, null);
        fastRewind = VectorDrawableCompat.create(getResources(), R.drawable.ic_fast_rewind, null);
        setWillNotDraw(false);

        titleBar = new TitleBar(context);
        controllerBar = new ControllerBar(context);
        volumeView = new VolumeAdjustView(context);
        brightnessView = new BrightnessAdjustView(context);
        addView(titleBar);
        addView(controllerBar);
        addView(volumeView);
        addView(brightnessView);
        setListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e(TAG, "onMeasure");
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        titleBar.measure(width, height);
        controllerBar.measure(width, height);
        int mHeight = height - titleBar.getMeasuredHeight() - controllerBar.getMeasuredHeight();
        volumeView.measure(width, mHeight);
        brightnessView.measure(width, mHeight);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e(TAG, "onLayout");
        t = titleBar.getMeasuredHeight();
        b = height - controllerBar.getMeasuredHeight();
        titleBar.locate(0, 0, width, t);
        controllerBar.locate(0, b, width, height);
        volumeView.locate(spacing, t, spacing + volumeView.getMeasuredWidth(), b);
        brightnessView.locate(width - brightnessView.getMeasuredWidth() - spacing, t, width - spacing, b);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e(TAG, "onDraw");
        super.onDraw(canvas);
        if (!isPlayed) canvas.drawColor(Color.BLACK);
        if (showFast) {
            int t, cx = (width >> 1);
            if (seekSecond > 0) {
                t = ((height + fastForward.getIntrinsicHeight()) >> 1);
                fastForward.setBounds(cx - (fastForward.getIntrinsicWidth() >> 1),
                        t - fastForward.getIntrinsicHeight(),
                        cx + (fastForward.getIntrinsicWidth() >> 1), t);
                fastForward.draw(canvas);
            } else {
                t = ((height + fastRewind.getIntrinsicHeight()) >> 1);
                fastRewind.setBounds(cx - (fastRewind.getIntrinsicWidth() >> 1),
                        t - fastRewind.getIntrinsicHeight(),
                        cx + (fastRewind.getIntrinsicWidth() >> 1), t);
                fastRewind.draw(canvas);
            }
//            String s = seekSecond / 60 + ":" + seekSecond % 60;
//            canvas.drawText();
        }
    }

    private void switchBar() {
        titleBar.switchState();
        controllerBar.switchState();
    }

    public void setVideoControllerListener(VideoControllerListener listener) {
        this.listener = listener;
    }

    public void init(String title, int duration, Bitmap bg) {
        titleBar.init(title);
        controllerBar.init(duration);
        this.bg = bg;
        invalidate();
    }

    public void play() {
        isPlayed = true;
        controllerBar.play();
    }

    public void pause() {
        controllerBar.stop();
    }

//    public void stop() {
//        controllerBar.stop();
//    }

    public void seekTo(int position) {
        controllerBar.seekTo(position);
    }

    public void fullScreen(boolean isFullScreen) {
        titleBar.onFullScreen(isFullScreen);
        controllerBar.onFullScreen(isFullScreen);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                x = event.getX();
                y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                switch (touchEvent) {
                    case 'o':
                        touchEvent = gesture(event.getX() - x, event.getY() - y);
                        x = event.getX();
                        y = event.getY();
                        switch (touchEvent) {
                            case 'o':
                                break;
                            case 'f':
                                seekSecond = 0;
                                showFast = true;
                                break;
                            case 'v':
                                if (canAdjustVolume) volumeView.show();
                                break;
                            case 'b':
                                brightnessView.show();
                                break;
                        }
                        break;
                    case 'f':
                        seek(event.getX() - x);
                        break;
                    case 'v':
                        if (canAdjustVolume) {
                            volumeView.adjust(y - event.getY());
                            x = event.getX();
                            y = event.getY();
                        }
                        break;
                    case 'b':
                        brightnessView.adjust(y - event.getY());
                        x = event.getX();
                        y = event.getY();
                        break;
                }
                break;
            case MotionEvent.ACTION_UP:
                switch (touchEvent) {
                    case 'o':
                        switchBar();
                        break;
                    case 'f':
                        seekVideo();
                        showFast = false;
                        seekSecond = 0;
                        touchEvent = 'o';
                        invalidate();
                        break;
                    case 'v':
                        if (canAdjustVolume) {
                            volumeView.hide(1000);
                        }
                        touchEvent = 'o';
                        break;
                    case 'b':
                        brightnessView.hide(1000);
                        touchEvent = 'o';
                        break;
                }
                break;
        }
        return true;
    }

    private char gesture(float dx, float dy) {
        if (dx == 0) {
            if (x > (width >> 1)) return 'v';
            else return 'b';
        } else {
            float n = Math.abs(dy / dx);
            if (n < 0.58) return 'f';
            else if (n > 1.74) {
                if (x > (width >> 1)) return 'v';
                else return 'b';
            }
        }
        return 'o';
    }

    private void seek(float offset) {
        seekSecond += (int) offset;
        invalidate();
    }

    private void seekVideo() {
        controllerBar.seek(seekSecond);
        if (listener != null) listener.onSeek(seekSecond);
    }

    private void setListener() {
        titleBar.setTitleBarListener(new TitleBar.TitleBarListener() {
            @Override
            public void back() {
                if (isFullScreen) {
                    isFullScreen = false;
                    listener.onFullScreen(false);
                    controllerBar.onFullScreen(false);
                    titleBar.onFullScreen(false);
                } else {
                    listener.onExit();
                }
            }
        });

        controllerBar.setControllerBarListener(new ControllerBar.ControllerBarListener() {
            @Override
            public boolean play() {
                isPlayed = true;
                return listener.onPlay();
            }

            @Override
            public void pause() {
                listener.onPause();
            }

            @Override
            public void seekTo(float percentage) {
                listener.onSeekTo(percentage);
            }

            @Override
            public void volumeOn() {
                canAdjustVolume = true;
            }

            @Override
            public void volumeOff() {
                canAdjustVolume = false;
            }

            @Override
            public void fullScreen(boolean isFullScreen) {
                VideoControllerView.this.isFullScreen = isFullScreen;
                listener.onFullScreen(isFullScreen);
                titleBar.onFullScreen(isFullScreen);
            }
        });
    }

    interface VideoControllerListener {

        boolean onPlay();

        void onPause();

        void onSeek(int offset);

        void onSeekTo(float percentage);

        void onFullScreen(boolean isFullScreen);

        void onExit();
    }
}
