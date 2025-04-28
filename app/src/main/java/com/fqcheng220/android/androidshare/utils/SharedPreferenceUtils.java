package com.fqcheng220.android.androidshare.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2025/4/27 13:09
 */
public class SharedPreferenceUtils {
    public static SharedPreferences getSP(final Context context, final String name) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static String getString(final Context context, final String name, final String key) {
        SharedPreferences sharedPreferences = getSP(context, name);
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, "");
        }
        return "";
    }

    public static void asyncPutString(final Context context, final String name, final String key, final String value) {
        putString(context, name, key, value, false);
    }

    public static void syncPutString(final Context context, final String name, final String key, final String value) {
        putString(context, name, key, value, true);
    }

    public static void putString(final Context context, final String name, final String key, final String value, final boolean sync) {
        SharedPreferences sharedPreferences = getSP(context, name);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit().putString(key, value);
            if (sync) {
                editor.commit();
            } else {
                editor.apply();
            }
        }
    }
}
