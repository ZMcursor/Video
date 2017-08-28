package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by ZMcursor on 2017/8/21 0021.
 */

class SeekBar extends View implements View.OnTouchListener {

    public static final int vertical = 100;
    public static final int horizontal = 200;

    private int orientation = horizontal;
    private float width = 0, height = 0, seekBarWidth = 0, seekBarLength = 1;
    private boolean isTouch = false;
    private Paint paint;
    private RectF tRect, dRect;
    private float percentage = 0;
    private float cx, cy, radius;
    private OnSeekListener listener = null;

    public SeekBar(Context context) {
        this(context, horizontal);
    }

    public SeekBar(Context context, int orientation) {
        super(context);
        this.orientation = orientation;
        paint = new Paint();
        paint.setAntiAlias(true);
        tRect = new RectF();
        dRect = new RectF();
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        float newWidth = MeasureSpec.getSize(widthMeasureSpec);
        float newHeight = MeasureSpec.getSize(heightMeasureSpec);
        float spacing;
        if (orientation == horizontal && newWidth != width) {
            height = getResources().getDimension(R.dimen.size24);
            width = newWidth;
            seekBarWidth = height / 24;
            cy = height / 2;
            radius = height / 6;
            seekBarLength = width - cy * 2;
            spacing = cy - seekBarWidth;
            tRect.set(spacing, spacing, width - spacing, height - spacing);
            dRect.set(tRect);
        } else if (orientation == vertical && newHeight != height) {
            width = getResources().getDimension(R.dimen.size24);
            height = newHeight;
            seekBarWidth = width / 24;
            cx = width / 2;
            radius = width / 6;
            seekBarLength = height - cx * 2;
            spacing = cx - seekBarWidth;
            tRect.set(spacing, spacing, width - spacing, height - spacing);
            dRect.set(tRect);
        }
        setMeasuredDimension((int) (width + 0.5f), (int) (height + 0.5f));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.e("SeekBar", "onDraw");
        //进度条 背景
        paint.setColor(Color.parseColor("#adb1b1b1"));
        canvas.drawRoundRect(tRect, seekBarWidth, seekBarWidth, paint);

        if (orientation == horizontal) {
            cx = percentage * seekBarLength + cy;
            dRect.right = cx + seekBarWidth;
        } else if (orientation == vertical) {
            cy = (1 - percentage) * seekBarLength + cx;
            dRect.top = cy - seekBarWidth;
        }
        //进度条
        paint.setColor(Color.parseColor("#FF4081"));
        canvas.drawRoundRect(dRect, seekBarWidth, seekBarWidth, paint);
        //游标
        paint.setColor(Color.WHITE);
        if (isTouch) {
            canvas.drawCircle(cx, cy, radius * 1.5f, paint);
        } else {
            canvas.drawCircle(cx, cy, radius, paint);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isTouch = true;
                seek(event);
                break;
            case MotionEvent.ACTION_MOVE:
                seek(event);
//                Log.e("Touch", "move");
                break;
            case MotionEvent.ACTION_UP:
                isTouch = false;
                seek(event);
                if (listener != null) listener.onSeek(percentage);
                break;
        }
        return true;
    }

    private void seek(MotionEvent event) {
        if (orientation == horizontal) {
            percentage = (event.getX() - cy) / seekBarLength;
        } else if (orientation == vertical) {
            percentage = (height - event.getY() - cx) / seekBarLength;
        }
        changePercentage();
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
        changePercentage();
    }

    public void adjust(float offset) {
        percentage += offset;
        changePercentage();
    }

    private void changePercentage() {
        if (percentage < 0) percentage = 0;
        else if (percentage > 1) percentage = 1;
        invalidate();
    }

    public float getPercentage() {
        return percentage;
    }

    public void setOnSeekListener(OnSeekListener listener) {
        this.listener = listener;
    }

    interface OnSeekListener {
        void onSeek(float percentage);
    }
}
