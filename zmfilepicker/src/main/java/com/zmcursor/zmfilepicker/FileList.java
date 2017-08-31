package com.zmcursor.zmfilepicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by ZMcursor on 2017/8/30 0030.
 */

public class FileList extends ListView implements AdapterView.OnItemClickListener {

    private Paint paint;
    private int padding;
    private FileType fileType = null;
    private FileListAdapter adapter;
    private File directory = null;

    public FileList(Context context) {
        this(context, null);
    }

    public FileList(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public FileList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FileList(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private void initView(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        padding = getResources().getDimensionPixelSize(R.dimen.spacing8);
        adapter = new FileListAdapter();
        fileType = new FileType();
        fileType.init(getContext());
        adapter.setDirectory();
        setAdapter(adapter);
        setOnItemClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setFileType(FileType fileType) {
        if (fileType != null) this.fileType.recycle();
        this.fileType = fileType;
    }

    public void setDirectory(File directory) {
        if (directory == null) return;
        this.directory = directory;
        adapter.setDirectory();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (adapter.fileList[position].isDirectory()) {
            directory = adapter.fileList[position];
            adapter.setDirectory();
        }
    }

    private class FileListAdapter extends BaseAdapter {

        //        private File directory;
        private File[] fileList;

        private void setDirectory() {
            if (directory == null)
                directory = Environment.getExternalStorageDirectory();
            if (directory.isDirectory()) fileList = directory.listFiles();
            else fileList = new File[]{directory};
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return fileList.length;
        }

        @Override
        public Object getItem(int position) {
            return fileList[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FileListItem item;
            if (convertView == null) {
                item = new FileListItem(getContext());
            } else item = (FileListItem) convertView;
            item.setFile(fileList[position]);
            return item;
        }
    }

//    private class MyFile {
//        private File file;
//        private boolean isSelect;
//
//        private void set(File parent, String name) {
////            this.file = file;
//            isSelect = false;
//        }
//
//        private void set(File file) {
//            this.file = file;
//            isSelect = false;
//        }
//    }

    public class FileListItem extends ViewGroup {

        private boolean isSelected = false;
        private IconView iconView;
        private TextView fileName;

        public FileListItem(Context context) {
            super(context);
            initView(context);
        }

        private void initView(Context context) {
            iconView = new IconView(context);
            addView(iconView);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            fileName = new TextView(context);
            fileName.setTextSize(16);
            fileName.setSingleLine();
            fileName.setGravity(Gravity.CENTER_VERTICAL);
            addView(fileName, layoutParams);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            iconView.measure(widthMeasureSpec, heightMeasureSpec);
            fileName.measure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), getResources().getDimensionPixelSize(R.dimen.size56));
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            iconView.layout(padding, 0, getMeasuredHeight() + padding, getHeight());
            fileName.layout(getMeasuredHeight() + (padding << 1), padding, getMeasuredWidth(), getHeight() * 2 / 3);
        }

        @Override
        public void onDrawForeground(Canvas canvas) {
            super.onDrawForeground(canvas);
        }

        private void onSelect(boolean isSelected) {
            if (this.isSelected != isSelected) {
                this.isSelected = isSelected;
                invalidate();
            }
        }

        public void setFile(File file) {
            iconView.setBg(fileType.getIcon(file));
            fileName.setText(file.getName());
        }

        private class IconView extends View {
            Drawable icon;

            public IconView(Context context) {
                super(context);
            }

            private void setBg(Drawable drawable) {
                if (drawable == icon) return;
                icon = drawable;
                invalidate();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                paint.setColor(Color.BLUE);
                int c = canvas.getHeight() >> 1;
                canvas.drawCircle(c, c, c - padding, paint);
                int a = icon.getIntrinsicHeight() >> 1;
                icon.setBounds(c - a, c - a, c + a, c + a);
                icon.draw(canvas);
            }
        }
    }
}
