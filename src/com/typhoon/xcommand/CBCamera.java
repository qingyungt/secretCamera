
package com.typhoon.xcommand;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;

public class CBCamera {
    static CBCamera m_instance;
    static final String TAG = "CameraDemo";

    Camera m_camera;
    WindowManager m_windowManager;
    ContentResolver m_contentResolver;

    public static CBCamera instance()
    {
        if (m_instance == null)
        {
            m_instance = new CBCamera();
        }
        return m_instance;
    }

    private CBCamera()
    {
        open();
    }

    public void setWindowManager(WindowManager windowManager)
    {
        m_windowManager = windowManager;
    }

    public void setContentResolver(ContentResolver contentResolver)
    {
        m_contentResolver = contentResolver;
    }

    public void initialPreview(SurfaceHolder holder)
    {
        try
        {
            if (m_camera != null)
            {
                m_camera.setPreviewDisplay(holder);
            }
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void startPreview(int width, int height)
    {
        if (m_camera != null)
        {
//            Camera.Parameters parameters = m_camera.getParameters();
//            Size previewSize = parameters.getSupportedPreviewSizes().get(2);
//            Display display = m_windowManager.getDefaultDisplay();
//
//            if (display.getRotation() == Surface.ROTATION_0)
//            {
////                parameters.setPreviewSize(height, width);
//                m_camera.setDisplayOrientation(90);
//            }
//
//            if (display.getRotation() == Surface.ROTATION_90)
//            {
////                parameters.setPreviewSize(width, height);
//            }
//
//            if (display.getRotation() == Surface.ROTATION_180)
//            {
////                parameters.setPreviewSize(height, width);
//            }
//
//            if (display.getRotation() == Surface.ROTATION_270)
//            {
////                parameters.setPreviewSize(width, height);
//                m_camera.setDisplayOrientation(180);
//            }

//            parameters.setPreviewSize(previewSize.width, previewSize.height);
            // parameters.setPreviewSize(width, height);
//            m_camera.setParameters(parameters);
            m_camera.startPreview();
        }
    }

    public void stopPreview()
    {
        if (m_camera != null)
        {
            m_camera.stopPreview();
        }
    }

    public void takePicture()
    {
        if (m_camera != null)
            m_camera.takePicture(null, null, jpegCallback);
    }

    public void open()
    {
        if (m_camera == null)
            m_camera = Camera.open();
    }

    public void release()
    {
        if (m_camera != null)
        {
            m_camera.release();
            m_camera = null;
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

    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try
            {
                Bitmap bitmapPicture = BitmapFactory.decodeByteArray(data, 0, data.length);
                String path = Environment.getExternalStorageDirectory().toString();
                String filename;
                Date date = new Date(0);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                filename = sdf.format(date);
                OutputStream fOut = null;
                // File file = new File(path, "/DCIM/"+filename+".jpg");
                File file = new File(path, "/" + filename + ".jpg");
                fOut = new FileOutputStream(file);
                // FileOutputStream out = new
                // FileOutputStream(String.format("/sdcard/DCIM/Signatures/%d.jpg",
                // System.currentTimeMillis()));
                bitmapPicture.compress(Bitmap.CompressFormat.JPEG, 0, fOut);
                fOut.flush();
                fOut.close();

                MediaStore.Images.Media.insertImage(m_contentResolver, file.getAbsolutePath(),
                        file.getName(), file.getName());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            finally
            {
                camera.startPreview();
            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };
}
