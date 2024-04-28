package com.fqcheng220.android.androidshare.base;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.fqcheng220.android.androidshare.utils.Logger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2024/4/1 18:27
 */
public class COWDemo {
  private static final String TAG = "COWDemo";

  public static void main(String[] args) {
  }

  public void testSortCopyOnWriteArrayList() {
    CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList();
    copyOnWriteArrayList.add(0);
    Collections.sort(copyOnWriteArrayList);
  }

  public void getTargetSdkVersion() {
    try {
      Class clz = Class.forName("dalvik.system.VMRuntime");

      Method methodGetRuntime = clz.getMethod("getRuntime", null);
      methodGetRuntime.setAccessible(true);
      Object obj = methodGetRuntime.invoke(null, null);

      //从Android10.0开始无法通过反射获取到这个方案，至于Android系统如何限制的，可以参考思路 https://blog.csdn.net/muxaioxie/article/details/110383018
      //之前版本可以正常通过反射获取
      Method methodGetTargetSdkVersion = clz.getMethod("getTargetSdkVersion", null);
      methodGetTargetSdkVersion.setAccessible(true);
      int targetSdk = (int) methodGetTargetSdkVersion.invoke(obj, null);

      Logger.d(TAG, "getTargetSdkVersion targetSdk=" + targetSdk);
    } catch (ClassNotFoundException | NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  public void getTargetSdkVersion2(Context context) {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo("com.fqcheng220.android.androidshare", PackageManager.GET_META_DATA);
      Logger.d(TAG, "getTargetSdkVersion2 targetSdk=" + packageInfo.applicationInfo.targetSdkVersion);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }
}
