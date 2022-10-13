package com.yatik.statussaver.Others;

import android.net.Uri;

import java.io.File;

public class Data {

    private final File file;
    private final Uri fileUri;
    private final String name;
    private String path;
    public static boolean isImage;
    public static boolean isVideo;

    public Data(File file, String name, String path){
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

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }
}
