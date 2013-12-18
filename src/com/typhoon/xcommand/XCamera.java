
package com.typhoon.xcommand;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class XCamera {

    private static final String TAG = "MyCamera";
    public static final String CAMERA_SAVE_PATH = Environment.getExternalStorageDirectory()
            .toString() + "/Command/.images/";

    private Camera mBackCamera;
    private Camera mFrontCamera;

    public XCamera() {
//        mBackCamera = openCamera(true);
        mBackCamera = openCamera();
//        mFrontCamera = openCamera(false);
    }

    private static Camera openCamera(boolean backCamera) {
        int numberOfCameras = Camera.getNumberOfCameras();
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (backCamera && cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                return Camera.open(i);
            } else if (!backCamera && cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT) {
                return Camera.open(i);
            }
        }
        return null;
    }

    private static Camera openCamera() {
        return Camera.open();
    }

    public void release(boolean backCamera)
    {
        if (backCamera) {
            mBackCamera.release();
        } else {
            mFrontCamera.release();
        }
    }

    /** Handles data for jpeg picture */
    private PictureCallback mJpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try
            {
                File imageFolder = new File(CAMERA_SAVE_PATH);
                if (!imageFolder.exists()) {
                    imageFolder.mkdirs();
                }
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                String filename;
                Date date = new Date(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                filename = sdf.format(date);
                OutputStream fOut = null;
                File file = new File(imageFolder, "/" + filename + ".jpg");
                fOut = new FileOutputStream(file);
                bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 0, fOut);
                fOut.flush();
                fOut.close();

                Log.d(TAG, "onPictureTaken - jpeg:" + filename);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public void takePicture(boolean back)
    {
        if (back && mBackCamera != null) {
            mBackCamera.takePicture(shutterCallback, rawCallback, mJpegCallback);
        } else if (!back && mFrontCamera != null) {
            mFrontCamera.takePicture(shutterCallback, rawCallback, mJpegCallback);
        }
    }

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };


//    public void startPreview(int width, int height)
//    {
//        if(m_camera != null)
//        {
//            Camera.Parameters parameters = m_camera.getParameters();
//            Display display = m_windowManager.getDefaultDisplay();
//
//            if(display.getRotation() == Surface.ROTATION_0)
//            {
//                parameters.setPreviewSize(height, width);
//                m_camera.setDisplayOrientation(90);
//            }
//
//            if(display.getRotation() == Surface.ROTATION_90)
//            {
//                parameters.setPreviewSize(width, height);
//            }
//
//            if(display.getRotation() == Surface.ROTATION_180)
//            {
//                parameters.setPreviewSize(height, width);
//            }
//
//            if(display.getRotation() == Surface.ROTATION_270)
//            {
//                parameters.setPreviewSize(width, height);
//                m_camera.setDisplayOrientation(180);
//            }
//
//            //parameters.setPreviewSize(width, height);
//            m_camera.setParameters(parameters);
//            m_camera.startPreview();
//        }
//    }
//
//    public void stopPreview()
//    {
//        if(m_camera != null)
//        {
//            m_camera.stopPreview();
//        }
//    }

}
