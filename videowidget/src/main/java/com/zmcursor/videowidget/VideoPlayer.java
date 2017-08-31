package com.zmcursor.videowidget;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.TimedText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by ZMcursor on 2017/8/24 0024.
 */

public class VideoPlayer extends SurfaceView implements SurfaceHolder.Callback,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,//MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnTimedTextListener {

    private static final String TAG = "VideoPlayer";

    private MediaPlayer mediaPlayer;
    private VideoPlayListener listener = null;
    private boolean canPlay = false;
    private int position = 0;
    private float ratio = 16 / 9f;// width = 16, height = 9;

    public VideoPlayer(Context context) {
        this(context, null);
    }

    public VideoPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setScreenOnWhilePlaying(true);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnTimedTextListener(this);

        getHolder().addCallback(this);
//        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
//        float n = width / height;
        if (ratio > w / h) {
            h = (int) (w / ratio + 0.5f);
        } else {
            w = (int) (ratio * h + 0.5f);
        }
        setMeasuredDimension(w, h);
    }

    public void openVideo(String path) throws IOException {
        mediaPlayer.reset();
        mediaPlayer.setDataSource(path);
//        mediaPlayer.setDisplay(getHolder());
        mediaPlayer.prepareAsync();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public float getRatio() {
        return ratio;
    }

    public boolean play() {
        if (canPlay) mediaPlayer.start();
        return canPlay;
    }

//    public void pause() {
////        if (listener != null) listener.onPause();
//        mediaPlayer.pause();
//    }

//    public void stop() {
////        if (listener != null) listener.onStop();
//        mediaPlayer.stop();
//    }

    public void seek(float percentage) {
        position = (int) (mediaPlayer.getDuration() * percentage);
        mediaPlayer.seekTo(position);
    }

    public void seek(int offset) {
        position = mediaPlayer.getCurrentPosition() + offset * 1000;
        mediaPlayer.seekTo(position);
    }

    public void setVideoPlayListener(VideoPlayListener listener) {
        this.listener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("TAG", " surfaceCreated ");
//        holder.removeCallback(this);
//        holder.addCallback(this);
        mediaPlayer.setDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("TAG", " surfaceChanged ");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("TAG", " surfaceDestroyed ");
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//
//    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) listener.onCompletion();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        getHolder().removeCallback(this);
//        getHolder().addCallback(this);
//        mediaPlayer.setDisplay(getHolder());

        Log.e("TAG", " onPrepared ");
        canPlay = true;
        if (listener != null) listener.onPrepared();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (listener != null) listener.onSeekComplete();
    }

    @Override
    public void onTimedText(MediaPlayer mp, TimedText text) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        Log.e("TAG", width + " onVideoSizeChanged " + height);
        float f = width / ((float) height);
        if (ratio != f) {
            Log.e("TAG", ratio + " onVideoSizeChanged " + f);
            ratio = f;
            requestLayout();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (listener != null) listener.onError(what);
        return true;
    }

    interface VideoPlayListener {
        void onPrepared();

//        void onPlay();

//        void onPause();

//        void onStop();

        void onSeekComplete();

        void onCompletion();

//        void onVideoSizeChanged();

        /**
         * @param code
         */
        void onError(int code);
    }
}
