package org.kteam.palm.common.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.kteam.common.utils.ConnectivityUtils;
import org.kteam.palm.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * @Package org.kteam.palm.common.utils
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-27 23:39
 */
public class APKUtils {

    private final Logger mLogger = Logger.getLogger(getClass());

    public static final int HANDLER_MSG_PD_UPDATE = 1000;
    public static final int HANDLER_MSG_DOWNLOAD_COMPLET = 1001;
    public static final int HANDLER_MSG_DOWNLOAD_FAILED = 1002;

    private Context mContext;

    public APKUtils(Context context) {
        mContext = context;
    }

    /**
     * 获得本地版本号
     */
    public Version getLocalVersionCode(){
        Version version = null;
        try {
            int versionCode = mContext.getPackageManager().getPackageInfo(Constants.PACKAGE_NAME, 0).versionCode;
            String versionName = mContext.getPackageManager().getPackageInfo(Constants.PACKAGE_NAME, 0).versionName;
            version = new Version();
            version.versionCode = versionCode;
            version.versionName = versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mLogger.error(e.getMessage(), e);
        }
        return version;
    }

    /**
     * 获得本地版本号
     */
    public static Version getLocalVersionCode(Context context){
        Version version = null;
        try {
            int versionCode = context.getPackageManager().getPackageInfo(Constants.PACKAGE_NAME, 0).versionCode;
            String versionName = context.getPackageManager().getPackageInfo(Constants.PACKAGE_NAME, 0).versionName;
            version = new Version();
            version.versionCode = versionCode;
            version.versionName = versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 下载APK文件
     */
    public void downFile(final Context context, final String url, final String filePath, final Handler handler){
        new Thread() {
            public void run() {
                HttpEntity entity = null;
                InputStream is = null;
                OutputStream os = null;
                try {
                    File file = new File(filePath);
                    File parentDir = file.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }
                    if (file.isDirectory()) {
                        file.delete();
                    }
                    if(file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                    os = new FileOutputStream(file);
                    HttpGet httpGet = new HttpGet(url);
                    httpGet.addHeader("Range", "bytes=" + (file.length()) + "-");
                    HttpClient client = new DefaultHttpClient();
                    if("cmwap".equals(ConnectivityUtils.getAPNType(context))) {
                        HttpHost proxy = new HttpHost( "10.0.0.172", 80, "http");
                        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                    }
                    HttpResponse response = client.execute(httpGet);
                    entity = response.getEntity();
                    is = entity.getContent();

                    long total = entity.getContentLength() + file.length();
                    long current = file.length();

                    int progress = 0;
                    long step = total / 100;
                    byte buf[] = new byte[1024];
                    int stepCount = 0;
                    do {
                        int numread = is.read(buf);
                        if (numread <= 0) {
                            break;
                        }
                        current += numread;
                        os.write(buf, 0, numread);
                        if (current % step < 1024) {
                            stepCount++;
                            if(stepCount == 5) {
                                progress = (int) (current * 100 / total);
                                Message msg = handler.obtainMessage();
                                msg.what = HANDLER_MSG_PD_UPDATE;
                                msg.arg1 = progress;
                                handler.sendMessage(msg);
                                stepCount = 0;
                            }
                        }
                    } while(true);
                    if (current >= total) {
                        Message msg = handler.obtainMessage();
                        msg.what = HANDLER_MSG_DOWNLOAD_COMPLET;
                        msg.obj = filePath;
                        handler.sendMessage(msg);
                        stepCount = 0;
                    }

                } catch(Exception e) {
                    mLogger.error(e.getMessage(), e);
                    handler.sendEmptyMessage(HANDLER_MSG_DOWNLOAD_FAILED);
                } finally {
                    try {
                        is.close();
                        os.close();
                        if (entity != null) {
                            entity.consumeContent();
                        }
                    } catch(Exception e) {
                        mLogger.error(e.getMessage(), e);
                    }
                }
            }
        }.start();
    }

    /**
     * 安装应用
     * @param packagePath
     * @return
     */
    public boolean installApp(String packagePath) {
        try {
            if(TextUtils.isEmpty(packagePath)) {
                return false;
            }
            File apkFile = new File(packagePath);
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", apkFile);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            }
            mContext.startActivity(intent);
            return true;
        } catch(Exception e) {
            mLogger.error(e.getMessage(), e);
        }
        return false;
    }


    /**
     * 卸载应用
     * @param pakageName
     */
    public void uninstallApp(String pakageName) {
        Uri packageURI = Uri.parse("package:"+pakageName);
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(packageURI);
        mContext.startActivity(intent);
    }

    public static class Version {
        public int versionCode;
        public String versionName;
    }


    public static void toAppDetailSettingIntent(Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }
}
