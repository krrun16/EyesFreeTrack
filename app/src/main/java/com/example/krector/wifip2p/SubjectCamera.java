package com.example.krector.wifip2p;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

public class SubjectCamera extends AppCompatActivity implements Camera.PreviewCallback {
    protected static final int MY_PERMISSIONS_REQUEST_CAMERA = 23;
    protected static final int MY_PERMISSIONS_REQUEST_AUDIO = 29;
    protected static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 16;

    private static final String VIDEO_DIRECTORY_NAME = "subject_videos";

    private static Camera mCamera;
    private CameraPreview mPreview;
    protected static final File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    protected static Long tsLong = System.currentTimeMillis()/1000;
    protected static String ts = tsLong.toString();
    protected static File file = new File(dir, ts + "_study.txt");
    private static MediaRecorder mMediaRecorder;
    static boolean isRecording;
    private static AppCompatActivity cameraActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cameraActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_subject_camera);
        ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(SubjectCamera.this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                MY_PERMISSIONS_REQUEST_AUDIO);
        getCameraPermission();
        mCamera = getCameraInstance();
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }
    @Override
    protected void onStart(){
        super.onStart();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                prepareVideoRecorder();
                try {
                    mMediaRecorder.start();
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
                isRecording = true;
            }
        },100);
//        prepareVideoRecorder();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
//                writeToFile("hello world!");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_AUDIO: {
//                writeToFile("hello world!");
            }
        }
    }

    private void getCameraPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SubjectCamera.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SubjectCamera.this,
                    Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(SubjectCamera.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }
    public Camera getCameraInstance(){
        Camera c = null;
        getCameraPermission();

        try {
            c = Camera.open(1); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "EyesFreeTrack");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("EyesFreeTrack", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ ts + ".mp4");
        return mediaFile;
    }
    private boolean prepareVideoRecorder(){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
//        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile().toString());

        mMediaRecorder.setVideoFrameRate(20);

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(90);
        // Step 6: Prepare configured MediaRecorder
        SurfaceHolder temp = mPreview.getHolder();
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.stop();
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }
    public static void endRecording(){
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mCamera.lock();
        }
        isRecording = false;
        cameraActivity.finish();
    }
    public void onPreviewFrame(byte[] data, Camera camera) {

        //create a Bitmap from the byte array
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = parameters.getPreviewSize();
        YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                size.width, size.height, null);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        image.compressToJpeg(new Rect(0, 0, image.getWidth(), image.getHeight()), 25, out);

       /* byte [] rawImage = out.toByteArray();

        // This is the same image as the preview but in JPEG and not rotated
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length);
        ByteArrayOutputStream rotatedStream = new ByteArrayOutputStream();

        // Rotate the Bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(270);

        // We rotate the same Bitmap
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, size.width, size.height, matrix, false);

        // We dump the rotated Bitmap to the stream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, rotatedStream);

        rawImage = rotatedStream.toByteArray();*/

        //use your Bitmap
    }

}
