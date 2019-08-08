package com.thkj.bgrunner.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;

import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileUtils {
    public static final String HSFX_ROOT_PATH = Environment.getExternalStorageDirectory().getPath() + "/bgrunner/";
    public static final String DOWNLOAD_PATH = HSFX_ROOT_PATH + "download";

    /**
     * 检查SD卡是否存在
     *
     * @return 存在返回true，否则返回false
     */
    public static boolean isSdcardReady() {
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        return sdCardExist;
    }

    /**
     * 获得SD路径
     *
     * @return
     */
    public static String getSdcardPath() {
        return Environment.getExternalStorageDirectory().toString() + File.separator;
    }

    /**
     * 获取缓存路径
     *
     * @param context
     * @return
     */
    public static String getCachePath(Context context) {
        File cacheDir = context.getCacheDir();//文件所在目录为getFilesDir();
        return cacheDir.getPath() + File.separator;
    }

    /**
     * 根据文件路径 递归创建文件
     *
     * @param file
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 安装APK
    public static boolean InstallApp(String path, String pkgName){
        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        try {
            process = new ProcessBuilder("pm", "install","-i", pkgName, "-r", path).start();
            successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s = successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {

        } finally {
            try {
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {

            }
            if (process != null) {
                process.destroy();
            }
        }

        Logger.d("安装[" + pkgName + "]结果: " + errorMsg.toString() + ":" + successMsg.toString());
        return successMsg.toString().equalsIgnoreCase("success");
    }

    public static void UninstallApk(String pkgName){
        try
        {
            new ProcessBuilder("pm", "uninstall", pkgName).start();
        }
        catch (Exception ex){}
    }

    public static class AppInfo{
        public String appName;
        public String packageName;
        public String versionName;
        public int versionCode;
    }

    /**
     *
     * @param sign 1、本机全部app的信息 2、系统应用的信息 3、非系统应用的信息
     * @return app的信息
     */
    public static HashMap<String, AppInfo> getAppInfo(Context ctx, int sign) {
        HashMap<String, AppInfo> appList = new HashMap<>(); //用来存储获取的应用信息数据　　　　　
        List<PackageInfo> packages = ctx.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpInfo = new AppInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(ctx.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            tmpInfo.versionCode = packageInfo.versionCode;
            if (sign == 1) {//全手机全部应用
                appList.put(tmpInfo.packageName, tmpInfo);
            } else if (sign == 2) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    appList.put(tmpInfo.packageName, tmpInfo);//如果非系统应用，则添加至appList
                }
            } else if (sign == 3) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    appList.put(tmpInfo.packageName, tmpInfo);//如果非系统应用，则添加至appList
                }
            }
        }
        return appList;
    }
}
