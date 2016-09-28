package com.example.micha.myfirstcamera2;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity {
    private static int mOpenCameraId;
    private Camera mCamera;
    private CameraPreview mPreview;
    private static final int STD_DELAY = 400;
    protected static final String TAG = "CameraActivity";

    /**Starts up the camera */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Log", "onCreate");
        setContentView(R.layout.activity_camera);

        if (this.mCamera == null){
            this.mCamera = getCameraInstance();}

        if(this.mCamera == null){
            Toast toast = Toast.makeText(getApplicationContext(), "No camera", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            //camera works, now do stuff with it
//            mCamera.setDisplayOrientation(180);
            setCameraDisplayOrientation(this, mOpenCameraId, mCamera);
            this.mPreview = new CameraPreview(this, this.mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(this.mPreview);
        }

    }

    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
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


    /** A safe way to get an instance of the Camera object. Code collected from elsewhere */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            // attempt to get a Camera instance for rear camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for(int i = 0; i < numberOfCameras; i++){
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if(info.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                    System.out.println("Found camera: " + info.facing);
                    c = Camera.open(info.facing);
                    mOpenCameraId = info.facing;
                }
            }
            if(c == null){
                c = Camera.open();
            }
        }
        catch (Exception e){
            // camera is not available (in use or does not exist)
            System.err.println("Error message: " + e.getMessage());
        }
        // returns null if camera is unavailable
        return c;
    }

    /**Generates a delay needed for application to save new pictures */
    private void delay(){
        try {
            //Makes the program inactive for a specific amout of time
            Thread.sleep(STD_DELAY);
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    /**Method for releasing the camera immediately on pause event*/
    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Log", "onPause");
        //Shuts down the preview shown on the screen
        if(this.mCamera != null) {
            this.mCamera.stopPreview();
        }
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.removeAllViews();
        //Calls an internal help method to restore the camera
        releaseCamera();
    }


    /**Help method to release the camera */
    private void releaseCamera(){
        //Checks if there is a camera object active
        if (this.mCamera != null){
            //Releases the camera
            this.mCamera.release();
            //Restore the camera object to its initial state
            this.mCamera = null;
        }
    }

    /**Activates the camera and makes it appear on the screen */
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Log", "onResume");
        // TODO Auto-generated method stub
        // Create an instance of Camera.
        if (this.mCamera == null){
            this.mCamera = getCameraInstance();}

        if(this.mCamera == null){
            Toast toast = Toast.makeText(getApplicationContext(), "No camera", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            this.mPreview = new CameraPreview(this, this.mCamera);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.removeAllViews();
            preview.addView(this.mPreview);
        }

        super.onResume();
    }



}
