package com.objectlife.cameralibrary.camera;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by objectlife.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;

    /**
     * 在部局文件中使用得到CameraPreview
     * @param context
     * @param attrs
     */
    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * 使用构造函数。直接New得到CameraPreview
     * @param context
     */
    public CameraPreview(Context context) {
        super(context);
    }


    /**
     * 设置camera
     * @param camera
     */
    public void setCamera(Camera camera){
        mCamera = camera;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) {
        Log.e(TAG,"------->surfaceCreated<-------");
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e(TAG,"------->surfaceDestroyed<-------");
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        Log.e(TAG,"------->surfaceChanged<-------");
        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }
        // set preview size and make any resize, rotate or reformatting changes here
        CameraUtil.initPreview(mCamera, w, h);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CameraManager.requestFocus(mCamera,CameraManager.isPreviewing());
        return super.onTouchEvent(event);
    }
}

