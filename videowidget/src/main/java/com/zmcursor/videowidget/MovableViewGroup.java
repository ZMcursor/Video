package com.zmcursor.videowidget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.Log;
import android.view.ViewGroup;

/**
 * Created by ZMcursor on 2017/8/23 0023.
 */

abstract class MovableViewGroup extends ViewGroup {
    private static final String TAG = "MovableViewGroup";

    private float moveStep;
    private int offset, distance, time;
    private char state;
    private boolean isHide;

    private Paint paint = new Paint();

    private Runnable keepMove = new Runnable() {
        @Override
        public void run() {
            move();
        }
    };

    private Runnable delayHide = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    public MovableViewGroup(Context context) {
        super(context);
        paint.setAntiAlias(true);
        setWillNotDraw(false);
    }

    /**
     * @param second    时间
     * @param distance  移动距离
     * @param visible   初始是否可见
     * @param direction 背景渐变方向 右 0；下 1；左 2；上 3；
     */
    public void initMove(float second, int distance, boolean visible, int direction) {
        Log.e(TAG, "switchState");
        moveStep = distance / (second * ((Activity) getContext()).getWindowManager().getDefaultDisplay().getRefreshRate());
        if (moveStep < 1) moveStep = 1;
        time = (int) (1000 * second * moveStep / distance);
        this.distance = distance;
        if (visible) {
            offset = 0;
            state = 's';
            setVisibility(VISIBLE);
        } else {
            offset = distance;
            state = 'h';
            setVisibility(INVISIBLE);
        }
        int[] rect = new int[]{0, 0, 0, 0};
        rect[direction] = distance;
        paint.setShader(new LinearGradient(rect[2], rect[3], rect[0], rect[1], Color.LTGRAY, Color.TRANSPARENT, Shader.TileMode.CLAMP));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
    }

    //    @Override
//    public void draw(Canvas canvas) {
//        Log.e(TAG, "draw");
//        super.draw(canvas);
//        if (state == 'a') {
//            if (isHide) offset += moveStep;
//            else offset -= moveStep;
//            if (offset <= 0) {
//                state = 's';
//                offset = 0;
//                move();
//            } else if (offset >= distance) {
//                setVisibility(INVISIBLE);
//                state = 'h';
//                offset = distance;
//            } else move();
//        }
//    }

    public void switchState() {
        Log.e(TAG, "switchState");
        switch (state) {
            case 'h':
                isHide = false;
                setVisibility(VISIBLE);
                state = 'a';
                move();
                break;
            case 'a':
                isHide = !isHide;
                break;
            case 's':
                isHide = true;
                state = 'a';
                move();
                break;
        }
    }

    public void show() {
        Log.e(TAG, "show");
        isHide = false;
        if (state == 'h') {
            setVisibility(VISIBLE);
            state = 'a';
            move();
        }
    }

    public void hide() {
        Log.e(TAG, "hide");
        isHide = true;
        if (state == 's') {
            state = 'a';
            move();
        }
    }

    public void hide(int delay) {
        if (state == 's' && delay > 0) {
            postDelayed(delayHide, delay);
        } else hide();
    }

    private void move() {
//        Log.e(TAG, "move " + offset);
//        locate(offset, getLeft(), getTop(), getRight(), getBottom());
//        if (state == 'a') {
        if (isHide) {
            offset += moveStep;
            if (offset >= distance) {
                setVisibility(INVISIBLE);
                state = 'h';
                offset = distance;
            } else {
                postDelayed(keepMove, time);
            }
        } else {
            offset -= moveStep;
            if (offset <= 0) {
                state = 's';
                offset = 0;
            } else {
                postDelayed(keepMove, time);
            }
        }
//        if (offset <= 0) {
//            state = 's';
//            offset = 0;
//        } else if (offset >= distance) {
//            setVisibility(INVISIBLE);
//            state = 'h';
//            offset = distance;
//        } else {
//            postDelayed(this, 8);
//        }
        requestLayout();
//        }
    }

    public void locate(int l, int t, int r, int b) {
//        if (getVisibility() == VISIBLE)
        locate(offset, l, t, r, b);
    }

    public abstract void locate(int offset, int l, int t, int r, int b);
}
