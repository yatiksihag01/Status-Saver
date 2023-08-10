package com.yatik.statussaver.ui;

import static com.yatik.statussaver.utils.Utilities.folderAboveQ;
import static com.yatik.statussaver.utils.Utilities.folderBelowQ;
import static com.yatik.statussaver.utils.Utilities.waPackageName;
import static com.yatik.statussaver.utils.Utilities.wa_status_uri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Html;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.yatik.statussaver.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 1;
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    SharedPreferences mSharedPreferences;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;
    private boolean isClickedSettingsButton = false;
    private boolean isClickedGP = false;
    Handler handler = new Handler();

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

//        Objects.requireNonNull(getSupportActionBar()).hide();
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, getTheme()));

        setUri();

        mPermissionResultLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

            if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                isReadPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
            }

            if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                isWritePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            }

            if (isReadPermissionGranted && isWritePermissionGranted) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    handler.postDelayed(() -> {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }, 1500);
                }
            } else {
                showNoPermissionAlert();
            }
        });

        requestPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isClickedSettingsButton){
            requestPermission();
            isClickedSettingsButton = false;
        }
        mSharedPreferences = getSharedPreferences("tree", Context.MODE_PRIVATE);
        String uriString = mSharedPreferences.getString("treeUriString", "");
        if (isClickedGP && uriString.matches("")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Folder Access Required")
                    .setMessage(R.string.folderAccessMessage)
                    .setCancelable(false)
                    .setIcon(R.drawable.permission_message)
                    .setPositiveButton(Html.fromHtml("<font color='#89B3F7'>GRANT PERMISSION</font>"), (dialog, which) -> aboveQPermission());
            AlertDialog dialog = builder.create();
            dialog.show();
            isClickedGP = false;
        }
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



    private void requestPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

            isReadPermissionGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;

            isWritePermissionGranted = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED;

            List<String> permissionRequest = new ArrayList<>();

            if (!isReadPermissionGranted) {
                permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (!isWritePermissionGranted) {
                permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            if (!permissionRequest.isEmpty()) {
                mPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));

            } else {
                handler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1500);
            }
            return;
        }

        mSharedPreferences = getSharedPreferences("tree", Context.MODE_PRIVATE);
        String uriString = mSharedPreferences.getString("treeUriString", "");

        if (uriString.matches("")) {
            //ask for folder permission

            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Folder Access Required")
                    .setMessage(R.string.folderAccessMessage)
                    .setCancelable(false)
                    .setIcon(R.drawable.permission_message)
                    .setPositiveButton(Html.fromHtml("<font color='#89B3F7'>GRANT PERMISSION</font>"), (dialog, which) -> aboveQPermission());
            AlertDialog dialog = builder.create();
            dialog.show();

        } else {
            handler.postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 1500);
        }
    }


    private void aboveQPermission() {

        isClickedGP = true;

        try {
            Intent createOpenDocumentTreeIntent = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                createOpenDocumentTreeIntent = ((StorageManager) getSystemService(STORAGE_SERVICE)).getPrimaryStorageVolume().createOpenDocumentTreeIntent();
            }

            assert createOpenDocumentTreeIntent != null;
            String replace = createOpenDocumentTreeIntent.getParcelableExtra("android.provider.extra.INITIAL_URI").toString().replace("/root/", "/document/");
            createOpenDocumentTreeIntent.putExtra("android.provider.extra.INITIAL_URI", Uri.parse(replace + "%3A" + "Android%2Fmedia"));
            startActivityForResult(createOpenDocumentTreeIntent, REQUEST_CODE);
        } catch (Exception unused) {
            Toast.makeText(SplashActivity.this, "can't find an app to select media, please active your 'Files' app and/or update your phone Google play services", Toast.LENGTH_LONG).show();
            isClickedGP = false;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //save shared preference to check whether app specific folder permission granted or not
                mSharedPreferences = getSharedPreferences("tree", MODE_PRIVATE);
                SharedPreferences.Editor editor = mSharedPreferences.edit();

                editor.putString("treeUriString", String.valueOf(wa_status_uri));
                editor.apply();

                handler.postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }, 1500);
            }
        }
    }

    private void showNoPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permission Denied")
                .setMessage(R.string.noPermissionAlert)
                .setCancelable(false)
                .setIcon(R.drawable.permission_message)
                .setPositiveButton(Html.fromHtml("<font color='#89B3F7'>SETTINGS</font>"), (dialog, which) -> {
                    isClickedSettingsButton = true;
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", SplashActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    SplashActivity.this.startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
