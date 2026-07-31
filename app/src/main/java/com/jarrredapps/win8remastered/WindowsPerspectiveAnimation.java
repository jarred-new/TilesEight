package com.jarrredapps.win8remastered;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class WindowsPerspectiveAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private float mCenterX;
    private float mCenterY;
    private Camera mCamera;

    public WindowsPerspectiveAnimation(float fromDegrees, float toDegrees) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
        // Set the pivot to the absolute center of the tile view
        mCenterX = width / 2.0f;
        mCenterY = height / 2.0f;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final Matrix matrix = t.getMatrix();

        mCamera.save();
        // Windows 8 tilts slightly backward on the X-axis and rotates on the Y-axis
        mCamera.rotateX(degrees * 0.4f); 
        mCamera.rotateY(degrees);       
        mCamera.getMatrix(matrix);
        mCamera.restore();

        // Adjust the matrix math so it rotates around the view's center point
        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);

        // Add a simultaneous Windows-style shrink/scale down effect
        float scale = 1.0f - (0.15f * interpolatedTime); // Shrinks to 85% size
        matrix.postScale(scale, scale, mCenterX, mCenterY);
    }
}

