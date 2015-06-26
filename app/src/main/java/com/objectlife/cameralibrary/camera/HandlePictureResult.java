package com.objectlife.cameralibrary.camera;

import android.hardware.Camera;

/**
 * Created by objectlife.
 */
public interface HandlePictureResult {

    /**
     * 拍照成功
     * @param data
     * @param camera
     */
    void onPictureTaken(byte[] data, Camera camera);


}
