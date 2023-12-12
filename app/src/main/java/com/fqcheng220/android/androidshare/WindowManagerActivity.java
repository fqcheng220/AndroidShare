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

/**
 * 一加7Tpro 12
 * 通过WindowManager#addView添加全屏view，结果发现底部导航栏会预留空间，这是为什么？
 * 如果是通过Activity#getWindow()#getDecorView()#addView()并且LayoutParams属性设置宽高MATCH_PARENT的话,屏幕会被填充满的
 */
public class WindowManagerActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_windowmanager);
    }

    @Override protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                WindowManager windowManager =
                    (WindowManager) WindowManagerActivity.this.getSystemService(Context.WINDOW_SERVICE);
                //windowManager = getWindowManager();
                View root = LayoutInflater.from(WindowManagerActivity.this).inflate(R.layout.view_windowmanager,null);
                WindowManager.LayoutParams layoutParams =
                    new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_SUB_PANEL;
                layoutParams.format = PixelFormat.RGBA_8888/*-3*/;
                layoutParams.flags =
                    /*WindowManager.LayoutParams.FLAG_FULLSCREEN | */WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS/*8519680*/;
                windowManager.addView(root,layoutParams);
            }
        },5*1000);
    }
}
