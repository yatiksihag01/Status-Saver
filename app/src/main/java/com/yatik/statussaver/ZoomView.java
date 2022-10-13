package com.yatik.statussaver;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.yatik.statussaver.Others.CommonClass;
import com.yatik.statussaver.databinding.ActivityImageDetailsBinding;

import java.io.File;
import java.util.Objects;

public class ZoomView extends AppCompatActivity {

    ActivityImageDetailsBinding binding;
    String filePath;
    String sentFrom;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String mimeType;

        super.onCreate(savedInstanceState);
        binding = ActivityImageDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, getTheme()));

        FrameLayout videoFrame = findViewById(R.id.videoFrame);

        filePath = getIntent().getStringExtra("filePath");
        sentFrom = getIntent().getStringExtra("sentFrom");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Objects.equals(sentFrom, "downloadsAdapter")){
            fileUri = Uri.parse(filePath);

        } else{
            fileUri = Uri.fromFile(new File(filePath));

        }

        if (filePath.endsWith(".jpg")){
            mimeType = "image/jpg";
            binding.mainImgContainer.setVisibility(View.VISIBLE);
            Glide.with(this).load(fileUri).into(binding.mainImgContainer);

        } else {

            mimeType = "video/mp4";
            videoFrame.setVisibility(View.VISIBLE);

            MediaController mediaController = new MediaController(this);
            binding.videoView.setOnPreparedListener(mp -> {
                mp.start();
                mediaController.show(0);
            });
            binding.videoView.setMediaController(mediaController);
            binding.videoView.setVideoURI(fileUri);
            binding.videoView.requestFocus();
        }


        binding.shareButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){

                File file = new File(filePath);
                Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), "com.yatik.statussaver.fileprovider", file);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
                shareIntent.setType(mimeType);
                startActivity(Intent.createChooser(shareIntent, "Send file to"));

            } else {

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.setType(mimeType);
                startActivity(Intent.createChooser(shareIntent, "Send file to"));

            }
        });

        binding.forwardButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setPackage(CommonClass.WA_PACKAGE_NAME);
            intent.setType(mimeType);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                File file = new File(filePath);
                Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), "com.yatik.statussaver.fileprovider", file);
                intent.setData(shareUri);

            } else {
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            }
            startActivity(intent);
        });

        binding.editButton.setOnClickListener(v -> {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Backup To Google Photos")
                    .setMessage(R.string.BackupMessage)
                    .setCancelable(true)
                    .setIcon(R.drawable.permission_message)
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                    .setPositiveButton("Yes, I'm Sure", (dialog, which) -> {

                        Intent launchIntent = new Intent(Intent.ACTION_SEND);
                        try{
                            launchIntent.setPackage("com.google.android.apps.photos");
                            launchIntent.setType(mimeType);
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {

                                File file = new File(filePath);
                                Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), "com.yatik.statussaver.fileprovider", file);
                                launchIntent.putExtra(Intent.EXTRA_STREAM, shareUri);

                            } else {
                                launchIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                            }
                            startActivity(launchIntent);
                        } catch (Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=com.yatik.statussaver.fileprovider")));
                        }

                        startActivity(launchIntent);

                    });
            AlertDialog dialog = builder.create();
            dialog.show();


        });

        binding.saveButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                CommonClass.saveFileAboveQ(this, filePath);
            } else {
                CommonClass.saveFileBelowQ(this, filePath);
            }
        });

    }


    @Override
    public void onBackPressed() {
        if(binding.videoView.isPlaying()){ // check if video is playing then hide views
            finish();
        }else{
            super.onBackPressed();
        }
    }

}