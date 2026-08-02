package com.jarrredapps.win8remastered;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
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
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;
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
    private View scrollView;
    //private OrientationEventListener orientationEventListener;
    //private int currentRotation = 0;

    private static List<ResolveInfo> cachedApps;
    private Intent launch;
    private ActivityOptions options;

    private int WALLPAPER_ACCESS_CODE = 200;

    boolean compatibilityCheck;

    Typeface segoer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeViews();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        initializeViews();
    }

    private void initializeViews() {
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT <= 29) {
            compatibilityCheck = true;
        } else {
            compatibilityCheck = false;
        }

        wallpaperView = findViewById(R.id.wallpaperView);
        title = findViewById(R.id.title);
        searchBar = findViewById(R.id.searchBar);
        scrollView = findViewById(R.id.scrollView);
        tileGrid = findViewById(R.id.tileGrid);

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
                    .setMessage("Please grant the permission to access the wallpaper and other files. After that, click ok to restart the app to set the app's wallpaper to the system wallpaper")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Restart the App
                            Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                            if (intent != null) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        }
                    })
                    .create();
                dialog.show();

                requestPermissions(new String[]{
                                       Manifest.permission.READ_EXTERNAL_STORAGE,
                                       Manifest.permission.MANAGE_EXTERNAL_STORAGE
                                   },
                                   WALLPAPER_ACCESS_CODE);
            }
        }

        if (LauncherUtils.isNotDefaultLauncher(this)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("You've not set TilesEight as a default launcher")
                .setMessage("Please set TilesEight as a default launcher to make it better...")
                .setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        // Create intent to open the Home settings page directly
                        Intent intent = new Intent(Settings.ACTION_HOME_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        // Check if there is an app that can handle this intent before launching
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            // Fallback to global settings if ACTION_HOME_SETTINGS is not supported
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    }
                })
                .setNeutralButton("Quit App", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dia, int which) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
            dialog.show();
        }

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            tileGrid.setColumnCount(ccLandscape);
            if (scrollView instanceof HorizontalScrollView) {
                ((HorizontalScrollView) scrollView).setHorizontalScrollBarEnabled(false);
                ((HorizontalScrollView) scrollView).setVerticalScrollBarEnabled(false);
            }
        } else {
            tileGrid.setColumnCount(ccPortrait);
            if (scrollView instanceof ScrollView) {
                ((ScrollView) scrollView).setHorizontalScrollBarEnabled(false);
                ((ScrollView) scrollView).setVerticalScrollBarEnabled(false);
            }
        }

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        wallpaperView.setImageDrawable(wallpaperDrawable);

        populateTiles();

        /*
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

         orientationEventListener.enable(); */
    }

    @Override
    protected void onStart() {
        super.onStart();
        //orientationEventListener.enable();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //orientationEventListener.enable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //orientationEventListener.disable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //orientationEventListener.disable();
    }

    public void populateTiles() {     
//        Intent intent = new Intent(Intent.ACTION_MAIN, null);
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//
//        final PackageManager pm = getPackageManager();
//
//        final List<ResolveInfo> apps =
//            pm.queryIntentActivities(intent, 0);

        final PackageManager pm = getPackageManager();

        if (cachedApps == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            cachedApps = pm.queryIntentActivities(intent, 0);
        }

        String launcherPackage = getPackageName();
        final List<ResolveInfo> apps = new ArrayList<>(cachedApps.size());
        for (ResolveInfo app : cachedApps) {
            if (!app.activityInfo.packageName.equals(launcherPackage)) {
                apps.add(app);
            }
        }

        final int orientation = getResources().getConfiguration().orientation;
        final int columns;
        final int rows;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            int screenDpHeight = getResources().getConfiguration().screenHeightDp;
            rows = screenDpHeight >= 900 ? 3 : 2;
            columns = Math.max(1, (int) Math.ceil(apps.size() / (double) rows));
            tileGrid.setRowCount(rows);
            tileGrid.setColumnCount(columns);
        } else {
            columns = ccPortrait;
            rows = Math.max(1, (int) Math.ceil(apps.size() / (double) columns));
            tileGrid.setColumnCount(columns);
            tileGrid.setRowCount(rows);
        }

        tileGrid.removeAllViews();

        // compute responsive tile size so portrait exactly fits available width
        final int tileSizePx;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // For landscape base tile size on available height and row count, cap by default dimen
            int screenHeight = getResources().getDisplayMetrics().heightPixels;
            int maxTile = getResources().getDimensionPixelSize(R.dimen.tile_size);
            // leave some space for paddings; use rows+1.5 to avoid touching edges
            tileSizePx = Math.max(64, Math.min(maxTile, screenHeight / (rows + 1)));
        } else {
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.shell_padding_horizontal) * 2;
            int marginTotal = getResources().getDimensionPixelSize(R.dimen.tile_margin) * (columns + 1);
            int available = screenWidth - horizontalPadding - marginTotal;
            int computed = Math.max(64, available / Math.max(1, columns));
            int maxTile = getResources().getDimensionPixelSize(R.dimen.tile_size);
            tileSizePx = Math.min(maxTile, computed);
        }

        new Thread(new Runnable(){
                @Override
                public void run() {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                            @Override
                            public void run() {
                                for (int index = 0; index < apps.size(); index++) {
                                    final ResolveInfo app = apps.get(index);

                                    final View tileView = getLayoutInflater()
                                        .inflate(R.layout.tile, tileGrid, false);

                                    final ImageView icon =
                                        tileView.findViewById(R.id.icon);

                                    final TextView appTitle =
                                        tileView.findViewById(R.id.appTitle);

                                    icon.setImageDrawable(app.loadIcon(pm));
                                    appTitle.setTypeface(segoer);
                                    appTitle.setText(app.loadLabel(pm));

                                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                                        params.rowSpec = GridLayout.spec(index % rows, 1);
                                        params.columnSpec = GridLayout.spec(index / rows, 1);
                                    } else {
                                        params.rowSpec = GridLayout.spec(index / columns, 1);
                                        params.columnSpec = GridLayout.spec(index % columns, 1);
                                    }
                                    int tileSize = tileSizePx;
                                    params.width = tileSize;
                                    params.height = tileSize;
                                    params.setMargins(
                                        getResources().getDimensionPixelSize(R.dimen.tile_margin),
                                        getResources().getDimensionPixelSize(R.dimen.tile_margin),
                                        getResources().getDimensionPixelSize(R.dimen.tile_margin),
                                        getResources().getDimensionPixelSize(R.dimen.tile_margin)
                                    );
                                    tileView.setLayoutParams(params);
                                    tileGrid.addView(tileView);

                                    final int position = index;
                                    if (compatibilityCheck) {
                                        launch =
                                            pm.getLaunchIntentForPackage(
                                            apps.get(position).activityInfo.packageName);

                                        options = ActivityOptions.makeCustomAnimation(
                                            MainActivity.this,
                                            R.anim.pers_enter,
                                            R.anim.pers_exit
                                        );

                                        tileView.setOnClickListener(new OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    startActivity(launch, options.toBundle());
                                                }
                                            });
                                    } else {
                                        tileView.setOnTouchListener(new View.OnTouchListener() {
                                                @Override
                                                public boolean onTouch(View v, MotionEvent event) {
                                                    switch (event.getAction()) {
                                                        case MotionEvent.ACTION_DOWN:
                                                            v.animate()
                                                                .setDuration(200)
                                                                .setInterpolator(new AccelerateInterpolator())
                                                                .scaleX(0.8f)
                                                                .scaleY(0.8f)
                                                                .start();
                                                            break;

                                                        case MotionEvent.ACTION_UP:
                                                        case MotionEvent.ACTION_CANCEL:
                                                            v.animate()
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
                                        tileView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    launchAppWithWindowsEffect(tileView,
                                                                               apps.get(position).activityInfo.packageName);
                                                }
                                            });
                                    }
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
                        });
                }
            }).start();
    } 

    private void launchAppWithWindowsEffect(final View clickedTile, final String packageName) {
        WindowsPerspectiveAnimation tiltAnim = new WindowsPerspectiveAnimation(0f, -22f);
        tiltAnim.setDuration(180);
        tiltAnim.setFillAfter(true);
        tiltAnim.setInterpolator(new DecelerateInterpolator());

        clickedTile.animate()
            .setDuration(180)
            .setInterpolator(new DecelerateInterpolator())
            .scaleX(0.92f)
            .scaleY(0.92f)
            .alpha(0.9f)
            .start();

        clickedTile.startAnimation(tiltAnim);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
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

                    clickedTile.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                clickedTile.clearAnimation();
                                clickedTile.animate()
                                    .setDuration(140)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .alpha(1f)
                                    .start();
                            }
                        }, 180);
                }
            }, 180);
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

    /*@Override
     public void onConfigurationChanged(Configuration newConfig) {
     super.onConfigurationChanged(newConfig);

     int orientation =
     newConfig.orientation;

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

     }*/

}
