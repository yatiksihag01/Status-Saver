package com.yatik.statussaver.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.yatik.statussaver.R;
import com.yatik.statussaver.ui.fragments.DownloadsFragment;
import com.yatik.statussaver.ui.fragments.ImageFragment;
import com.yatik.statussaver.ui.fragments.VideoFragment;
import com.yatik.statussaver.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.yatik.statussaver.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        replaceFragment(new ImageFragment());

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

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case (R.id.images):
                    replaceFragment(new ImageFragment());
                    break;
                case (R.id.videos):
                    replaceFragment(new VideoFragment());
                    break;
                case (R.id.downloads):
                    replaceFragment(new DownloadsFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_frame_layout, fragment, null)
                .setReorderingAllowed(true)
                .commit();
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