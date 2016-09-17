package com.chenley.an.updateappsample.Manager;

import com.chenley.an.updateappsample.Listener.IUpdateDownLoadListener;
import com.chenley.an.updateappsample.networking.UpdateDownloadRequest;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author:    Chenley
 * Version    V2.0
 * Date:      2016/9/16
 * Description: 下载调度管理器，调用下载请求类
 * Modification    History:
 * Date         	Author        		Version        	Description
 * ------------------------------------------------------------------
 * 2016/9/16         Chenley             2.0                2.0
 * Why & What is modified:
 */
public class UpdateAppManager {
    /**
     * 单例模式构建
     */
    private static UpdateAppManager mUpdateAppManager;

    /**
     * 线程池类
     */
    private ThreadPoolExecutor mThreadPoolExecutor;

    /**
     * 更新下载请求
     */
    private UpdateDownloadRequest mUpdateDownloadRequest;

    /**
     * 私有构造方法
     */
    private UpdateAppManager() {
        mThreadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    /**
     * @return
     */
    public static UpdateAppManager getIntance() {
        return new UpdateAppManager();
    }


    /**
     * 开始下载文件
     */
    public void startDownload(String downloadUrl
            , String localPath
            , IUpdateDownLoadListener mIUpdateDownLoadListener) {
        if (mUpdateDownloadRequest != null) {
            return;
        }
        checkFilePath(localPath);
        mUpdateDownloadRequest = new UpdateDownloadRequest(downloadUrl
                , localPath, mIUpdateDownLoadListener);
        Future<?> future = mThreadPoolExecutor.submit(mUpdateDownloadRequest);
    }

    /**
     * 检查文件是否存在
     */
    private void checkFilePath(String localFilePath) {
        try {
            File dir = new File(localFilePath.substring(0, localFilePath.lastIndexOf("/")));
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File appFile = new File(dir, "appName");
            if (appFile.exists()) {
                appFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}