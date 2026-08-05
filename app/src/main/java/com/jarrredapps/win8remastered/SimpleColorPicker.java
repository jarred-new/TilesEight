package com.jarrredapps.win8remastered;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

public class SimpleColorPicker extends View {
    private int color = 0xFF000000;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private OnColorChangedListener listener;

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    public SimpleColorPicker(Context context) {
        super(context);
        init();
    }

    public SimpleColorPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setClickable(true);
        paint.setStyle(Paint.Style.FILL);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minw = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int w = resolveSize(minw, widthMeasureSpec);
        int h = resolveSize(100 + getPaddingTop() + getPaddingBottom(), heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(color);
        canvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), paint);
    }

    private void openDialog() {
        final View dlgView = LayoutInflater.from(getContext()).inflate(R.layout.simple_color_picker, null);
        final SeekBar seekR = dlgView.findViewById(R.id.seekR);
        final SeekBar seekG = dlgView.findViewById(R.id.seekG);
        final SeekBar seekB = dlgView.findViewById(R.id.seekB);
        final View preview = dlgView.findViewById(R.id.colorPreview);
        final EditText hexInput = dlgView.findViewById(R.id.hexInput);
        final Button btnApplyHex = dlgView.findViewById(R.id.btnApplyHex);

        int initR = Color.red(color);
        int initG = Color.green(color);
        int initB = Color.blue(color);

        seekR.setProgress(initR);
        seekG.setProgress(initG);
        seekB.setProgress(initB);
        preview.setBackgroundColor(color);
        hexInput.setText(String.format("#%06X", (0xFFFFFF & color)));

        final SeekBar.OnSeekBarChangeListener skl = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int r = seekR.getProgress();
                int g = seekG.getProgress();
                int b = seekB.getProgress();
                int tmp = Color.argb(255, r, g, b);
                preview.setBackgroundColor(tmp);
                hexInput.setText(String.format("#%06X", (0xFFFFFF & tmp)));
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekR.setOnSeekBarChangeListener(skl);
        seekG.setOnSeekBarChangeListener(skl);
        seekB.setOnSeekBarChangeListener(skl);

        btnApplyHex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt = hexInput.getText().toString().trim();
                if (txt.startsWith("#")) txt = txt.substring(1);
                if (txt.length() == 6) {
                    try {
                        int parsed = (int) Long.parseLong(txt, 16);
                        int tmp = 0xFF000000 | parsed;
                        seekR.setProgress(Color.red(tmp));
                        seekG.setProgress(Color.green(tmp));
                        seekB.setProgress(Color.blue(tmp));
                        preview.setBackgroundColor(tmp);
                    } catch (NumberFormatException ignored) {}
                }
            }
        });

        AlertDialog.Builder b = new AlertDialog.Builder(getContext());
        b.setTitle("Pick color");
        b.setView(dlgView);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int r = seekR.getProgress();
                int g = seekG.getProgress();
                int b = seekB.getProgress();
                color = Color.argb(255, r, g, b);
                invalidate();
                if (listener != null) listener.onColorChanged(color);
            }
        });
        b.setNegativeButton("Cancel", null);
        b.show();
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.listener = l;
    }

    public void setColor(int color) {
        this.color = color | 0xFF000000;
        invalidate();
    }

    public int getColor() {
        return color;
    }
}
