package com.chenley.an.updateappsample.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.chenley.an.updateappsample.Listener.IUpdateDownLoadListener;
import com.chenley.an.updateappsample.Manager.UpdateAppManager;
import com.chenley.an.updateappsample.R;
import com.chenley.an.updateappsample.networking.UpdateDownloadRequest;

import java.io.File;

/**
 * Author:    Chenley
 * Version    V2.0
 * Date:      2016/9/17
 * Description:
 * Modification    History:
 * Date         	Author        		Version        	Description
 * ------------------------------------------------------------------
 * 2016/9/17         Chenley             2.0                2.0
 * Why & What is modified:
 */
public class UpdateService extends Service {
    private static final String TAG = UpdateService.class.getSimpleName() + "@@";
    /**
     * apk下载地址
     */
    private String mApkUrl;
    /**
     * 下载文件存放路径
     */
    private String mFilePath;
    /**
     * 更新app管理器
     */
    private UpdateAppManager mUpdateAppManager;
    /**
     * 通知管理器
     */
    private NotificationManager mNotificationManager;
    /**
     * 通知
     */
    private Notification mNotification;

    @Override
    public void onCreate() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mFilePath = Environment.getExternalStorageDirectory() + "/yutown.apk";
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            notifyUser(getString(R.string.update_download_failed), getString(R.string.udpate_download_failed_msg), 0);
            stopSelf();
        }
        mApkUrl = intent.getStringExtra("apkUrl");
        notifyUser(getString(R.string.udpate_download_started), getString(R.string.udpate_download_started), 0);
        startDownload();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload() {
        UpdateAppManager manager = UpdateAppManager.getIntance();
        manager.startDownload(mApkUrl, mFilePath, new IUpdateDownLoadListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "onStarted: " + getString(R.string.udpate_download_started));
            }

            @Override
            public void onProgressChanged(int progress, String downloadUrl) {
                Log.d(TAG, "onProgressChanged() called with: " + "progress = [" + progress + "], downloadUrl = [" + downloadUrl + "]");
                notifyUser(getString(R.string.udpate_download_processing)
                        , getString(R.string.udpate_download_processing)
                        , progress);
            }

            @Override
            public void onFinished(float completeSize, String downloadUrl) {
                Log.d(TAG, "onFinished() called with: " + "completeSize = [" + completeSize + "], downloadUrl = [" + downloadUrl + "]");
                notifyUser(getString(R.string.udpate_download_finished)
                        , getString(R.string.udpate_download_finished)
                        , 100);
                stopSelf();
            }

            @Override
            public void onFailure(UpdateDownloadRequest.FailureCode failureCode) {
                Log.d(TAG, "onFailure() called with: " + "failureCode = [" + failureCode + "]");
                notifyUser(getString(R.string.update_download_failed)
                        , getString(R.string.update_download_failed)
                        , 0);
                stopSelf();
            }

            @Override
            public void onSuccess() {
                Log.d(TAG, "onSuccess() called with: " + "");
                notifyUser(getString(R.string.update_download_sucess)
                        , getString(R.string.update_download_sucess)
                        , 100);
                stopSelf();
            }
        });
    }

    /**
     * 通知用户
     *
     * @param updateState 更新状态
     * @param updateMsg   更新信息
     * @param progress    更新进度
     */
    private void notifyUser(String updateState, String updateMsg, int progress) {
        Log.d(TAG, "notifyUser() called with: " + "updateState = [" + updateState + "], updateMsg = [" + updateMsg + "], progress = [" + progress + "]");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources()
                        , R.mipmap.ic_launcher))
                .setContentText(getString(R.string.app_name))
                .setAutoCancel(true)
                .setTicker(updateState)
                .setWhen(System.currentTimeMillis());
        if (progress > 0 && progress <= 100) {
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setContentIntent(progress >= 100
                ? getContentIntent_()
                : PendingIntent.getActivity(this, 0, new Intent(), PendingIntent.FLAG_UPDATE_CURRENT));
        mNotification = builder.build();
        mNotificationManager.notify(0, mNotification);
    }

    /**
     * 文件下载文成，安装apk文件
     * @return
     */
    private PendingIntent getContentIntent_() {
        File apkFile = new File(mFilePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath())
                , "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0
                , intent
                , PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}