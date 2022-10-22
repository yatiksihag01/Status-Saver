package com.yatik.statussaver.Others;

import android.net.Uri;

import java.io.File;

public class Data {

    public static boolean isImage;
    public static boolean isVideo;
    private final File file;
    private final Uri fileUri;
    private final String name;
    private String path;

    public Data(File file, String name, String path) {
        this.file = file;
        this.name = name;
        this.path = path;

        isImage = file.getName().endsWith(".jpg");
        isVideo = file.getName().endsWith(".mp4");
        fileUri = Uri.fromFile(new File(path));
    }

    public File getFile() {
        return file;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
