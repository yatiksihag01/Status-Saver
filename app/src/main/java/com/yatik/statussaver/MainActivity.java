package com.yatik.statussaver;

import static com.yatik.statussaver.Others.CommonClass.wa_status_uri;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.storage.StorageManager;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.Html;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yatik.statussaver.Fragments.DownloadsFragment;
import com.yatik.statussaver.Fragments.ImageFragment;
import com.yatik.statussaver.Fragments.VideoFragment;
import com.yatik.statussaver.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 1;
    ActivityResultLauncher<String[]> mPermissionResultLauncher;
    SharedPreferences sharedPreferences;
    Handler handler = new Handler();
    List<Object> statusList = new ArrayList<>();
    boolean doubleBackToExitPressedOnce = false;
    private boolean isReadPermissionGranted = false;
    private boolean isWritePermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.yatik.statussaver.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.topAppBar.setNavigationOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        binding.navigationView.setNavigationItemSelectedListener(item -> {

            binding.drawerLayout.closeDrawer(GravityCompat.START);

            switch (item.getItemId()) {

                case (R.id.choose_app):
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;

                case (R.id.rate_us):
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.yatik.statussaver")));
                    break;

                case (R.id.share_app):
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_TEXT,
                            "Hey!! check out this awesome status saver app at: https://play.google.com/store/apps/details?id=com.yatik.statussaver");
                    shareIntent.setType("text/plain");
                    startActivity(Intent.createChooser(shareIntent, "Share app via"));
                    break;

                case (R.id.privacy_policy):
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://sites.google.com/view/yatikstatussaver/home")));
                    break;
            }
            return false;
        });

        mPermissionResultLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                    if (result.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                        isReadPermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.READ_EXTERNAL_STORAGE));
                    }

                    if (result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                        isWritePermissionGranted = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
                    }

                    if (!isReadPermissionGranted && !isWritePermissionGranted) {
                        showNoPermissionAlert();
                    }

                    if (isReadPermissionGranted && isWritePermissionGranted) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            replaceFragment(new ImageFragment(statusList));
                        }
                    }
                });

        requestPermission();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {
                case (R.id.images):
                    replaceFragment(new ImageFragment(statusList));
                    break;
                case (R.id.videos):
                    replaceFragment(new VideoFragment(statusList));
                    break;
                case (R.id.downloads):
                    replaceFragment(new DownloadsFragment());
                    break;
            }
            return true;
        });
    }


    private void requestPermission() {

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
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            replaceFragment(new ImageFragment(statusList));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            sharedPreferences = getSharedPreferences("tree", Context.MODE_PRIVATE);
            String uriString = sharedPreferences.getString("treeUriString", "");

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

                new Thread(() -> {
                    getListAboveQ(wa_status_uri);
                    handler.post(() -> replaceFragment(new ImageFragment(statusList)));
                }).start();
            }
        }
    }


    private void aboveQPermission() {

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
            Toast.makeText(MainActivity.this, "can't find an app to select media, please active your 'Files' app and/or update your phone Google play services", Toast.LENGTH_LONG).show();
        }

    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment, null)
                .setReorderingAllowed(true)
                .commit();
    }


    private void showNoPermissionAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Permission Denied")
                .setMessage(R.string.noPermissionAlert)
                .setCancelable(false)
                .setIcon(R.drawable.permission_message)
                .setPositiveButton(Html.fromHtml("<font color='#89B3F7'>SETTINGS</font>"), (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                    intent.setData(uri);
                    MainActivity.this.startActivity(intent);
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //save shared preference to check whether app specific folder permission granted or not
                sharedPreferences = getSharedPreferences("tree", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("treeUriString", String.valueOf(wa_status_uri));
                editor.apply();

                new Thread(() -> {
                    getListAboveQ(wa_status_uri);
                    handler.post(() -> replaceFragment(new ImageFragment(statusList)));
                }).start();

            }
        }
    }


    public void getListAboveQ(Uri uriMain) {

        ContentResolver contentResolver = getContentResolver();
        Uri buildChildDocumentsUriUsingTree = DocumentsContract.buildChildDocumentsUriUsingTree(uriMain, DocumentsContract.getDocumentId(uriMain));

        try (Cursor cursor = contentResolver.query(buildChildDocumentsUriUsingTree, new String[]{"document_id"}, null, null, null)) {
            while (cursor.moveToNext()) {
                if (!DocumentsContract.buildDocumentUriUsingTree(uriMain, cursor.getString(0)).toString().endsWith(".nomedia")) {

                    statusList.add(DocumentsContract.buildDocumentUriUsingTree(uriMain, cursor.getString(0)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

}