package com.example.krector.wifip2p;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by krector on 4/15/2017.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context context;
    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;
    private SurfaceView mSurfaceView;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        this.context = context;
        mCamera = camera;
        mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            mCamera.setParameters(params);
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
            mCamera.startPreview();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public Camera.Size getPreviewSize()
    {
        return mPreviewSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            Camera.Parameters params = mCamera.getParameters();
            Camera.Size previewSize = getPreviewSize();
            params.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setParameters(params);

            mCamera.setPreviewCallback((PreviewCallback) context);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height)
    {
        Camera.Size optimalSize = null;

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Camera.Size size : sizes)
        {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE)
            {
                optimalSize = size;
            }
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null)
        {
            // TODO : Backup in case we don't get a size.
        }

        return optimalSize;
    }
}

