package com.typhoon.xcommand;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback { // 完成自定义的CameraPreview
    private static final String TAG = "CameraPreview";
    private SurfaceHolder mHolder;
    private Camera camera;
    private PictureCallback jpegCallback;
    private AutoFocusCallback autoFocusCallback;

    public CameraPreview(Context context) { // view必有带Context的构造函数
        super(context);

        // camera = Camera.open();
        // try {
        // // 设置显示
        // camera.setPreviewDisplay(mHolder);
        // } catch (IOException exception) {
        // camera.release();
        // camera = null;
        // }
        // camera.startPreview();
    }


    public CameraPreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CameraPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Camera camera, PictureCallback jpegCallback,
            AutoFocusCallback autoFocusCallback) {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.camera = camera;
        this.jpegCallback = jpegCallback;
        this.autoFocusCallback = autoFocusCallback;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");
        // 已经获得Surface的width和height，设置Camera的参数

        Camera.Parameters parameters = this.camera.getParameters();
        Size previewSize = parameters.getSupportedPreviewSizes().get(0);
        Size pictureSize = parameters.getSupportedPictureSizes().get(
                parameters.getSupportedPictureSizes().size() - 1);

        parameters.setPreviewSize(previewSize.width, previewSize.height);
        parameters.setPictureSize(pictureSize.width, pictureSize.height);
        parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
        // parameters.setPreviewSize(width, height);
        this.camera.setParameters(parameters); // 设置完效果后必须有这个
        // 开始预览
        this.camera.startPreview();
        this.camera.autoFocus(autoFocusCallback);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        // TODO Auto-generated method stub
        this.camera = Camera.open();
        try {
            // 设置显示
            this.camera.setPreviewDisplay(holder);
        } catch (IOException exception) {
            this.camera.release();
            this.camera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed");
        // TODO Auto-generated method stub
        this.camera.stopPreview();
        // 释放Camera
        this.camera.release();
        this.camera = null;
    }

    public void takePicture() {
        camera.takePicture(null, null, jpegCallback); // 拍照动作
    }
}