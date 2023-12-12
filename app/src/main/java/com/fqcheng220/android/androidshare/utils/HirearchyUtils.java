package com.fqcheng220.android.androidshare.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description:
 * 输出View层级信息
 * @date 2023/12/5 17:37
 */
public class HirearchyUtils {
  private static final String TAG = "HirearchyUtils";

  public static void printHirearchy(Window window) {
    if(window != null){
      printHirearchy(window.getDecorView());
    }
  }

  public static void printHirearchy(View topView) {
    List<String> list = new ArrayList<>();
    dumpHirearchy(topView, list, 0);
    for (String item : list) {
      Logger.d(TAG, item);
    }
  }

  public static void dumpHirearchy(View topView, List<String> list, int depth) {
    if (topView != null && list != null) {
      StringBuilder stringBuilder = new StringBuilder();
      int iterDepth = depth;
      while (iterDepth-- >= 0) {
        stringBuilder.append("--");
      }
      stringBuilder.append("class={");
      stringBuilder.append(topView.getClass().getName());
      stringBuilder.append("}");
      stringBuilder.append(",id={");
      String idName = String.valueOf(topView.getId());
      try{
        idName = topView.getResources().getResourceName(topView.getId());
      }catch (Exception e){
        e.printStackTrace();
      }
      stringBuilder.append(idName);
      stringBuilder.append("}");
      stringBuilder.append(",width=");
      stringBuilder.append(topView.getMeasuredWidth());
      stringBuilder.append(",height=");
      stringBuilder.append(topView.getMeasuredHeight());
      int[] loc = new int[2];
      topView.getLocationOnScreen(loc);
      stringBuilder.append(",x=");
      stringBuilder.append(loc[0]);
      stringBuilder.append(",y=");
      stringBuilder.append(loc[1]);
      Rect rect = new Rect();
      topView.getWindowVisibleDisplayFrame(rect);
      stringBuilder.append(",WindowVisibleDisplayFrame=");
      stringBuilder.append(rect);
      list.add(stringBuilder.toString());
      if (topView instanceof ViewGroup) {
        for (int i = 0; i < ((ViewGroup) topView).getChildCount(); i++) {
          View innerView = ((ViewGroup) topView).getChildAt(i);
          dumpHirearchy(innerView, list, depth + 1);
        }
      }
    }
  }
}
