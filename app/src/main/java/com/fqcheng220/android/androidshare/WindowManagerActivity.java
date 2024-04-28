package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.fqcheng220.android.androidshare.utils.HirearchyUtils;

import static android.view.WindowManager.LayoutParams.FLAG_DIM_BEHIND;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

/**
 * 一加7Tpro 12
 * 通过WindowManager#addView添加全屏view，结果发现底部导航栏会预留空间，这是为什么？
 * 如果是通过Activity#getWindow()#getDecorView()#addView()并且LayoutParams属性设置宽高MATCH_PARENT的话,屏幕会被填充满的
 */
public class WindowManagerActivity extends Activity {
  private View mViewRoot;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_windowmanager);
  }

  @Override protected void onResume() {
    super.onResume();
    if (mViewRoot == null) {
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          WindowManager windowManager = (WindowManager) WindowManagerActivity.this.getSystemService(Context.WINDOW_SERVICE);
          //windowManager = getWindowManager();
          final View root = LayoutInflater.from(WindowManagerActivity.this).inflate(R.layout.view_windowmanager, null);
          WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
          //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManagerActivity.this.getResources().getDisplayMetrics().widthPixels,
          //    WindowManagerActivity.this.getResources().getDisplayMetrics().heightPixels);
          layoutParams.type = WindowManager.LayoutParams.TYPE_BASE_APPLICATION;
          layoutParams.format = PixelFormat.RGBA_8888/*-3*/;
          layoutParams.flags =
              WindowManager.LayoutParams.FLAG_FULLSCREEN | FLAG_LAYOUT_IN_SCREEN | FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS/*8519680*/;
          layoutParams.flags |= FLAG_LAYOUT_NO_LIMITS;//经测试无效；参考https://blog.csdn.net/ccc905341846/article/details/114372557
          //layoutParams.flags |= FLAG_DIM_BEHIND;
          //layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          //    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
          windowManager.addView(root, layoutParams);
          mViewRoot = root;
        }
      }, 5 * 1000);
    } else {
      HirearchyUtils.printHirearchy(mViewRoot);
      HirearchyUtils.printHirearchy(getWindow());
    }
  }
}
