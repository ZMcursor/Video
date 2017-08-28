package com.zmcursor.videowidget;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

/**
 * Created by ZMcursor on 2017/8/21 0021.
 */

class VolumeAdjustView extends AdjustView {
    private static final String TAG = "VolumeAdjustView";

    AudioManager audioManager;
    private float maxVolume;

    public VolumeAdjustView(Context context) {
        super(context, R.drawable.ic_volume, 0);
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        getSeekBar().setPercentage(getVolume());
    }

//    @Override
//    public void adjust(float px) {
//        super.adjust(px);
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (getSeekBar().getPercentage() * maxVolume), 0);
//    }

    private float getVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume;
    }

    @Override
    public void onSeek(float percentage) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (getSeekBar().getPercentage() * maxVolume), 0);
    }

    @Override
    public void locate(int offset, int l, int t, int r, int b) {
        Log.e(TAG, "locate");
        layout(l - offset, t, r - offset, b);
    }
}
