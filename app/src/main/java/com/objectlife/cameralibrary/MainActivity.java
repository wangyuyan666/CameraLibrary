package com.objectlife.cameralibrary;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.objectlife.cameralibrary.camera.CameraManager;
import com.objectlife.cameralibrary.camera.CameraPreview;
import com.objectlife.cameralibrary.camera.CameraUtil;
import com.objectlife.cameralibrary.camera.HandlePictureResult;
/**
 * Created by objectlife.
 */
public class MainActivity extends AppCompatActivity {

    CameraPreview cameraPreview;
    Camera camera;
    boolean flashing;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        if (CameraUtil.checkCameraHardware(this)){
            camera = CameraManager.getCameraInstance(this);
        }

        if (camera != null){
            cameraPreview.setCamera(camera);
        }

        findViewById(R.id.take_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraManager.takePicture(new HandlePictureResult() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        // handle picture data
                    }
                });
            }
        });

        findViewById(R.id.flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraUtil.checkFlashLight(MainActivity.this)){
                    if (flashing){
                        flashing = false;
                        CameraManager.closeFlash();
                    }else{
                        flashing = true;
                        CameraManager.openFlash();
                    }
                }
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        CameraManager.startPreview();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraManager.stopPreview();
        CameraManager.releaseCamera();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
