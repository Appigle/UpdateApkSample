package com.chenley.an.updateappsample.networking;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.chenley.an.updateappsample.Listener.IUpdateDownLoadListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Author:    Chenley
 * Version    V2.0
 * Date:      2016/9/16
 * Description: 真正处理文件的下载和线程间的通信类
 * Modification    History:
 * Date         	Author        		Version        	Description
 * ------------------------------------------------------------------
 * 2016/9/16         Chenley             2.0                2.0
 * Why & What is modified:
 */
public class UpdateDownloadRequest implements Runnable {
    private static final String TAG = "UpdateDownloadRequest@@";
    /**
     * 下载apk文件地址
     */
    private String mDownloadUrl;
    /**
     * 本地存放文件地址
     */
    private String mLocalFilePath;
    /**
     * 更新下载监听
     */
    private IUpdateDownLoadListener mIUpdateDownLoadListener;

    /**
     * 下载时候进行flag
     */
    private boolean isDownloading = false;
    /**
     * 当前下载文件的长度
     */
    private long currentLength;
    /**
     * 下载响应Handler
     */
    private DownloadResponseHandler mHandler;

    public UpdateDownloadRequest(String mDownloadUrl
            , String mLocalFilePath
            , IUpdateDownLoadListener mIUpdateDownLoadListener) {
        this.mDownloadUrl = mDownloadUrl;
        this.mLocalFilePath = mLocalFilePath;
        this.mIUpdateDownLoadListener = mIUpdateDownLoadListener;
        this.mHandler = new DownloadResponseHandler();
    }


    @Override
    public void run() {
        try {
            makeRequest();
        } catch (IOException e) {
            mHandler.sendFailureMsg(FailureCode.IOERROR);
        } catch (InterruptedException e) {
            mHandler.sendFailureMsg(FailureCode.INTERRUPTED);
        }
    }

