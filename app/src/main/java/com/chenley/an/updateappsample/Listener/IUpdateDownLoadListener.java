package com.chenley.an.updateappsample.Listener;

import com.chenley.an.updateappsample.networking.UpdateDownloadRequest;

/**
 * Author:    Chenley
 * Version    V2.0
 * Date:      2016/9/16
 * Description: 事件的监听回调
 * Modification    History:
 * Date         	Author        		Version        	Description
 * ------------------------------------------------------------------
 * 2016/9/16         Chenley             2.0                2.0
 * Why & What is modified:
 */
public interface IUpdateDownLoadListener {
    /**
     * 下载更新开始回调
     */
    void onStarted();

    /**
     * 更新进度回调
     * @param progress
     * @param downloadUrl
     */
    void onProgressChanged(int progress, String downloadUrl);

    /**
     * 更新下载完成回调
     * @param completeSize
     * @param downloadUrl
     */
    void onFinished(float completeSize, String downloadUrl);

    /**
     *  下载更新失败回调
     * @param failureCode
     */
    void onFailure(UpdateDownloadRequest.FailureCode failureCode);

    /**
     * 下载成功回调
     */
    void onSuccess();
}