package com.yatik.statussaver;

import static com.yatik.statussaver.Others.CommonClass.folderAboveQ;
import static com.yatik.statussaver.Others.CommonClass.folderBelowQ;
import static com.yatik.statussaver.Others.CommonClass.waPackageName;
import static com.yatik.statussaver.Others.CommonClass.wa_status_uri;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, getTheme()));

        setUri();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }, 1500);
    }

    public void setUri() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        boolean name = sharedPreferences.getBoolean("switch_preference_wab", false);
        if (name) {

            wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses");
            waPackageName = "com.whatsapp.w4b";
            folderBelowQ = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "WhatsApp Business/Media/.Statuses");

            folderAboveQ = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android/media/com.whatsapp.w4b/WhatsApp Business/Media/.Statuses");

        } else {
            wa_status_uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
            waPackageName = "com.whatsapp";
            folderBelowQ = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "WhatsApp/Media/.Statuses");

            folderAboveQ = new File(Environment.getExternalStorageDirectory()
                    + File.separator + "Android/media/com.whatsapp/WhatsApp/Media/.Statuses");
        }

    }
}