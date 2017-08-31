package com.zmcursor.video;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dl7.player.media.IjkPlayerView;
import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class ZZActivity extends AppCompatActivity {

    private static final short maxLength = 10;
    private ListView list;
    private ListAdapter adapter;
    private IjkPlayerView playerView;
    private File[] files = null;
    private File directory;
    private Handler handler = null;
    private Dialog progressDialog = null;
    private FilePickerDialog filePickerDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        playerView = (IjkPlayerView) findViewById(R.id.player_view);
        list = (ListView) findViewById(R.id.zm_list);
        directory = new File(getFilesDir(), "ZZto/");
//        Log.e("ZZActivity", directory.getAbsolutePath());
        if (directory.exists() || directory.mkdir()) {
            files = directory.listFiles();
        }
        adapter = new ListAdapter();
        list.setAdapter(adapter);
        playerView.init().enableOrientation();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playerView.reset();
                playerView.setTitle(files[position].getName()).setVideoPath(files[position].getAbsolutePath()).start();
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ZZActivity.this);
                builder.setTitle("操作文件");
                final View radioGroup = getLayoutInflater().inflate(R.layout.dialog_long, null);
                final EditText editText = (EditText) radioGroup.findViewById(R.id.file_rename);
                String name = files[position].getName();
                editText.setText(name);
                editText.setSelection(name.length());
                builder.setView(radioGroup);
//                builder.setMessage("确定删除" + files[position].getName() + "?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RadioButton renameBtn = (RadioButton) radioGroup.findViewById(R.id.ratio_rename);
                        RadioButton deleteBtn = (RadioButton) radioGroup.findViewById(R.id.ratio_delete);
                        if (renameBtn.isChecked()) {
                            try {
                                File newFile = new File(directory, editText.getText().toString());
                                if (newFile.createNewFile()) {
                                    if (files[position].renameTo(newFile))
                                        Toast.makeText(ZZActivity.this, "重命名成功", Toast.LENGTH_SHORT).show();
                                    else {
                                        newFile.delete();
                                        Toast.makeText(ZZActivity.this, "重命名失败", Toast.LENGTH_SHORT).show();
                                    }
                                    refreshList();
                                } else
                                    Toast.makeText(ZZActivity.this, "重命名失败", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(ZZActivity.this, "重命名失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (deleteBtn.isChecked()) {
                            if (files[position].delete()) {
                                Toast.makeText(ZZActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                refreshList();
                            } else
                                Toast.makeText(ZZActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    private void addFile() {
        filePickerDialog(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(final String[] files) {
                if (handler == null) {
                    handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.what == 0) {
                                refreshList();
                                Toast.makeText(ZZActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ZZActivity.this, "复制失败", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog(true);
                            return true;
                        }
                    });
                }
                try {
                    File from = new File(files[0]);
                    String name = from.getName();
                    int r = name.lastIndexOf('.');
                    if (r > 0) name = name.substring(0, r);
                    File to = new File(directory, name);
                    Log.e("ZZActivity", from.getAbsolutePath());
                    Log.e("ZZActivity", to.getAbsolutePath());
                    if (to.createNewFile()) {
                        copy(from, to);
                        progressDialog(false);
                    } else handler.sendEmptyMessage(1);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    private void deleteFile() {
        filePickerDialog(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                File file = new File(files[0]);
                if (file.delete())
                    Toast.makeText(ZZActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                else Toast.makeText(ZZActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void progressDialog(boolean isHide) {
        if (progressDialog != null) {
            if (isHide) progressDialog.dismiss();
            else progressDialog.show();
        } else if (!isHide) {
            progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
            progressDialog.setTitle("复制中");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
        }
    }

    private void filePickerDialog(DialogSelectionListener listener) {
        if (filePickerDialog != null) {
            if (listener != null) filePickerDialog.setDialogSelectionListener(listener);
            filePickerDialog.show();
        } else {
            filePickerDialog = new FilePickerDialog(this, new DialogProperties());
            filePickerDialog.setTitle("Select a File");
            if (listener != null) filePickerDialog.setDialogSelectionListener(listener);
            filePickerDialog.show();
        }
    }

    private void refreshList() {
        files = directory.listFiles();
        adapter.notifyDataSetChanged();
//        list.setAdapter(adapter);
    }

    private void copy(final File from, final File to) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileChannel input = new FileInputStream(from).getChannel();
                    FileChannel output = new FileOutputStream(to).getChannel();
                    output.transferFrom(input, 0, input.size());
                    input.close();
                    output.close();
                    handler.sendEmptyMessage(0);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(1);
                    to.delete();
                }
            }
        }).start();
    }

    private class ListAdapter extends BaseAdapter {

        //        private ArrayList<VFile> files;
        private int padding;

        private ListAdapter() {
//            files = new ArrayList<>();
            padding = getResources().getDimensionPixelSize(R.dimen.spacing16);
        }

        @Override
        public int getCount() {
            return files == null ? 0 : files.length;
        }

        @Override
        public Object getItem(int position) {
            return files[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView;
            if (convertView != null) textView = (TextView) convertView;
            else {
                textView = new TextView(parent.getContext());
                textView.setLayoutParams(new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT, ListView.LayoutParams.WRAP_CONTENT));
                textView.setPadding(padding, padding, padding, padding);
                textView.setTextSize(16);
            }
            textView.setText(files[position].getName());
            return textView;
        }

//        private class ViewHolder {
//            private TextView textView;
//
//            private ViewHolder(Context context) {
//                textView = new TextView(context);
//                textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                textView.setPadding(padding, padding, padding, padding);
//                textView.setTextSize(16);
//            }
//        }

        private class VFile {
            private String name;
            private int date;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) addFile();
        else deleteFile();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        playerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        playerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        playerView.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        playerView.configurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return playerView.handleVolumeKey(keyCode) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (playerView.onBackPressed()) return;
        super.onBackPressed();
    }
}
