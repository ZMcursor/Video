package com.zmcursor.videowidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.ViewGroup;

import java.io.IOException;

/**
 * Created by ZMcursor on 2017/8/24 0024.
 */

public class VideoView extends ViewGroup {

    public static final int Event_VideoPrepared = 0;
    public static final int Event_VideoPlay = 10;
    public static final int Event_VideoPause = 20;
    public static final int Event_VideoStop = 30;
    public static final int Event_VideoSeekComplete = 40;
    public static final int Event_VideoCompletion = 50;


    private int width, height;
    private VideoControllerView controllerView;
    private VideoPlayer videoPlayer;
    private boolean isFullScreen = false, autoPlay = false;
    private VideoPlayListener listener = null;
    private String path;

    public VideoView(Context context) {
        this(context, null);
    }

    public VideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        controllerView = new VideoControllerView(context);
        videoPlayer = new VideoPlayer(context);
        addView(videoPlayer);
        addView(controllerView);
        setWillNotDraw(false);
        setListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        if (isFullScreen) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            float f = 4 / 3f;
            if (videoPlayer.getRatio() < f) {
                height = (int) (width / f + 0.5f);
            } else {
                height = (int) (width / videoPlayer.getRatio() + 0.5f);
            }
        }
        videoPlayer.measure(width, height);
        controllerView.measure(width, height);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l = (width - videoPlayer.getMeasuredWidth()) >> 1;
        t = (height - videoPlayer.getMeasuredHeight()) >> 1;
        videoPlayer.layout(l, t, l + videoPlayer.getMeasuredWidth(), t + videoPlayer.getMeasuredHeight());
        controllerView.layout(0, 0, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
    }

    public void openVideo(String path, boolean autoPlay) {
        this.autoPlay = autoPlay;
        this.path = path;
        try {
            videoPlayer.openVideo(path);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) listener.onError(5200);
        }
    }

    public boolean play() {
        if (videoPlayer.getMediaPlayer().isPlaying()) return true;
        if (videoPlayer.play()) {
            controllerView.play();
            return true;
        }
        return false;
    }

    public void pause() {
        controllerView.pause();
        videoPlayer.getMediaPlayer().pause();
    }

    public void stop() {
        controllerView.pause();
        videoPlayer.getMediaPlayer().stop();
    }

    public void seekTo(int position) {
        controllerView.seekTo(position);
        videoPlayer.getMediaPlayer().seekTo(position * 1000);
    }

    public void exit() {
        controllerView.pause();
        videoPlayer.getMediaPlayer().stop();
        mExit();
    }

    public void fullScreen(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
        changeSize();
        controllerView.fullScreen(isFullScreen);
    }

    public void setVideoPlayListener(VideoPlayListener videoPlayListener) {
        this.listener = videoPlayListener;
    }

    public void setListener() {
        videoPlayer.setVideoPlayListener(new VideoPlayer.VideoPlayListener() {
            @Override
            public void onPrepared() {
                controllerView.init(path.substring(path.lastIndexOf('/') + 1),
                        videoPlayer.getMediaPlayer().getDuration(), getFrameImage());
                if (listener != null) listener.onPrepared();
                if (autoPlay) play();
            }

//            @Override
//            public void onPlay() {
//                if (listener != null) listener.onPrepared();
//            }

            @Override
            public void onSeekComplete() {
                if (listener != null) listener.onSeekComplete();
            }

            @Override
            public void onCompletion() {
                controllerView.pause();
                if (listener != null) listener.onCompletion();
            }

            @Override
            public void onError(int code) {
                if (listener != null) listener.onError(code);
            }
        });

        controllerView.setVideoControllerListener(new VideoControllerView.VideoControllerListener() {
            @Override
            public boolean onPlay() {
                return videoPlayer.play();
            }

            @Override
            public void onPause() {
                videoPlayer.getMediaPlayer().pause();
            }

            @Override
            public void onSeek(int offset) {
                videoPlayer.seek(offset);
            }

            @Override
            public void onSeekTo(float percentage) {
                videoPlayer.seek(percentage);
            }

            @Override
            public void onFullScreen(boolean isFullScreen) {
                VideoView.this.isFullScreen = isFullScreen;
                changeSize();
            }

            @Override
            public void onExit() {
                videoPlayer.getMediaPlayer().stop();
                mExit();
            }
        });
    }

    private Bitmap getFrameImage() {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime();//获取第一帧图片
        mmr.release();//释放资源
        return bitmap;
    }

    private void changeSize() {

    }

    private void mExit() {

    }

    public interface VideoPlayListener {
        void onPrepared();

//        void onPause();

//        void onStop();

        void onSeekComplete();

        void onCompletion();

        /**
         * @param code
         */
        void onError(int code);
    }
}
