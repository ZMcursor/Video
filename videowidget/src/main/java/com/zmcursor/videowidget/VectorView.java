package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by ZMcursor on 2017/8/22 0022.
 */

class VectorView extends View {

    private Drawable bg;

    public VectorView(Context context, @DrawableRes int drawable) {
        super(context);
        bg = VectorDrawableCompat.create(getResources(), drawable, null);

    }

    public void setBg(Drawable bg) {
        this.bg = bg;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    public Drawable getBg() {
        return bg;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.e("VectorView", "onDraw");
        bg.setBounds((canvas.getWidth() - bg.getIntrinsicWidth()) / 2,
                (canvas.getHeight() - bg.getIntrinsicHeight()) / 2,
                canvas.getWidth() - (canvas.getWidth() - bg.getIntrinsicWidth()) / 2,
                canvas.getHeight() - (canvas.getHeight() - bg.getIntrinsicHeight()) / 2);
        bg.draw(canvas);
    }
}
