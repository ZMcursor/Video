package com.zmcursor.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.dl7.player.media.IjkPlayerView;
import com.zmcursor.videowidget.VideoView;
import com.zmcursor.zmfilepicker.FileList;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;
    private IjkPlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        videoView = (VideoView) findViewById(R.id.video_view);
//        mPlayerView = (IjkPlayerView) findViewById(R.id.videos_view);

        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                DialogProperties properties = new DialogProperties();
//
//                FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
//                dialog.setTitle("Select a File");
//                dialog.setDialogSelectionListener(new DialogSelectionListener() {
//                    @Override
//                    public void onSelectedFilePaths(String[] files) {
//                        String path = files[0];
//                        Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
//
////                        videoView.openVideo(path, false);
//
//                        mPlayerView.init().setTitle(path).enableOrientation().setVideoPath(path).start();
//                    }
//                });
//                dialog.show();
                startActivity(new Intent(MainActivity.this, ZZActivity.class));
            }
        });

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        LinearLayout container = (LinearLayout) findViewById(R.id.container);
//        container.addView(new BrightnessAdjustView(this));
//        container.addView(new VolumeAdjustView(this));


        Button button2 = (Button) findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("fdhuvufd");
                ListView listView = new FileList(getContext());
                listView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                builder.setView(listView);
                builder.setPositiveButton("ok", null);
                builder.show();
            }
        });
    }

    private Context getContext() {
        return this;
    }
}
