package com.jarrredapps.win8remastered;

import android.content.Context;
import android.util.AttributeSet;
import android.preference.DialogPreference;
import android.view.View;
import android.content.res.TypedArray;
import android.widget.SeekBar;


public class TileColorDialog extends DialogPreference {

    private SimpleColorPicker simpleColorPicker;
    private int tileColor;

    public TileColorDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        simpleColorPicker = view.findViewById(R.id.simpleColorPicker);
        tileColor = getPersistedInt(0);
        
        if (simpleColorPicker != null) {
            simpleColorPicker.setColor(tileColor);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult 
            && simpleColorPicker != null) {
            int selectedColor = simpleColorPicker.getColor();

            if (callChangeListener(selectedColor)) {
                persistInt(selectedColor);
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
            tileColor = getPersistedInt(0);
        } else {
            tileColor = (int) defaultValue;
            persistInt(tileColor);
        }
    }
}
