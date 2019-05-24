package com.ebanswers.cmdlib.utils;

import android.text.TextUtils;
import android.util.Log;

import static java.lang.String.format;
import static java.util.Locale.getDefault;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */

public class LogUtils {
    public static boolean DEBUG = true;
    public final static String TAG = "Command";

    public static void setDebug(boolean flag) {
        DEBUG = flag;
    }

    public static void d(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, message);
        }
    }

    public static void e(String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.e(TAG, message);
        }
    }

    public static void log(String message, Object... args) {
        log(format(getDefault(), message + ":%s", args));
    }

    public static void w(Throwable throwable) {
        Log.w(TAG, throwable);
    }

    public static String formatStr(String str, Object... args) {
        return format(getDefault(), str, args);
    }
}
