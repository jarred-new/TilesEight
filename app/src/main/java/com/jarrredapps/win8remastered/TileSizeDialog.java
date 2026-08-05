package com.jarrredapps.win8remastered;

import android.content.Context;
import android.util.AttributeSet;
import android.preference.DialogPreference;
import android.view.View;
import android.content.res.TypedArray;
import android.content.SharedPreferences;
import android.widget.SeekBar;


public class TileSizeDialog extends DialogPreference {

    private SeekBar portraitSeek;
    private SeekBar landscapeSeek;

    private int portraitInt;
    private int landscapeInt;

    private static final String SUFFIX_PORTRAIT = "_portrait";
    private static final String SUFFIX_LANDSCAPE = "_landscape";

    public TileSizeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        portraitSeek = view.findViewById(R.id.portraitSeek);
        landscapeSeek = view.findViewById(R.id.landscapeSeek);

        SharedPreferences prefs = getSharedPreferences();
        String pKey = getKey() + SUFFIX_PORTRAIT;
        String lKey = getKey() + SUFFIX_LANDSCAPE;
        portraitInt = prefs.getInt(pKey, 0);
        landscapeInt = prefs.getInt(lKey, 0);

        if (portraitSeek != null && landscapeSeek != null) {
            portraitSeek.setProgress(portraitInt);
            landscapeSeek.setProgress(landscapeInt);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult 
            && portraitSeek != null
            && landscapeSeek != null) {
            int selectedPt = portraitSeek.getProgress();
            int selectedLs = landscapeSeek.getProgress();
            SharedPreferences prefs = getSharedPreferences();
            SharedPreferences.Editor e = prefs.edit();
            String pKey = getKey() + SUFFIX_PORTRAIT;
            String lKey = getKey() + SUFFIX_LANDSCAPE;

            if (callChangeListener(selectedPt)) {
                portraitInt = selectedPt;
                e.putInt(pKey, selectedPt);
            }
            if (callChangeListener(selectedLs)) {
                landscapeInt = selectedLs;
                e.putInt(lKey, selectedLs);
            }
            e.apply();
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //super.onSetInitialValue(restorePersistedValue, defaultValue);
        SharedPreferences prefs = getSharedPreferences();
        String pKey = getKey() + SUFFIX_PORTRAIT;
        String lKey = getKey() + SUFFIX_LANDSCAPE;

        if (restorePersistedValue) {
            portraitInt = prefs.getInt(pKey, 0);
            landscapeInt = prefs.getInt(lKey, 0);
        } else {
            int def = 0;
            if (defaultValue instanceof Integer) {
                def = (Integer) defaultValue;
            } else if (defaultValue instanceof String) {
                try { def = Integer.parseInt((String) defaultValue); } catch (NumberFormatException ignored) {}
            }
            portraitInt = def;
            landscapeInt = def;

            SharedPreferences.Editor e = prefs.edit();
            e.putInt(pKey, portraitInt);
            e.putInt(lKey, landscapeInt);
            e.apply();
        }
    }
}
