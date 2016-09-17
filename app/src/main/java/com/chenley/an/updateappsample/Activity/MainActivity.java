package com.chenley.an.updateappsample.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chenley.an.updateappsample.R;
import com.chenley.an.updateappsample.Service.UpdateService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity@@";
    /**
     * APK文件下载地址
     */
    private String mApkUrl = "http://180.153.105.141/imtt.dd.qq.com/16891/8DA9DE8F848FEF9A4A8F1DAB317E4D90.apk?mkey=57a0193c3b0f8b41&f=d410&c=0&fsname=com.doyutown.fishpond_2.2.5_205.apk&csr=4d5s&p=.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_update_app).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateApp();
            }
        });
    }

    /**
     * 点击更新app
     */
    private void UpdateApp() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher)
                .setMessage(R.string.update_app)
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentService = new Intent(MainActivity.this, UpdateService.class);
                        intentService.putExtra("apkUrl", mApkUrl);
                        startService(intentService);
                    }
                })
                .create();
        dialog.show();
    }
}
