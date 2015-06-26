package com.objectlife.cameralibrary.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by objectlife.
 */
public class CameraUtil {

    /** Check if this device has a camera
     *  2.3版本以后可以用 Camera.getNumberOfCameras() 来判断设备有几个摄像头。
     * */
    public static boolean checkCameraHardware(Context context) {

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


    /**
     * 判断是否支持闪光灯
     * @param context
     * @return
     */

    public static boolean checkFlashLight(Context context) {

        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }


    /**
     * 设置预览图像与手机屏幕方向相同
     * @param context
     * @param cameraId
     * @param camera
     */
    public static void setCameraDisplayOrientation(Context context, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    /**
     * 根据屏幕宽高设置最佳预览效果
     * @param camera
     * @param width
     * @param height
     */
    public static void initPreview(Camera camera, int width, int height){

        Camera.Parameters parameters=camera.getParameters();
        Camera.Size size=getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);

        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
            parameters.setPictureSize(size.width, size.height);
            parameters.setPictureFormat(ImageFormat.JPEG);
            camera.setParameters(parameters);
        }
    }

    /**
     * 获取最优预览效果
     * @param sizes
     * @param w
     * @param h
     * @return
     */
    private static Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }


}
