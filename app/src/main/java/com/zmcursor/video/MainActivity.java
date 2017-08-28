package com.zmcursor.video;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.zmcursor.videowidget.VideoView;

public class MainActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        android.widget.VideoView videoVi;

        videoView = (VideoView) findViewById(R.id.video_view);

        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProperties properties = new DialogProperties();

                FilePickerDialog dialog = new FilePickerDialog(MainActivity.this, properties);
                dialog.setTitle("Select a File");
                dialog.setDialogSelectionListener(new DialogSelectionListener() {
                    @Override
                    public void onSelectedFilePaths(String[] files) {
                        String path = files[0];
                        Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();

                        videoView.openVideo(path, false);
                    }
                });
                dialog.show();
            }
        });

//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        LinearLayout container = (LinearLayout) findViewById(R.id.container);
//        container.addView(new BrightnessAdjustView(this));
//        container.addView(new VolumeAdjustView(this));
    }
}
