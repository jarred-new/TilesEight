package com.jarrredapps.win8remastered;

import android.content.Context;
import android.util.AttributeSet;
import android.preference.DialogPreference;
import android.view.View;
import android.content.res.TypedArray;
import android.widget.SeekBar;


public class TileSizeDialog extends DialogPreference {

    private SeekBar portraitSeek;
    private SeekBar landscapeSeek;

    private int portraitInt;
    private int landscapeInt;

    public TileSizeDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        portraitSeek = view.findViewById(R.id.portraitSeek);
        landscapeSeek = view.findViewById(R.id.landscapeSeek);

        portraitInt = getPersistedInt(0);
        landscapeInt = getPersistedInt(0);

        if (portraitSeek != null
            && landscapeSeek != null) {
            // variables were flipped because the seekbars' progress values are flipped.
            portraitSeek.setProgress(landscapeInt);
            landscapeSeek.setProgress(portraitInt);
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

            if (callChangeListener(selectedPt)) {
                portraitInt = selectedPt;
                persistInt(selectedPt);
            }
            if (callChangeListener(selectedLs)) {
                landscapeInt = selectedLs;
                persistInt(selectedLs);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getIndex(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        //super.onSetInitialValue(restorePersistedValue, defaultValue);
        if (restorePersistedValue) {
            portraitInt = getPersistedInt(0);
            landscapeInt = getPersistedInt(0);
        } else {
            portraitInt = (int) defaultValue;
            landscapeInt = (int) defaultValue;

            persistInt(portraitInt);
            persistInt(landscapeInt);
        }
    }
}
