package com.jarrredapps.win8remastered;

import android.app.role.RoleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

public class LauncherUtils {

    /**
     * Checks if the current app is NOT set as the default home launcher.
     * 
     * @param context The application or activity context.
     * @return true if the app is NOT the default launcher; false if it is the default.
     */
    public static boolean isNotDefaultLauncher(Context context) {
        // Modern approach for Android 10 (API 29) and above using RoleManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            RoleManager roleManager = context.getSystemService(RoleManager.class);
            if (roleManager != null) {
                // Returns true if your app holds the HOME role (meaning it IS the default)
                boolean isDefault = roleManager.isRoleHeld(RoleManager.ROLE_HOME);
                return !isDefault; 
            }
        }

        // Legacy / Fallback approach using Intent resolution (Works on all versions)
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        PackageManager packageManager = context.getPackageManager();
        // MATCH_DEFAULT_ONLY ensures we get the app currently handling the intent by default
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (resolveInfo != null && resolveInfo.activityInfo != null) {
            String currentDefaultPackage = resolveInfo.activityInfo.packageName;
            String myPackageName = context.getPackageName();

            // If the current default launcher package does NOT equal your package, return true
            return !myPackageName.equals(currentDefaultPackage);
        }

        // Fallback if no default handler is found or if it's the Android system resolver (chooser)
        return true; 
    }

    /**
     * Opens the website using a browser
     * This will use the external browser.
     */
    public static void openLink(Context context, String url) {     
        // 1. Create the Intent with ACTION_VIEW and parse the URL string
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        // 2. Prevent crashes by checking if there is a browser app to handle the intent
        if (browserIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(browserIntent);
        } else {
            // Optional: Fallback if no browser is installed on the device
            Toast.makeText(context, "No web browser installed", Toast.LENGTH_LONG).show();
        }
    }
}
