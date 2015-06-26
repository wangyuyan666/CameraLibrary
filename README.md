# CameraLibrary


##Usage

```

<com.objectlife.cameralibrary.camera.CameraPreview
        android:id="@+id/camera_preview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
        
        
        
        
        
cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
camera = CameraManager.getCameraInstance(this);
cameraPreview.setCamera(camera);


@Override
    protected void onResume() {
        super.onResume();
        CameraManager.startPreview();
    }        
        
```




