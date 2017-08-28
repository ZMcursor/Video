package com.zmcursor.videowidget;

import android.app.Activity;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by ZMcursor on 2017/8/21 0021.
 */

class BrightnessAdjustView extends AdjustView {
    private static final String TAG = "BrightnessAdjustView";

//    private Activity activity;

    public BrightnessAdjustView(Context context) {
        super(context, R.drawable.ic_light, 2);
        getSeekBar().setPercentage(getBrightness((Activity) context));
    }

//    @Override
//    public void adjust(float px) {
//        super.adjust(px);
//        setBrightness((Activity) getContext(), getSeekBar().getPercentage());
//    }.

    private void setBrightness(Activity activity, float brightness) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = brightness;
        activity.getWindow().setAttributes(lp);
    }

    private float getBrightness(Activity activity) {
        float screenBrightness = 0.2f;
        try {
            screenBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) / 255f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return screenBrightness;
    }

    @Override
    public void onSeek(float percentage) {
        setBrightness((Activity) getContext(), getSeekBar().getPercentage());
    }

    @Override
    public void locate(int offset, int l, int t, int r, int b) {
        Log.e(TAG, "locate");
        layout(l + offset, t, r + offset, b);
    }
}
