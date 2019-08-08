package com.thkj.bgrunner.utils;

import android.content.Context;

import com.orhanobut.logger.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.jessyan.progressmanager.body.ProgressInfo;

public class DownloadAndInstallApk {
    private Context ctx;
    private HashMap<String, FileUtils.AppInfo> hnAppInfo;
    private HashMap<String, DownloadInfo> hmDownloadInfo = new HashMap<>();
    private String pkgInfo;

    public class DownloadInfo{
        public String url;
        public String pkgname;
        public String versionname;
        public DownloadInfo(String url, String pkgname, String versionname){
            this.url = url;
            this.pkgname = pkgname;
            this.versionname = versionname;
        }
    }

    public DownloadAndInstallApk(Context ctx){
        this.ctx = ctx;
    }

    public void StartWork()
    {
        GetLocalApkInfo();      // 1. 获取本机安装的apk信息
        GetPkgInfoFromSvr();    // 2. 获取服务器上的安装包信息
        ParsePkgInfo();         // 3. 解析服务器上的安装包信息
        CheckForWork();         // 4. 根据1和3的结果进行安装/卸载apk
    }

    public void GetLocalApkInfo(){
        hnAppInfo = FileUtils.getAppInfo(ctx, 1);
    }

    public void GetPkgInfoFromSvr(){
        pkgInfo = "[{\"url\":\"http://192.168.0.73:8900/com.inpoint.cyksapp.apk\", \"pkgname\":\"com.inpoint.cyksapp\",\"versionname\":\"1\"},{\"url\":\"http://192.168.0.73:8900/com.dgys.hnmsawebapp.apk\", \"pkgname\":\"com.dgys.hnmsawebapp\",\"versionname\":\"1\"},{\"pkgname\":\"com.inpoint.hangyuntong\"}]";
        Logger.d("服务器获取到的数据: " + pkgInfo);
    }

    public void ParsePkgInfo(){
        try{
            hmDownloadInfo.clear();
            JSONArray jPkgInfo = (JSONArray) new JSONTokener(pkgInfo).nextValue();
            if(jPkgInfo.length()>0){
                for(int i = 0; i < jPkgInfo.length(); i++) {
                    JSONObject jo = jPkgInfo.getJSONObject(i);
                    if (jo.has("url") && jo.has("versionname") && jo.has("pkgname"))
                    {
                        String szName = jo.getString("pkgname").trim();
                        hmDownloadInfo.put(szName, new DownloadInfo(jo.getString("url").trim(),
                                        szName, jo.getString("versionname").trim()));
                    }
                    else if (jo.has("pkgname")){
                        String szName = jo.getString("pkgname").trim();
                        hmDownloadInfo.put(szName, new DownloadInfo("", szName,""));
                    }
                }
            }
        }
        catch (Exception ex){}
    }

    public void CheckForWork(){
        Iterator iter = hmDownloadInfo.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String szPkgName = entry.getKey().toString();
            DownloadInfo di = (DownloadInfo)entry.getValue();
            if (hnAppInfo.containsKey(szPkgName))
            {
                if (di.url.length() == 0)
                {
                    // 卸载App
                    Logger.d("卸载: " + di.pkgname);
                    FileUtils.UninstallApk(di.pkgname);
                }
                else
                {
                    // 更新
                    FileUtils.AppInfo ai = hnAppInfo.get(szPkgName);
                    if (di.versionname.compareTo(ai.versionName) != 0)
                    {
                        DownloadAndInstall(di.url, di.pkgname);
                    }
                }
            }
            else
            {
                // 安装
                if (di.url.length() != 0)
                {
                    DownloadAndInstall(di.url, di.pkgname);
                }
            }
        }
    }

    public void DownloadAndInstall(String downUrl, String szPkgName){
        DownloadUtils.getInstance().download(downUrl, FileUtils.DOWNLOAD_PATH, szPkgName, new DownloadUtils.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(String szName) {
                Logger.d("下载完毕: "+ szName);
                String szFileName = szName;
                if (szName.toLowerCase().indexOf(".apk") == -1)
                    szFileName = szName + ".apk";
                Logger.d("开始安装: "+ szName);
                FileUtils.InstallApp(FileUtils.DOWNLOAD_PATH + "/" + szFileName + "", szName);
            }
            @Override
            public void onDownloading(String szName, ProgressInfo progressInfo) {
                //Logger.d("正在下载: "+ szName + " - " + progressInfo.getPercent() + "%");
            }
            @Override
            public void onDownloadFailed(String szName, Exception ex) {
                Logger.d("下载失败: "+ szName);
            }
        });
    }
}
