package com.upgrade.channey.common_upgrade;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by channey on 2017/1/3.
 * 自动更新工具类
 */

public class UpdateUtil {
    private static final String TAG = "UpdateUtil";
    private static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String apkName;
//    private static final String PATH = Environment.DIRECTORY_DOWNLOADS;

    /**
     * 下载apk
     *
     * @param context
     * @param listener {@link DownloadListener}
     */
    public static void downLoadApk(Context context,final DownloadListener listener,final String link){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ArrayList<String> list = new ArrayList<String>();
            String[] array;
            int writeStoragePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(writeStoragePermission != PackageManager.PERMISSION_GRANTED){
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(list.size() > 0){
                array = Util.listToArray(list);
                Activity activity = (Activity) context;
                activity.requestPermissions(array,10001);
            }else {
                DownLoadThread thread = new DownLoadThread(listener, link);
                thread.start();
            }
        }else {
            DownLoadThread thread = new DownLoadThread(listener, link);
            thread.start();
        }
    }

    /**
     * 安装apk
     *
     * @param context
     * @param authority file provider authority
     */
    public static void installApk(Context context,String authority){
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String type = "application/vnd.android.package-archive";
        File file = new File(PATH, apkName);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context,authority,file);
        } else {
            uri = Uri.fromFile(file);
        }
        i.setDataAndType(uri,type);
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public interface DownloadListener{
        void startDownload();
        void downloadSuccess();
        void downloadFailure(Exception e);
        void onDownload(long totalSize, long current);
    }

    /**
     * 截取出url后面的apk的文件名
     *
     * @param url
     * @return
     */
    private static String getApkNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/"), url.length());
    }

    static class DownLoadThread extends Thread{
        private DownloadListener listener;
        private String link;
        public DownLoadThread(final DownloadListener listener,final String link) {
            this.link = link;
            this.listener = listener;
        }

        @Override
        public void run() {
            try {
                listener.startDownload();
                URL url = new URL(link);
                apkName = getApkNameFromUrl(link);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                FileOutputStream fileOutputStream = null;
                InputStream inputStream;
                Environment.getDownloadCacheDirectory();
//                    String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/shengdaxd/";
//                String downLoadPath = Environment.getDownloadCacheDirectory().getAbsolutePath();
//                Log.d(TAG,"downLoadPath -> "+downLoadPath);
////                File parentFile = new File(PATH);
////                if (!parentFile.exists()){
//////                    parentFile.mkdir();
////                    parentFile.mkdirs();
////                }
                File file = new File(PATH, apkName);
                if(file.exists()){
                    file.delete();
                }
//                        Log.d(TAG,"下载到文件 "+file.getAbsolutePath()+" 中");
                if (connection.getResponseCode() == 200) {
                    int contentLength = connection.getContentLength();
                    inputStream = connection.getInputStream();

                    if (inputStream != null) {
//                    file = getFile(UpdateUtil.PATH);
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buffer = new byte[1024];
                        int length = 0;
                        long size = 0;
                        while ((length = inputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, length);
                            size += length;
                            listener.onDownload(contentLength,size);
                        }
                        Log.d(TAG,"file size "+size);
                        fileOutputStream.close();
                        fileOutputStream.flush();
                        listener.downloadSuccess();
                    }else {
                        listener.downloadFailure(new Exception("download failed!"));
                    }
                    inputStream.close();
                }else {
                    listener.downloadFailure(new Exception("download failed!"));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                listener.downloadFailure(e);
            } catch (IOException e) {
                e.printStackTrace();
                listener.downloadFailure(e);
            }finally {

            }
        }
    }
}
