package com.zmcursor.video;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Created by ZMcursor on 2017/8/30 0030.
 */

public class FileUtils {

    public static boolean deleteFile(Context context, File file) {
        if (file.exists()) {
            if (file.delete()) return true;
            else Toast.makeText(context, "文件删除失败", Toast.LENGTH_SHORT).show();
        } else Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean newFile(Context context, File file) {
        try {
            if (file.createNewFile()) return true;
            else Toast.makeText(context, "文件已存在", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "IOException 无权访问", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean copyFile(Context context, File from, File to) {
        try {
            FileChannel input = new FileInputStream(from).getChannel();
            FileChannel output = new FileOutputStream(to).getChannel();
            output.transferFrom(input, 0, input.size());
            input.close();
            output.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "复制失败", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}
