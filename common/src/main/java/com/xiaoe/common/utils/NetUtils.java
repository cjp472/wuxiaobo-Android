package com.xiaoe.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author flynnWang
 * @date 2018/11/22
 * <p>
 * 描述：网络工具类
 */
public class NetUtils {

    private static final String TAG = "NetUtils";
    private static final int LOW_SPEED_UPLOAD_BUF_SIZE = 1024;
    private static final int HIGH_SPEED_UPLOAD_BUF_SIZE = 10240;
    private static final int MAX_SPEED_UPLOAD_BUF_SIZE = 102400;
    private static final int LOW_SPEED_DOWNLOAD_BUF_SIZE = 2024;
    private static final int HIGH_SPEED_DOWNLOAD_BUF_SIZE = 30720;
    private static final int MAX_SPEED_DOWNLOAD_BUF_SIZE = 102400;

    public static final String NETWORK_TYPE_2G = "2G";
    public static final String NETWORK_TYPE_3G = "3G";
    public static final String NETWORK_TYPE_4G = "4G";
    public static final String NETWORK_TYPE_WIFI = "WIFI";
    public static final String NETWORK_TYPE_UNKONW_NETWORK = "unkonw network";
    public static final String NETWORK_TYPE_NO_NETWORK = "no network";

    private NetUtils() {
    }

    public static boolean hasNetwork(Context var0) {
        if (var0 != null) {
            ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo var2 = var1.getActiveNetworkInfo();
            return var2 != null && var2.isAvailable();
        } else {
            return false;
        }
    }

    public static boolean hasDataConnection(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getNetworkInfo(1);
        if (var2 != null && var2.isAvailable()) {
            Log.d("net", "has wifi connection");
            return true;
        } else {
            var2 = var1.getNetworkInfo(0);
            if (var2 != null && var2.isConnectedOrConnecting()) {
                Log.d("net", "has mobile connection");
                return true;
            } else {
                Log.d("net", "no data connection");
                return false;
            }
        }
    }

    public static boolean isWifiConnection(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getNetworkInfo(1);
        if (var2 != null && var2.isConnected()) {
            Log.d("net", "wifi is connected");
            return true;
        } else {
            return false;
        }
    }

    public static int getUploadBufSize(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        if (var2 != null && var2.getType() == 1) {
            return 102400;
        } else {
            return var2 == null && isConnectionFast(var2.getType(), var2.getSubtype()) ? 10240 : 1024;
        }
    }

    public static int getDownloadBufSize(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        if (var2 != null && var2.getType() == 1) {
            return 102400;
        } else {
            return var2 == null && isConnectionFast(var2.getType(), var2.getSubtype()) ? 30720 : 2024;
        }
    }

    private static boolean isConnectionFast(int var0, int var1) {
        if (var0 == 1) {
            return true;
        } else {
            if (var0 == 0) {
                switch (var1) {
                    case 1:
                        return false;
                    case 2:
                        return false;
                    case 3:
                        return true;
                    case 4:
                        return false;
                    case 5:
                        return true;
                    case 6:
                        return true;
                    case 7:
                        return false;
                    case 8:
                        return true;
                    case 9:
                        return true;
                    case 10:
                        return true;
                    default:
                        if (Build.VERSION.SDK_INT >= 11 && (var1 == 14 || var1 == 13)) {
                            return true;
                        }
                        if (Build.VERSION.SDK_INT >= 9 && var1 == 12) {
                            return true;
                        }
                        if (Build.VERSION.SDK_INT >= 8 && var1 == 11) {
                            return false;
                        }
                }
            }
            return false;
        }
    }

    public static String getNetworkType(Context var0) {
        ConnectivityManager var1 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo var2 = var1.getActiveNetworkInfo();
        if (var2 != null && var2.isAvailable()) {
            int var3 = var2.getType();
            if (var3 == 1) {
                return NETWORK_TYPE_WIFI;
            } else {
                TelephonyManager var4 = (TelephonyManager) var0.getSystemService(Context.TELEPHONY_SERVICE);
                switch (var4.getNetworkType()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return NETWORK_TYPE_2G;
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return NETWORK_TYPE_3G;
                    case 13:
                        return NETWORK_TYPE_4G;
                    default:
                        return NETWORK_TYPE_UNKONW_NETWORK;
                }
            }
        } else {
            return NETWORK_TYPE_NO_NETWORK;
        }
    }

}
