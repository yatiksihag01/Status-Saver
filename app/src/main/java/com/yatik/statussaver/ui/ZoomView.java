package com.yatik.statussaver.ui;

import static com.yatik.statussaver.utils.Utilities.getAbsolutePath;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.yatik.statussaver.R;
import com.yatik.statussaver.databinding.ActivityZoomViewBinding;
import com.yatik.statussaver.repository.DefaultStatusRepository;
import com.yatik.statussaver.utils.Utilities;

import java.io.File;

public class ZoomView extends AppCompatActivity {

    ActivityZoomViewBinding binding;
    String filePath;
    Uri fileUri;
    StatusViewModel viewModel;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String mimeType;
        binding = ActivityZoomViewBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black, getTheme()));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.black, getTheme()));

         viewModel = new ViewModelProvider(
                this, new StatusViewModelFactory(new DefaultStatusRepository(this))
        ).get(StatusViewModel.class);

        FrameLayout videoFrame = findViewById(R.id.videoFrame);

        filePath = getIntent().getStringExtra("filePath");
        fileUri = Uri.parse(filePath);

        CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(this);
        circularProgressDrawable.setStrokeWidth(5f);
        circularProgressDrawable.setCenterRadius(30f);
        circularProgressDrawable.start();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(circularProgressDrawable);

        if (filePath.endsWith(".jpg")) {
            mimeType = "image/jpg";
            binding.mainImgContainer.setVisibility(View.VISIBLE);
            Glide.with(this).load(fileUri).apply(requestOptions).into(binding.mainImgContainer);

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
            if (filePath.startsWith("file")) {

                File file = new File(getAbsolutePath(filePath));
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
            intent.setPackage(Utilities.waPackageName);
            intent.setType(mimeType);

            if (filePath.startsWith("file")) {
                String absolutePath = getAbsolutePath(filePath);
                File file = new File(absolutePath);
                Uri shareUri = FileProvider.getUriForFile(getApplicationContext(), "com.yatik.statussaver.fileprovider", file);
                intent.putExtra(Intent.EXTRA_STREAM, shareUri);

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
                    .setNegativeButton(Html.fromHtml("<font color='#89B3F7'>NO</font>"), (dialog, which) -> dialog.dismiss())
                    .setPositiveButton(Html.fromHtml("<font color='#89B3F7'>YES</font>"), (dialog, which) -> {

                        Intent launchIntent = new Intent(Intent.ACTION_SEND);
                        try {
                            launchIntent.setPackage("com.google.android.apps.photos");
                            launchIntent.setType(mimeType);
                            if (filePath.startsWith("file")) {
                                String absolutePath = getAbsolutePath(filePath);
                                File file = new File(absolutePath);
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

            viewModel.saveStatus(fileUri, mimeType);
            if (viewModel.isFileSaved()) {
                Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Unable to save this file", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onBackPressed() {
        if (binding.videoView.isPlaying()) { // check if video is playing then hide views
            finish();
        } else {
            super.onBackPressed();
        }
    }

}