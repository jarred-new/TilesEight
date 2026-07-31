package com.jarrredapps.win8remastered;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.List;

public class MainActivity extends Activity {
    public static final int ccLandscape = 5;
    public static final int ccPortrait = 3;
    //public static final int rcLandscape = 4;
    //public static final int rcPortrait = 0;

    private GridLayout tileGrid;
    private ImageView wallpaperView;
    private TextView title;
    private SearchView searchBar;
    private OrientationEventListener orientationEventListener;
    private int currentRotation = 0;
    private View tile;

    private Intent launch;
    private ActivityOptions options;

    private int WALLPAPER_ACCESS_CODE = 200;

    boolean compatibilityCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT <= 29) {
            compatibilityCheck = true;
        } else {
            compatibilityCheck = false;
        }

        wallpaperView = findViewById(R.id.wallpaperView);
        title = findViewById(R.id.title);
        searchBar = findViewById(R.id.searchBar);

        Typeface segoe = Typeface.createFromAsset(getAssets(), 
                                                  "segoe_ui_light.ttf");
        title.setTypeface(segoe);

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) 
                == PackageManager.PERMISSION_DENIED &&
                checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
                AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Before we start...")
                    .setMessage("Please grant the permission to access the wallpaper and other files...")
                    .create();
                dialog.show();

                requestPermissions(new String[]{
                                       Manifest.permission.READ_EXTERNAL_STORAGE,
                                       Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                   }, 
                                   WALLPAPER_ACCESS_CODE);
            }
        }

        wallpaperView = findViewById(R.id.wallpaperView);
        title = findViewById(R.id.title);
        tileGrid = findViewById(R.id.tileGrid);

        // 1. Get an instance of WallpaperManager
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);

        // 2. Retrieve the wallpaper as a Drawable
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        // 3. Set it to an ImageView or background        
        wallpaperView.setImageDrawable(wallpaperDrawable);

        populateTiles();

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

                    // LANDSCAPE
                    if (currentRotation == 90) {
                        //tileGrid.setRowCount(rcLandscape);
                        tileGrid.setColumnCount(ccLandscape);
                        tileGrid.removeAllViews();
                        populateTiles();
                        tileGrid.requestLayout();
                        tileGrid.invalidate();
                        //tileGrid.setOrientation(GridLayout.HORIZONTAL);
                    }
                    if (currentRotation == 270) {
                        //tileGrid.setRowCount(rcLandscape);
                        tileGrid.setColumnCount(ccLandscape);
                        tileGrid.removeAllViews();
                        populateTiles();
                        tileGrid.requestLayout();
                        tileGrid.invalidate();
                        //tileGrid.setOrientation(GridLayout.HORIZONTAL);
                    }

                    // PORTRAIT
                    if (currentRotation == 180) {
                        //tileGrid.setRowCount(rcPortrait);
                        tileGrid.setColumnCount(ccPortrait);
                        tileGrid.removeAllViews();
                        populateTiles();
                        tileGrid.requestLayout();
                        tileGrid.invalidate();
                        //tileGrid.setOrientation(GridLayout.VERTICAL);
                    }
                    if (currentRotation == 0) {
                        //tileGrid.setRowCount(rcPortrait);
                        tileGrid.setColumnCount(ccPortrait);
                        tileGrid.removeAllViews();
                        populateTiles();
                        tileGrid.requestLayout();
                        tileGrid.invalidate();
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

    public void populateTiles() {     
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final PackageManager pm = getPackageManager();

        final List<ResolveInfo> apps =
            pm.queryIntentActivities(intent, 0);

        //tileGrid.setColumnCount(ccPortrait);
        //tileGrid.setRowCount(rcPortrait);

        new Thread(new Runnable(){
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                            @Override
                            public void run() {
                                for (final ResolveInfo app : apps) {

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

                                    tileGrid.addView(tile);

                                    final int i;
                                    for (i = 0; i < tileGrid.getChildCount(); i++) {
                                        final View tile = tileGrid.getChildAt(i);
                                        if (compatibilityCheck) {
                                            launch =
                                                pm.getLaunchIntentForPackage(
                                                apps.get(i).activityInfo.packageName);

                                            options = ActivityOptions.makeCustomAnimation(
                                                MainActivity.this, 
                                                R.anim.pers_enter, // The target app enters with this
                                                R.anim.pers_exit   // Your launcher exits with this
                                            );

                                            tile.setOnClickListener(new OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {                       
                                                        // Google blocks makeCustomAnimation starting on Android 10 (API Level 29)
                                                        // Due to malicious apps preventing to hijacking apps
                                                        startActivity(launch, options.toBundle());
//                        overridePendingTransition(R.anim.pers_enter, 
//                                                  R.anim.pers_exit);
                                                    }
                                                });
                                        } else {
                                            tile.setOnTouchListener(new View.OnTouchListener() {
                                                    @Override
                                                    public boolean onTouch(View v, MotionEvent event) {

                                                        switch (event.getAction()) {
                                                            case MotionEvent.ACTION_DOWN:
//                                                ValueAnimator scaleIn = ValueAnimator.ofFloat(1f, 0.6f);
//                                                scaleIn.setDuration(500);
//                                                scaleIn.setInterpolator(new AccelerateInterpolator());
//                                                scaleIn.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                                                        @Override
//                                                        public void onAnimationUpdate(ValueAnimator animation) {
//                                                            float scale = animation.getAnimatedValue();
//                                                            tile.setScaleX(scale);
//                                                            tile.setScaleY(scale);
//                                                        }
//
//                                                    });
//                                                scaleIn.start();

                                                                tile.animate()
                                                                    .setDuration(200)
                                                                    .setInterpolator(new AccelerateInterpolator())
                                                                    .scaleX(0.8f)
                                                                    .scaleY(0.8f)
                                                                    .start();
                                                                break;

                                                            case MotionEvent.ACTION_UP:
                                                            case MotionEvent.ACTION_CANCEL:
//                                                ValueAnimator scaleOut = ValueAnimator.ofFloat(0.6f, 1f);
//                                                scaleOut.setDuration(500);
//                                                scaleOut.setInterpolator(new AccelerateInterpolator());
//                                                scaleOut.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//                                                        @Override
//                                                        public void onAnimationUpdate(ValueAnimator animation) {
//                                                            float scale = animation.getAnimatedValue();
//                                                            tile.setScaleX(scale);
//                                                            tile.setScaleY(scale);
//                                                        }
//
//                                                    });
//                                                scaleOut.start();
                                                                tile.animate()
                                                                    .setDuration(200)
                                                                    .setInterpolator(new AccelerateInterpolator())
                                                                    .scaleX(1f)
                                                                    .scaleY(1f)
                                                                    .start();
                                                                break;
                                                        }

                                                        return false;
                                                    }
                                                });
                                            tile.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View view) {                                         
                                                        launchAppWithWindowsEffect(tile,
                                                                                   apps.get(i).activityInfo.packageName);
                                                    }
                                                });
                                        }

                                        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                                @Override
                                                public boolean onQueryTextSubmit(String query) { return false; }

                                                @Override
                                                public boolean onQueryTextChange(String newText) {
                                                    filterGrid(tileGrid, newText);
                                                    return true;
                                                }
                                            });

                                    }
                                }
                            }
                        });
                }
            }).start();
    } 

    private void launchAppWithWindowsEffect(final View clickedTile, final String packageName) {
        // 1. Configure the 3D perspective animation (Tilt from 0 to -25 degrees)
        WindowsPerspectiveAnimation tiltAnim = new WindowsPerspectiveAnimation(0f, -25f);
        tiltAnim.setDuration(200); // Quick Windows 8 snap response (200ms)
        tiltAnim.setFillAfter(true); // Hold the frame state
        tiltAnim.setInterpolator(new AccelerateInterpolator());

        // 2. Start animating the local launcher view element
        clickedTile.startAnimation(tiltAnim);

        // 3. Delay the system intent launch until the custom tile tilt finishes
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        // Standard platform flag ensures seamless task generation
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Native ActivityOptions fallback for older platforms 
                        // Android 13 will use its native zoom here, combined with your custom tile behavior
                        Bundle opts = ActivityOptions.makeBasic().toBundle();
                        startActivity(launchIntent, opts);
                    }

                    // Optional: Clear the tile animation so it resets when returning home
                    clickedTile.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clickedTile.clearAnimation();
                            }
                        }, 200);
                }
            }, 200); // Must precisely match the tiltAnim duration
    }


    public void filterGrid(GridLayout gridLayout, String query) {
        // 1. Convert query to lowercase for case-insensitive matching
        String lowerCaseQuery = query.toLowerCase().trim();

        // 2. Loop backwards to prevent index shifting bugs during layout recalculations
        for (int i = gridLayout.getChildCount() - 1; i >= 0; i--) {
            View child = gridLayout.getChildAt(i);

            // 3. Find the target view holding your filterable text
            // Replace R.id.item_text with the actual ID inside your cell layout
            TextView textView = child.findViewById(R.id.appTitle); 

            if (textView != null) {
                String itemText = textView.getText().toString().toLowerCase();

                // 4. Toggle visibility: GONE completely removes it from the layout flow
                if (itemText.contains(lowerCaseQuery)) {
                    child.setVisibility(View.VISIBLE);
                } else {
                    child.setVisibility(View.GONE); 
                }
            }
        }
    }

    /*
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int orientation =
            getResources().getConfiguration().orientation;

        if (orientation ==
            Configuration.ORIENTATION_LANDSCAPE) {
            tileGrid.setColumnCount(ccLandscape);
            tileGrid.removeAllViews();
            populateTiles();

        } else {
            tileGrid.setColumnCount(ccPortrait);
            tileGrid.removeAllViews();
            populateTiles();
        }
        super.onConfigurationChanged(newConfig);
    }
    */
}
