package com.thkj.bgrunner.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.thkj.bgrunner.R;
import com.thkj.bgrunner.server.Service1;
import com.thkj.bgrunner.utils.DownloadAndInstallApk;

public class MainActivity extends AppCompatActivity {
    DownloadAndInstallApk downloadApk = new DownloadAndInstallApk(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(MainActivity.this, Service1.class));
    }

    public void OnDownload(View v){
        StartInstallLogic();
    }

    public void StartInstallLogic()
    {
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Logger.d(">>开始检查");
                    downloadApk.StartWork();
                    Logger.d("<<结束检查");
                    //Thread.sleep(10000);
                }
                catch (Exception e){}
            }
        });
        thread.start();
    }
}
