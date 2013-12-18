
package com.typhoon.xcommand;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;

public class XActivity extends Activity {

    static final String TAG = "zhangcheng";
    private CameraPreview preview;
    private TextView takePictureButton;
    Camera camera;
    private ToneGenerator tone;
    private static final int OPTION_SNAPSHOT = 0; // MENU项的值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_camera_android);
        setContentView(R.layout.activity_x);
        preview = (CameraPreview) findViewById(R.id.cameraView); // 自定义的view
        preview.init(camera, jpegCallback, autoFocusCallBack);
        // setContentView(preview);

        takePictureButton = (TextView) findViewById(R.id.takePicture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                preview.takePicture();
            }
        });

        takePictureButton.setClickable(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LayoutParams lp = preview.getLayoutParams();
        lp.width = 50;
        lp.height = 50;
        preview.setLayoutParams(lp);
    }

    public boolean onOptionsItemSelected(MenuItem item) { // activity的选项选择函数
        Log.i(TAG, "onOptionsItemSelected");
        int itemId = item.getItemId(); // 获得自定义ID
        switch (itemId) {
            case OPTION_SNAPSHOT:
                // 拍摄照片
                preview.takePicture();
                break;
        }
        return true;
    }

    PictureCallback jpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.i(TAG, "jpegCallback");
            Parameters ps = camera.getParameters();
            if (ps.getPictureFormat() == PixelFormat.JPEG) {
                // 存储拍照获得的图片
                String path = save(data);
                // 将图片交给Image程序处理
                Uri uri = Uri.fromFile(new File(path));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setDataAndType(uri, "image/jpeg");
                startActivity(intent); // 显示刚才拍的照片
            }
        }
    };

    private ShutterCallback shutterCallback = new ShutterCallback() {

        @Override
        public void onShutter() {
            Log.i(TAG, "shutterCallback");
            if (tone == null)
                // 发出提示用户的声音
                tone = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
            tone.startTone(ToneGenerator.TONE_PROP_BEEP2);
        }
    };

    private AutoFocusCallback autoFocusCallBack = new AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
//            camera.takePicture(null, null, jpegCallback); // 拍照动作
            takePictureButton.setClickable(true);
        }
    };

    private String save(byte[] data) { // 保存jpg到SD卡中
        String path = "/sdcard/" + System.currentTimeMillis() + ".jpg";
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                // 判断SD卡上是否有足够的空间
                String storage = Environment.getExternalStorageDirectory().toString();
                StatFs fs = new StatFs(storage);
                long available = fs.getAvailableBlocks() * fs.getBlockSize();
                if (available < data.length) {
                    // 空间不足直接返回空
                    return null;
                }
                File file = new File(path);
                if (!file.exists())
                    // 创建文件
                    file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return path;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.camera_android, menu);
        menu.add(0, OPTION_SNAPSHOT, 0, "aaaaa"); // 添加自定义meunitme项目
        return super.onCreateOptionsMenu(menu);
    }
}