    /**
     * 真正去建立连接的方法
     */
    private void makeRequest() throws IOException, InterruptedException {
        if (!Thread.currentThread().isInterrupted()) {
            mHandler.sendStartMsg();
            try {
                URL url = new URL(mDownloadUrl);
                HttpURLConnection mHttpURLConnection = (HttpURLConnection) url.openConnection();
                mHttpURLConnection.setRequestMethod("GET");
                mHttpURLConnection.setConnectTimeout(5000);
                mHttpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                mHttpURLConnection.connect();//阻塞当前线程
                currentLength = mHttpURLConnection.getContentLength();
                if (!Thread.currentThread().isInterrupted()) {
                    mHandler.sendResponseMsg(mHttpURLConnection.getInputStream());
                }
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * 格式化数据，获取俩位小数的字符串数字
     *
     * @param val
     * @return
     */
    private String getTwoPointFloatStr(float val) {
        DecimalFormat mDecimalFormat = new DecimalFormat("0.00000000000");
        return mDecimalFormat.format(val);
    }

    /**
     * 错误类型枚举
     */
    public enum FailureCode {
        UNKONNHOST //找不到HOST地址
        , SOCKETERROR//sokcet错误
        , SOCKETTIMEOUT//超时
        , CONNECTTIMEOUT//超时
        , IOERROR//IO错误
        , HTTPRESPONSEERROR//http响应错误
        , JSONERROR//JSON数据错误
        , INTERRUPTED;//阻塞
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }


    private class DownloadResponseHandler {
        /**
         * 下载成功
         */
        protected static final int SUCCESS_MSG = 0X123;
        /**
         * 下载失败
         */
        protected static final int FAILURE_MSG = 0X124;
        /**
         * 下载开始
         */
        protected static final int START_MSG = 0X125;
        /**
         * 下载完成
         */
        protected static final int FINISH_MSG = 0X126;
        /**
         * 没有网络
         */
        protected static final int NETWORKING_OFF = 0X127;
        /**
         * 下载进度值更新
         */
        protected static final int PROGRESS_CHANGED = 0X128;
        /**
         * 完成下载文件长度
         */
        private float mCompleteSize = 0;
        /**
         * 完成下载进度值
         */
        private int mProgress = 0;

        /**
         * 完成信息发送和处理
         */
        private Handler mHandler;

        public DownloadResponseHandler() {
            this.mHandler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    handleSelfMessege(msg);
                }
            };
        }

        /**
         * 发送结束信息
         */
        protected void sendFinishMsg() {
            sendMsg(obtainMsg(FINISH_MSG, null));
            isDownloading = false;
        }

        /**
         * 发送开始信息
         */
        protected void sendStartMsg() {
            sendMsg(obtainMsg(START_MSG, null));
            isDownloading = true;
        }

        /**
         * 发送进度值更新信息
         */
        protected void sendProgressChangedMsg(int progress) {
            sendMsg(obtainMsg(PROGRESS_CHANGED, new Object[]{progress}));
        }

        /**
         * 发送错误信息
         *
         * @param failureCode
         */
        protected void sendFailureMsg(FailureCode failureCode) {
            sendMsg(obtainMsg(FAILURE_MSG, new Object[]{failureCode}));
            isDownloading = false;

        }

        /**
         * 发送消息
         *
         * @param msg
         */
        private void sendMsg(Message msg) {
            if (mHandler != null) {
                mHandler.sendMessage(msg);
            } else {
                handleSelfMessege(msg);
            }
        }

        /**
         * 获取一个消息对象
         *
         * @param responseMsg
         * @param response
         */
        protected Message obtainMsg(int responseMsg, Object response) {
            Message msg = null;
            if (mHandler != null) {
                msg = mHandler.obtainMessage(responseMsg, response);
            } else {
                msg = Message.obtain();
                msg.what = responseMsg;
                msg.obj = response;
            }
            return msg;
        }

        /**
         * 自身信息处理类
         *
         * @param msg
         */
        protected void handleSelfMessege(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MSG:
                    response = (Object[]) msg.obj;
                    onFailure((FailureCode) response[0]);
                    break;
                case PROGRESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMsg(((Integer) response[0]).intValue());
                    break;
                case FINISH_MSG:
                    onFinish();
                    break;
                case START_MSG:
                    onStarted();
                    break;
                case SUCCESS_MSG:
                    onSuccess();
                    break;
                default:
                    break;
            }
        }

        /**
         * 下载成功回调
         */
        public void onSuccess() {
            mIUpdateDownLoadListener.onSuccess();
        }

        /**
         * 下载进度更新回调
         *
         * @param progress
         */
        public void handleProgressChangedMsg(int progress) {
            mIUpdateDownLoadListener.onProgressChanged(progress, mDownloadUrl);
        }

        /**
         * 下载开始回调
         */
        public void onStarted() {
            mIUpdateDownLoadListener.onStarted();
        }

        /**
         * 下载结束回调
         */
        public void onFinish() {
            mIUpdateDownLoadListener.onFinished(mCompleteSize, mDownloadUrl);
        }

        /**
         * 下载失败回调
         *
         * @param failureCode
         */
        public void onFailure(FailureCode failureCode) {
            mIUpdateDownLoadListener.onFailure(failureCode);
        }

        /**
         * 文件下载和状态信息发送
         *
         * @param is
         */
        void sendResponseMsg(InputStream is) {
            RandomAccessFile mRandomAccessFile = null;
            mCompleteSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int lenth = -1;
                int limit = 0;
                mRandomAccessFile = new RandomAccessFile(mLocalFilePath, "rwd");
                while ((lenth = is.read(buffer)) != -1) {
                    if (isDownloading) {
                        mRandomAccessFile.write(buffer, 0, lenth);
                        mCompleteSize += lenth;
                        if (mCompleteSize < currentLength) {
                            mProgress = (int) (Float.parseFloat(getTwoPointFloatStr(mCompleteSize / currentLength))*100);
                            if (limit % 30 == 0 && mProgress <= 100) {
                                sendProgressChangedMsg(mProgress);
                            }
                            limit++;
                        }
                    }
                }
                sendFinishMsg();
            } catch (Exception e) {
                sendFailureMsg(FailureCode.IOERROR);
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (mRandomAccessFile != null) {
                        mRandomAccessFile.close();
                    }
                } catch (Exception e) {
                    sendFailureMsg(FailureCode.IOERROR);
                }
            }

        }
    }
}


