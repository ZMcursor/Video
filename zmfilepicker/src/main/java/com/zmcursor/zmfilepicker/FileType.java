package com.zmcursor.zmfilepicker;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by ZMcursor on 2017/8/31 0031.
 */

public class FileType {

    private static final String TYPE = "type";
    private static final String ICON = "icon";
    private static final String SUFFIX = "suffix";
    private static final String FOLDER = "folder";
    private static final String FILE = "file";

    private Drawable[] icons = null;
    private Drawable folderIcon, fileIcon;
    private HashMap<String, Integer> types;

    public void setIcons(Context context, FileTypeMap iconMap) {
        folderIcon = VectorDrawableCompat.create(context.getResources(), iconMap.getFolderIcon(), null);
        fileIcon = VectorDrawableCompat.create(context.getResources(), iconMap.getFileIcon(), null);
        icons = new Drawable[iconMap.size()];
        types = new HashMap<>();
        for (int i = 0; i < iconMap.size(); i++) {
            icons[i] = VectorDrawableCompat.create(context.getResources(), iconMap.getTypeIcon(i), null);
            for (String suffix : iconMap.getTypeSuffixes(i)) {
                types.put(suffix, i);
            }
        }
    }

    public void setIcons(String json) throws JSONException {
        JSONArray typeArray = new JSONArray(json);
        icons = new Drawable[typeArray.length()];
        types = new HashMap<>();
        for (int i = 0; i < typeArray.length(); i++) {
            JSONObject type = typeArray.getJSONObject(i);
//            icons[i] = getImage(type.getString(ICON));
            JSONArray suffix = type.getJSONArray(SUFFIX);
            for (int j = 0; j < suffix.length(); j++) {
                types.put(suffix.getString(j), i);
            }
        }
    }

    public void setIcons(Context context, String file) throws IOException, JSONException {
        InputStreamReader isr = new InputStreamReader(context.getAssets().open(file), "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = br.readLine()) != null) {
            builder.append(line);
        }
        br.close();
        isr.close();
        setIcons(builder.toString());
    }

    Drawable getIcon(File file) {
        if (file.isDirectory()) return folderIcon;
        else {
            String name = file.getName();
            int index = name.lastIndexOf('.');
            if (index > 0) {
                Integer i = types.get(name.substring(index + 1).toLowerCase());
                if (i != null) return icons[i];
            }
        }
        return fileIcon;
    }

    void init(Context context) {
        if (icons == null) getDefIcons(context);
    }

    private void getDefIcons(Context context) {
        FileTypeMap icons = new FileTypeMap();

        icons.setBasicIcon(R.drawable.ic_folder, R.drawable.ic_file);

        String[] suffix = new String[]{"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        icons.putType(R.drawable.ic_image, suffix);

        suffix = new String[]{"aac", "mp3", "amr", "ogg"};
        icons.putType(R.drawable.ic_music, suffix);

        suffix = new String[]{"mp4", "3gp", "avi", "mov", "rmvb", "mkv", "flv"};
        icons.putType(R.drawable.ic_video, suffix);

        suffix = new String[]{"xml", "json", "cpp"};
        icons.putType(R.drawable.ic_code, suffix);

        suffix = new String[]{"txt", "log", "cfg"};
        icons.putType(R.drawable.ic_txt, suffix);

        suffix = new String[]{"doc", "docx", "wps"};
        icons.putType(R.drawable.ic_doc, suffix);

        suffix = new String[]{"xlsx", "xls", "et"};
        icons.putType(R.drawable.ic_xlsx, suffix);

        suffix = new String[]{"ppt", "pptx", "dps"};
        icons.putType(R.drawable.ic_ppt, suffix);

        suffix = new String[]{"pdf"};
        icons.putType(R.drawable.ic_pdf, suffix);

        setIcons(context, icons);
    }

    void recycle() {
    }

}
