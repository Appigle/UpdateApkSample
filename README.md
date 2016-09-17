# UpdateApkSample
使用基础的网络请求方式，使用Service下载网络apk文件，然后进行安装的小DEMO；
1. 知识点：
> * 通过接口回调的方式去表现程序的运行过程：onStarted（）、onProcess（）、onfinish（） and so on...;
> * 结构清晰：Activity、Service、Manager、netwroking and so on...;
> * 成员变量命名规则是在名称前面+m（member，表示成员变量意思），静态变量则是在名称前面+s（static，表示静态的意思），同事遵从驼峰命名法；
> * 网络文件的基础流方式下载，以及apk文件下载完成后的Intent安装代码；
> * 使用线程池进行网络请求方法；
> * 使用接口实现回调对程序过程的监控和调试；
> * 内存卡文件的操作：检查是否操作，创建新的文件夹，创建新的文件，获取文件的绝对地址 and so on...
> * Notification/NotificationManager的基础用法；
> * Service的基础用法；
> * PendingIntent的基础用法；
> * 添加成员变量、方法的规范注释<**这里需要进一步学习**>；
> * 格式化浮点数获得指定小数位数的字符串
> ```Java
>   private String getTwoPointFloatStr(float val) {
        DecimalFormat mDecimalFormat = new DecimalFormat("0.00000000000");
        return mDecimalFormat.format(val);
      }
> ```


