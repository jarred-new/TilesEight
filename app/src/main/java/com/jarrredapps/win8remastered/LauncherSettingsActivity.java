package com.jarrredapps.win8remastered;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toolbar;

public class LauncherSettingsActivity extends Activity {

    private Toolbar activity_settingsToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        activity_settingsToolbar = findViewById(R.id.activity_settingsToolbar);
        activity_settingsToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }                   
            });
        setActionBar(activity_settingsToolbar);

        setTitle("TilesEight Settings");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
    }

}
