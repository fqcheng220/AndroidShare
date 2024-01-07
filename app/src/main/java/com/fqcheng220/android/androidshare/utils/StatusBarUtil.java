package com.fqcheng220.android.androidshare.utils;

import android.content.Context;

import java.lang.reflect.Field;

public class StatusBarUtil {
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        try {
            int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                height = context.getResources().getDimensionPixelSize(resourceId);
            }
        } catch (Throwable var7) {
            height = 0;
        }
        if (height == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int resourceId = Integer.parseInt(field.get(obj).toString());
                if (resourceId > 0) {
                    height = context.getResources().getDimensionPixelSize(resourceId);
                }
            } catch (Throwable var6) {
                height = 0;
            }
        }
        return height;
    }
}
