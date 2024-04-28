package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import com.fqcheng220.android.androidshare.utils.Logger;
import java.lang.reflect.Method;

/**
 *
 */
public class DialogActivity extends Activity {

  private static final String TAG = "DialogActivity";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_dialog);
    findViewById(R.id.tv_test_dialog).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        AlertDialog alertDialog = new AlertDialog.Builder(DialogActivity.this).setTitle("test").create();
        alertDialog.show();
        new Handler().postDelayed(new Runnable() {
          @Override public void run() {
            finish();
          }
        }, 10 * 1000);
      }
    });
    findViewById(R.id.tv_test_fullscreen_dialog1).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFullScreenDialog(100,WindowManager.LayoutParams.MATCH_PARENT,false);
      }
    });
    findViewById(R.id.tv_test_fullscreen_dialog2).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFullScreenDialog(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,false);
      }
    });
    findViewById(R.id.tv_test_fullscreen_dialog3).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFullScreenDialog(100,WindowManager.LayoutParams.MATCH_PARENT,true);
      }
    });
    findViewById(R.id.tv_test_fullscreen_dialog4).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFullScreenDialog(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT,true);
      }
    });
  }

  /**
   * Android 7.0以下没有函数
   * {@link android.view.WindowManagerGlobal#closeAllExceptDecorView}，所以Android7.0以下需要手写实现
   */
  private void closeAllExceptDecorView() {
    Logger.d(TAG, "closeAllExceptDecorView");
    try {
      Class clz = Class.forName("android.view.WindowManagerGlobal");
      Method method1 = clz.getMethod("getInstance");
      method1.setAccessible(true);
      Object windowManagerGlobalObj = method1.invoke(null);

      Method method3 = Activity.class.getDeclaredMethod("getActivityToken");
      method3.setAccessible(true);
      IBinder binder = (IBinder) method3.invoke(this);

      Method method2 = clz.getDeclaredMethod("closeAllExceptView", new Class[] { IBinder.class, View.class, String.class, String.class });
      method2.setAccessible(true);
      method2.invoke(windowManagerGlobalObj, new Object[] { binder, this.getWindow().getDecorView(), null, null });
    } catch (Exception e) {
      e.printStackTrace();
      Logger.e(TAG, "closeAllExceptDecorView error:" + e.getLocalizedMessage());
    }
  }

  /**
   * dialog窗口设置好"android:windowFullscreen属性后，
   * 如果{@param width}和{@param height}都是{@link WindowManager.LayoutParams#MATCH_PARENT}，则系统状态栏会隐藏!!!dialog消失后则系统状态栏恢复正常
   * @param width
   * @param height
   */
  private void testFullScreenDialog(final int width,final int height,boolean bgTransparent) {
    Dialog dialog = new AlertDialog.Builder(this, R.style.DialogFullScreen).setTitle("testTitle").setMessage("testMessage").create();
    WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
    layoutParams.width = width;
    layoutParams.height = height;
    dialog.getWindow().setGravity(Gravity.CENTER);
    if(bgTransparent){
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    dialog.getWindow().setAttributes(layoutParams);
    dialog.show();
  }
}
