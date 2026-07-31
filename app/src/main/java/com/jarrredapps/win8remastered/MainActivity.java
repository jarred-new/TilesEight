package com.jarrredapps.win8remastered;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Date;
import java.util.List;
import android.animation.ValueAnimator;
import android.widget.ScrollView;
import android.animation.TimeInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.OrientationEventListener;
import android.view.ViewGroup;
import android.graphics.Typeface;
import android.app.ActivityOptions;
import android.os.Build;

public class MainActivity extends Activity {
    public static final int ccLandscape = 5;
    public static final int ccPortrait = 3;
    //public static final int rcLandscape = 4;
    //public static final int rcPortrait = 0;
    
    private GridLayout tileGrid;
    private ImageView wallpaperView;
    private TextView title;
    private OrientationEventListener orientationEventListener;
    private int currentRotation = 0;
    View tile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wallpaperView = findViewById(R.id.wallpaperView);
        title = findViewById(R.id.title);
        
        Typeface segoe = Typeface.createFromAsset(getAssets(), 
                                                  "segoe_ui_light.ttf");
        title.setTypeface(segoe);
        
        // 1. Get an instance of WallpaperManager
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

        // 2. Retrieve the wallpaper as a Drawable
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        // 3. Set it to an ImageView or background        
        wallpaperView.setImageDrawable(wallpaperDrawable);

        
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getPackageManager();

        List<ResolveInfo> apps =
            pm.queryIntentActivities(intent, 0);

        tileGrid = findViewById(R.id.tileGrid);

        //tileGrid.setColumnCount(ccPortrait);
        //tileGrid.setRowCount(rcPortrait);

        for (ResolveInfo app : apps) {

            tile = getLayoutInflater()
                .inflate(R.layout.tile, tileGrid, false);

            final ImageView icon =
                tile.findViewById(R.id.icon);

            final TextView appTitle =
                tile.findViewById(R.id.appTitle);

            icon.setImageDrawable(app.loadIcon(pm));

            Typeface segoer = Typeface.createFromAsset(getAssets(), 
                                                       "segoe_ui_regular.ttf");
            appTitle.setTypeface(segoer);
            appTitle.setText(app.loadLabel(pm));

            final Intent launch =
                pm.getLaunchIntentForPackage(
                app.activityInfo.packageName);
                
            final ActivityOptions options = ActivityOptions.makeCustomAnimation(
                this, 
                R.anim.pers_enter, // The target app enters with this
                R.anim.pers_exit   // Your launcher exits with this
            );
            

            tile.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT <= 29) {
                            // Google blocks makeCustomAnimation starting on Android 10 (API Level 29)
                            // Due to malicious apps preventing to hijacking apps
                            startActivity(launch, options.toBundle());
                        }
                        else {
                            
                        }
//                        overridePendingTransition(R.anim.pers_enter, 
//                                                  R.anim.pers_exit);
                    }
                });
          
            tileGrid.addView(tile);
        }

        orientationEventListener =
            new OrientationEventListener(this) {

            @Override
            public void onOrientationChanged(int orientation) {

                if (orientation == ORIENTATION_UNKNOWN)
                    return;

                int rotation;

                // Orientation is returned in degrees (0-359)
                // 0: Portrait (natural)
                // 90: Landscape (left side up)
                // 180: Upside down
                // 270: Landscape (right side up)
                if (orientation >= 315 || orientation < 45) {
                    rotation = 0;
                } else if (orientation < 135) {
                    rotation = 90;
                } else if (orientation < 225) {
                    rotation = 180;
                } else {
                    rotation = 270;
                }

                if (rotation != currentRotation) {

                    currentRotation = rotation;

                    if (currentRotation == 90 || currentRotation == 180) {
                        //tileGrid.setRowCount(rcLandscape);
                        tileGrid.setColumnCount(ccLandscape);
                        //tileGrid.setOrientation(GridLayout.HORIZONTAL);
                    }
                    if (currentRotation == 180 || currentRotation == 270) {
                        //tileGrid.setRowCount(rcPortrait);
                        tileGrid.setColumnCount(ccPortrait);
                        //tileGrid.setOrientation(GridLayout.VERTICAL);
                    }
                }
            }
        };

        orientationEventListener.enable();

    }

    @Override
    protected void onStart() {
        super.onStart();
        orientationEventListener.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        orientationEventListener.enable();
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        orientationEventListener.disable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        orientationEventListener.disable();
    }



    /*
     @Override
     public void onConfigurationChanged(Configuration newConfig) {
     int orientation =
     getResources().getConfiguration().orientation;

     if (orientation ==
     Configuration.ORIENTATION_LANDSCAPE) {

     tileGrid.setColumnCount(6);

     } else {

     tileGrid.setColumnCount(4);
     }
     super.onConfigurationChanged(newConfig);
     }*/

}
