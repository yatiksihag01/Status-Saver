package com.yatik.statussaver.Others;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonClass {

    public static final int SPAN_COUNT = 2;

    public static final String FOLDER_NAME = "StatusSaver";

    public static final String WA_PACKAGE_NAME = "com.whatsapp";

    public static final File FOLDER_BELOW_Q = new File(Environment.getExternalStorageDirectory()
            + File.separator + "WhatsApp/Media/.Statuses");

    public static final File FOLDER_ABOVE_Q = new File(Environment.getExternalStorageDirectory()
            + File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses");

    public static final File SAVED_FOLDER_FILE = new File(Environment.getExternalStorageDirectory()
            + File.separator + "Download/" + FOLDER_NAME);

    public static final Uri wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");

    public static final Uri SAVED_FOLDER_URI = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3ADownload%2F" + FOLDER_NAME);

    public static void saveFileBelowQ(Context context, String filePath){

        File root = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FOLDER_NAME);

        if (!root.exists()) {
            root.mkdirs();
        }

        String path = root + File.separator + Uri.parse(filePath).getLastPathSegment();

        if (new File(path).exists()){
            Toast.makeText(context, "File Already Saved!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            File file = new File(filePath);
            InputStream is = new FileInputStream(file);
            OutputStream os = new FileOutputStream(path);
            byte[] fileData = new byte[is.available()];
            is.read(fileData);
            os.write(fileData);
            is.close();
            os.close();

            MediaScannerConnection.scanFile(context,
                    new String[] { file.toString() }, null,
                    (pathToFile, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + pathToFile + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
            });

            Toast.makeText(context, "Saved file successfully.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            Toast.makeText(context, "Unable To Save This File.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static void saveFileAboveQ(Context context, String filePath){
        File file = new File(filePath);
        String fileName;
        String mimeType;
        Uri fileUri = Uri.parse(filePath);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-ssmmhh", Locale.getDefault());

        if (file.getName().endsWith(".jpg")) {
            mimeType = "image/jpg";
            fileName = "IMG-" + sdf.format(new Date()) + ".jpg";
        } else {
            mimeType = "video/mp4";
            fileName = "VID-" + sdf.format(new Date()) + ".mp4";
        }

        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();

        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER_NAME + "/");

        try {
            Uri resultUri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues);
            OutputStream os = contentResolver.openOutputStream(resultUri);
            InputStream is = contentResolver.openInputStream(fileUri);
            byte[] fileData = new byte[is.available()];
            is.read(fileData);
            os.write(fileData);
            is.close();
            os.close();

            Toast.makeText(context, "File Saved Successfully", Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            Toast.makeText(context, "Unable To Save This File", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }


}
