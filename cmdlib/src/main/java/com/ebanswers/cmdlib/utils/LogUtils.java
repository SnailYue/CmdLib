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

    public static void setDebug(boolean flag) {
        DEBUG = flag;
    }

    public static void d(String tag,String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag,String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag,String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.i(tag, message);
        }
    }

    public static void v(String tag,String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.v(tag, message);
        }
    }

    public static void w(String tag,String message) {
        if (TextUtils.isEmpty(message)) {
            return;
        }
        if (DEBUG) {
            Log.w(tag, message);
        }
    }

    public static void log(String message, Object... args) {
        log(format(getDefault(), message + ":%s", args));
    }

    public static String formatStr(String str, Object... args) {
        return format(getDefault(), str, args);
    }
}
