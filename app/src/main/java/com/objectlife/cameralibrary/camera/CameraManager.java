package com.objectlife.cameralibrary.camera;

import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;


/**
 * Created by objectlife.
 */
public class CameraManager {

    private static final int FOCUS_SUCCESS = 0;

    private static Camera mCamera;

    /** 是否开启预览 */
    private static boolean mPreviewing;

    /** 自动聚焦间隔 */
    private static long AUTOFOCUS_INTERVAL = 5*1000L;

    /** 拍照完成回调处理 */
    private static HandlePictureResult mHandlePictureResult;

    /** 自动聚焦线程 */
    private static FocusThread focusThread;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case FOCUS_SUCCESS:
                    autoFocusInThread();
                break;
            }
        }
    };


    /** A safe way to get an instance of the Camera object.
     * Android 2.3以后可以用Camera.open(int)打开指定摄像头
     * */
    public static Camera getCameraInstance(Context context){

        mCamera =  getCameraInstance(context,0);
        return mCamera;
    }

    /**
     * 打开指定摄像头 通过Camera.getNumberOfCameras()获取摄像头个数N。。cameraId取值范围：0~N-1
     * @param cameraId
     * @return
     */
    public static Camera getCameraInstance(Context context,int cameraId){

        try {
            mCamera = Camera.open(cameraId);
            CameraUtil.setCameraDisplayOrientation(context,cameraId,mCamera);
        }
        catch (Exception e){
        }
        return mCamera; // returns null if camera is unavailable
    }


    /**
     * 请求聚焦线程
     */
    static class FocusThread implements Runnable{

        @Override
        public void run() {
            requestFocus(mCamera,mPreviewing);
        }
    }

    /**
     * 聚焦回调
     */
    private static Camera.AutoFocusCallback mAutoFocusCallBack = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success)
                mHandler.sendEmptyMessage(FOCUS_SUCCESS);
        }
    };

    /**
     * 聚焦成功后进行拍照
     */
    private static Camera.AutoFocusCallback mAutoFocusCallBackTakePicture = new Camera.AutoFocusCallback(){

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success){
                mCamera.takePicture(null, null,new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        if (mHandlePictureResult != null)
                            mHandlePictureResult.onPictureTaken(data,camera);
                    }
                });
            }
        }
    };

    /**
     * 请求聚焦
     */
    public static void requestFocus(Camera camera, boolean previewing){

        if (camera != null && previewing){

            mHandler.removeMessages(FOCUS_SUCCESS);
            camera.autoFocus(mAutoFocusCallBack);

        }
    }


    /**
     * 请求聚焦并拍照
     */
    private static void requestFocusForTakePicture(){
        if (mCamera != null && mPreviewing){
            mCamera.autoFocus(mAutoFocusCallBackTakePicture);
        }
    }

    private static void autoFocusInThread(){

        if (focusThread == null){
            focusThread = new FocusThread();
        }

        mHandler.postDelayed(focusThread, AUTOFOCUS_INTERVAL);
    }

    public static boolean isPreviewing(){
        return mPreviewing;
    }

    /**
     * 拍照
     * @param handlePictureResult 拍照结果处理回调
     */
    public static void takePicture(HandlePictureResult handlePictureResult){
        mHandlePictureResult = handlePictureResult;
        requestFocusForTakePicture();
    }


    /**
     * 打开闪光灯
     */
    public static void openFlash(){
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);

    }

    /**
     * 关闭闪光灯
     */
    public static void closeFlash(){
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

//    /**
//     * 切换摄像头
//     * 注:调用前需通过Camera.getNumberOfCameras()获取摄像头个数N。。cameraId取值范围：0~N-1
//     * @param camera    当前摄像头对象
//     * @param preview   CameraPreview
//     * @param cameraId  要打开的摄像头id
//     */
//    public static void switchCamera(Camera camera, CameraPreview preview, int cameraId){
//
//
//    }

    /**
     * 打开预览
     */
    public static void startPreview(){

        if (mCamera != null && !mPreviewing){
            mCamera.startPreview();
            mPreviewing = true;

            autoFocusInThread();
        }
    }

    /**
     * 停止预览
     */
    public static void stopPreview(){

        if (mCamera != null && mPreviewing){
            mCamera.stopPreview();
            mPreviewing = false;
        }
    }

    /**
     * 释放摄像头资源
     */
    public static void releaseCamera(){
        if (mCamera != null){
            mCamera.release();
            mCamera = null;
        }
    }


}
