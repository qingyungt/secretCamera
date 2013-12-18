
package com.typhoon.xcommand;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;

//import com.android.emailcommon.provider.EmailContent.Message;
//
//import miui.content.ExtraIntent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class XCommand {

    private static final String TAG = "XCommand";

    public static final String COMMAND_PREFIX = "xxx_command:";


    private static final String COMMAND_WIPE_DATA = "wd";
    private static final String COMMAND_TAKE_PICTURE = "tp";
    private static final String COMMAND_TAKE_VIDEO = "tv";
    private static final String COMMAND_TAKE_SOUND = "ts";
    private static final String COMMAND_FIND_LOCATION = "fl";
    private static final String COMMAND_LOCK_DEVICE = "ld";

    private static final String DEFAULT_PASSWORD = "777777";

    public static boolean checkStringMessage(Context context, String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }

        if (!message.startsWith(COMMAND_PREFIX)) {
            return false;
        }

        String commandString = message.substring(COMMAND_PREFIX.length());
        String[] commands = commandString.split(",");

        for (String command : commands) {

            if (command.startsWith(COMMAND_WIPE_DATA)) {
//                wipeData(context);
                continue;
            }
            if (command.startsWith(COMMAND_TAKE_PICTURE)) {
                takePicture(context, true);
                continue;
            }
            if (command.startsWith(COMMAND_TAKE_VIDEO)) {
                videoMethod(context);
                continue;
            }
            if (command.startsWith(COMMAND_TAKE_SOUND)) {
                soundRecorderMethod(context);
                continue;
            }
            if (command.startsWith(COMMAND_FIND_LOCATION)) {
                getLocation(context);
                continue;
            }

            if (command.startsWith(COMMAND_LOCK_DEVICE)) {
                String password = command.substring(2);
                if (TextUtils.isEmpty(password)) {
                    password = DEFAULT_PASSWORD;
                }
//                lockDevice(context, password);
                continue;
            }
        }

        return true;
    }

//    public static boolean checkMail(Context context, Message message) {
//        String subject = message.mSubject;
//        return checkStringMessage(context, subject);
//    }

