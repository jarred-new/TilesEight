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

/*
package com.example.myapp;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

public class CustomDialogPreference extends DialogPreference {

    private EditText myEditText;
    private String mValue;

    public CustomDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        // Bind the UI element from dialog_custom.xml
        myEditText = (EditText) view.findViewById(R.id.dialog_edittext);

        // Populate with previously saved data if it exists
        mValue = getPersistedString("");
        if (myEditText != null && mValue != null) {
            myEditText.setText(mValue);
        }
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        // Triggered when user exits dialog. 'positiveResult' is true if they hit Save
        if (positiveResult && myEditText != null) {
            String enteredText = myEditText.getText().toString();

            // Check with rules to see if it should persist, then save
            if (callChangeListener(enteredText)) {
                mValue = enteredText;
                persistString(enteredText);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(android.content.res.TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            mValue = getPersistedString("");
        } else {
            // Set default state from XML
            mValue = (String) defaultValue;
            persistString(mValue);
        }
    }
}
*/

