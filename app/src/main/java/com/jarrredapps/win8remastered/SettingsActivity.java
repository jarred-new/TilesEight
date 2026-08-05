package com.jarrredapps.win8remastered;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Display the fragment inside the FrameLayout container
        getFragmentManager().beginTransaction()
            .replace(R.id.settings_container, new SettingsFragment())
            .commit();
    }
}
