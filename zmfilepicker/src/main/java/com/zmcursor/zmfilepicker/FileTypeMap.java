package com.zmcursor.zmfilepicker;

import android.support.annotation.DrawableRes;

/**
 * Created by ZMcursor on 2017/8/31 0031.
 */

public class FileTypeMap {

    private int capacity, count = 0;
    @DrawableRes
    private int[] keys;
    private String[][] values;
    @DrawableRes
    private int folderIcon;
    @DrawableRes
    private int fileIcon;

    public FileTypeMap() {
        this(10);
    }

    public FileTypeMap(int initCapacity) {
        capacity = initCapacity;
        keys = new int[capacity];
        values = new String[capacity][];
    }

    public void setBasicIcon(@DrawableRes int folderIcon, @DrawableRes int fileIcon) {
        this.folderIcon = folderIcon;
        this.fileIcon = fileIcon;
    }

    @DrawableRes
    public int getFolderIcon() {
        return folderIcon;
    }

    @DrawableRes
    public int getFileIcon() {
        return fileIcon;
    }

    public void putType(@DrawableRes int resId, String[] suffixes) {
        if (count >= capacity) {
            capacity += (capacity >> 1);
            int[] newKeys = new int[capacity];
            String[][] newValues = new String[capacity][];
            System.arraycopy(keys, 0, newKeys, 0, keys.length);
            System.arraycopy(values, 0, newValues, 0, values.length);
            keys = newKeys;
            values = newValues;
        }
        keys[count] = resId;
        values[count] = suffixes;
        count++;
    }

    @DrawableRes
    int getTypeIcon(int index) {
        if (index < count) return keys[index];
        else throw new IndexOutOfBoundsException();
    }

    String[] getTypeSuffixes(int index) {
        if (index < count) return values[index];
        else throw new IndexOutOfBoundsException();
    }

    int size() {
        return count;
    }
}