//    public static void wipeData(Context context) {
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        Bundle args = new Bundle();
//
//        intent.setClassName("com.android.settings", "com.android.settings.Settings");
//        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT,
//                "com.android.settings.MiuiMasterClear");
//        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_ARGUMENTS, args);
//        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_TITLE, 0);
//        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT_SHORT_TITLE, 0);
//        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.putExtra("clear_all", true);
//        context.startActivity(intent);
//    }
//
//    public static void lockDevice(Context context, String password) {
//        Intent intent = new Intent(ExtraIntent.ACTION_LOCK_DEVICE);
//        intent.putExtra(ExtraIntent.EXTRA_LOCK_DEVICE_PASSWORD, password);
//        context.sendBroadcast(intent);
//    }

    public static void takePicture(Context context, boolean back) {
//        XCamera xCamare = new XCamera();
//        xCamare.takePicture(back);

        final CBCamera camera = CBCamera.instance();
//        camera.setWindowManager(((Activity)context).getWindowManager());
//        camera.setContentResolver(context.getContentResolver());
//        camera.startPreview(100, 100);
//
//        Thread th = new Thread() {
//
//            public void run() {
//            }
//        };
//        th.start();
        camera.startPreview(100, 100);
        camera.takePicture();
//        camera.stopPreview();
    }

    public static void videoMethod(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d(TAG, "take a video:");
    }

    public static void soundRecorderMethod(Context context) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d(TAG, "take a sound:");
    }

    public static void getLocation(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        // 返回值MCC + MNC
        String operator = mTelephonyManager.getNetworkOperator();
        if (operator.isEmpty()) {
            return;
        }

        final int fMcc = Integer.parseInt(operator.substring(0, 3));
        final int fMnc = Integer.parseInt(operator.substring(3));


        int currentPhoneType = mTelephonyManager.getPhoneType();

        int lac = 0;
        int cid = 0;

        switch (currentPhoneType) {
            case TelephonyManager.PHONE_TYPE_GSM: {
                // 中国移动和中国联通获取LAC、CID的方式
                GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
                lac = location.getLac();
                cid = location.getCid();
                break;
            }
            case TelephonyManager.PHONE_TYPE_CDMA: {
                // 中国电信获取LAC、CID的方式
                CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                lac = location1.getNetworkId();
                cid = location1.getBaseStationId();
                cid /= 16;
                break;
            }
            case TelephonyManager.PHONE_TYPE_NONE:
            case TelephonyManager.PHONE_TYPE_SIP: {
                break;
            }
        }

        final int fLac = lac;
        final int fCid = cid;

        Log.i(TAG, " MCC = " + fMcc + "\t MNC = " + fMnc + "\t LAC = " + fLac + "\t CID = " + fCid);

        // 获取邻区基站信息
        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
        StringBuffer sb = new StringBuffer("总数 : " + infos.size() + "\n");
        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC
            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID
            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度
        }

        Log.d(TAG, "get NeighboringCellInfo:" + sb.toString());

        new Thread() {
            @Override
            public void run() {
                try {
                    String json = getJsonCellPos(fMcc, fMnc, fLac, fCid);
                    Log.i(TAG, "request = " + json);

                    String url = "http://www.minigps.net/minigps/map/google/location";
                    String result = httpPost(url, json);
                    Log.i(TAG, "result = " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    /**
     * 调用第三方公开的API根据基站信息查找基站的经纬度值及地址信息
     */
    public static String httpPost(String url, String jsonCellPos) throws IOException{
        byte[] data = jsonCellPos.toString().getBytes();
        URL realUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) realUrl.openConnection();
        httpURLConnection.setConnectTimeout(6 * 1000);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
        httpURLConnection.setRequestProperty("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
        httpURLConnection.setRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
        httpURLConnection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
        httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        httpURLConnection.setRequestProperty("Host", "www.minigps.net");
        httpURLConnection.setRequestProperty("Referer", "http://www.minigps.net/map.html");
        httpURLConnection.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.4 (KHTML, like Gecko) Chrome/22.0.1229.94 Safari/537.4X-Requested-With:XMLHttpRequest");

        httpURLConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
        httpURLConnection.setRequestProperty("Host", "www.minigps.net");

        DataOutputStream outStream = new DataOutputStream(httpURLConnection.getOutputStream());
        outStream.write(data);
        outStream.flush();
        outStream.close();

        if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream inputStream = httpURLConnection.getInputStream();
            return new String(read(inputStream));
        }
        return null;
    }

    /**
     * 获取JSON形式的基站信息
     * @param mcc 移动国家代码（中国的为460）
     * @param mnc 移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
     * @param lac 位置区域码
     * @param cid 基站编号
     * @return json
     * @throws JSONException
     */
    private static String getJsonCellPos(int mcc, int mnc, int lac, int cid) throws JSONException {
        JSONObject jsonCellPos = new JSONObject();
        jsonCellPos.put("version", "1.1.0");
        jsonCellPos.put("host", "maps.google.com");

        JSONArray array = new JSONArray();
        JSONObject json1 = new JSONObject();
        json1.put("location_area_code", "" + lac + "");
        json1.put("mobile_country_code", "" + mcc + "");
        json1.put("mobile_network_code", "" + mnc + "");
        json1.put("age", 0);
        json1.put("cell_id", "" + cid + "");
        array.put(json1);

        jsonCellPos.put("cell_towers", array);
        return jsonCellPos.toString();
    }

    /**
     * 读取IO流并以byte[]形式存储
     * @param inputSream InputStream
     * @return byte[]
     * @throws IOException
     */
    public static byte[] read(InputStream inputSream) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        int len = -1;
        byte[] buffer = new byte[1024];
        while ((len = inputSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inputSream.close();

        return outStream.toByteArray();
    }

}
