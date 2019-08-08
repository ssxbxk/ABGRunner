package com.thkj.bgrunner.server;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.thkj.bgrunner.utils.DownloadAndInstallApk;
import com.thkj.bgrunner.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

/**
 * This Service is Persistent Service. Do some what you want to do here.<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Service1 extends Service{
    DownloadAndInstallApk downloadApk = new DownloadAndInstallApk(getApplicationContext());
    @Override
    public void onCreate() {
        super.onCreate();

        StartInstallLogic();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void StartInstallLogic()
    {
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while(true)
                {
                    try
                    {
                        Logger.d(">>开始检查");
                        downloadApk.StartWork();
                        Logger.d("<<结束检查");
                        Thread.sleep(60000);
                    }
                    catch (Exception e){}
                }
            }
        });
        thread.start();
    }
}