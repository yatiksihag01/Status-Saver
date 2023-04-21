package com.yatik.statussaver.ui

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceFragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yatik.statussaver.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Toast.makeText(this, "Saving changes...", Toast.LENGTH_SHORT).show()
        //To re-fetch all data
        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
        finish()
    }

    class StatusSaverPreferenceFragment : PreferenceFragment() {
        @Deprecated("Deprecated in Java")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.prefrences)
        }

    }

}