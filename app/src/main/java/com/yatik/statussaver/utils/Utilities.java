package com.yatik.statussaver.utils;

import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Utilities {

    public static final int SPAN_COUNT = 2;
    public static final String FOLDER_NAME = "StatusSaver";
    public static final File SAVED_FOLDER_FILE = new File(Environment.getExternalStorageDirectory()
            + File.separator + "Download/" + FOLDER_NAME);
    public static Uri wa_status_uri;
    public static String waPackageName;
    public static File folderBelowQ;
    public static File folderAboveQ;

    public static boolean sdk29OrUp() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static Uri getDownloadsFolderUri() {
        return Uri.parse(Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER_NAME + "/");
    }

    public static String getAbsolutePath(String filePath) {
        // filePath => file:/storage/emulated/0/WhatsApp/Media/.Statuses/xyz.jpg
        // absolutePath => /storage/emulated/0/WhatsApp/Media/.Statuses/xyz.jpg
        return filePath.substring(5);
    }

}
