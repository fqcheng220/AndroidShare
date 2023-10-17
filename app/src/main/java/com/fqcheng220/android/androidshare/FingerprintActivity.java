package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import com.fqcheng220.android.androidshare.utils.Logger;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class FingerprintActivity extends Activity {
  private static final String TAG = FingerprintActivity.class.getSimpleName();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fingerprint);
    testManualCancelFingerprintAuth();
    //testManualCancelFingerprintAuth2();
  }

  /**
   * 测试使用android高版本sdk api打开指纹认证页面，
   * 在指纹认证页面还没有初始化好（这里的初始化好指的是HAL还没有初始化好）时就立刻手动关闭指纹认证页面{@link CancellationSignal#cancel()}
   */
  private void testManualCancelFingerprintAuth() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      final CancellationSignal cancellationSignal = new CancellationSignal();
      Executor executor = new MainThreadExecutor();
      final BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this).setTitle("title").setNegativeButton("cancel", executor, new DialogInterface.OnClickListener() {
        @Override public void onClick(DialogInterface dialogInterface, int i) {

        }
      }).build();
      biometricPrompt.authenticate(cancellationSignal, executor, new BiometricPrompt.AuthenticationCallback() {
        @Override public void onAuthenticationError(int errorCode, CharSequence errString) {
          super.onAuthenticationError(errorCode, errString);
        }
      });
      Logger.d(TAG, "prepare to auto cancel");
      /**
       * Android12 华为p50下 如果延时不够长，系统指纹验证页面可能会报取消被忽略错 Skipping cancellation for non-started operation
       *
       * 2023-10-17 13:31:10.055 30594-30594 FingerprintActivity     com.fqcheng220.android.androidshare  D  auto cancel
       * 2023-10-17 13:31:10.055  1797-1797  BiometricS...uthSession pid-1797                             D  sensorId: 0, shouldCancel: true
       * 2023-10-17 13:31:10.055  1797-1797  FingerprintService      pid-1797                             D  cancelAuthenticationFromService, sensorId: 0
       * 2023-10-17 13:31:10.055  1797-1797  Fingerprint21           pid-1797                             D  cancelAuthentication, sensorId: 0
       * 2023-10-17 13:31:10.055  1797-1797  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1139
       * 2023-10-17 13:31:10.055  1797-1797  HwFingerprintService    pid-1797                             D  CODE_NOTIFY_AUTHENTICATION_CANCELED
       * 2023-10-17 13:31:10.055  1797-3056  BiometricS...gerprint21 pid-1797                             D  cancelAuthenticationOrDetection, isCorrectClient: true, tokenMatches: true
       * 2023-10-17 13:31:10.056  1797-1797  HwFingerprintService    pid-1797                             I  notifyAuthenticationCanceled pkgName:com.fqcheng220.android.androidshare
       * 2023-10-17 13:31:10.056  1797-1797  FingerViewControllerTag pid-1797                             I  removeFingerViewOrImage pkgName:com.android.systemui, isAsync:false
       * 2023-10-17 13:31:10.056  1797-3056  BiometricS...gerprint21 pid-1797                             D  Cancelling: {[261] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=475984113, userId=0}, State: 4
       * 2023-10-17 13:31:10.056  1797-1797  HwFingerprintService    pid-1797                             I  fingerViewState type:0
       * 2023-10-17 13:31:10.056  1797-1797  HwFingerprintService    pid-1797                             I  suspendAuthentication:0
       * 2023-10-17 13:31:10.056  1797-5108  FingerView...llerThread pid-1797                             I  begin mRemoveFingerViewRunnable
       * 2023-10-17 13:31:10.056  1797-3056  BiometricS...gerprint21 pid-1797                             W  Skipping cancellation for non-started operation: {[261] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=475984113, userId=0}, State: 4
       */

      /**
       * 跟踪Android12系统源码/frameworks/base/services/core/java/com/android/server/biometrics/sensors/BiometricScheduler.java
       * Slog.w(getTag(), "Skipping cancellation for non-started operation: " + operation);
       * 可以看到注释// We can set it to null immediately, since the HAL was never notified to start.
       * 说明是HAL还没有初始化完成，客户端就立刻发起取消指纹认证所以会报错
       * 591      private void cancelInternal(Operation operation) {
       * 592          if (operation != mCurrentOperation) {
       * 593              Slog.e(getTag(), "cancelInternal invoked on non-current operation: " + operation);
       * 594              return;
       * 595          }
       * 596          if (!(operation.mClientMonitor instanceof Interruptable)) {
       * 597              Slog.w(getTag(), "Operation not interruptable: " + operation);
       * 598              return;
       * 599          }
       * 600          if (operation.mState == Operation.STATE_STARTED_CANCELING) {
       * 601              Slog.w(getTag(), "Cancel already invoked for operation: " + operation);
       * 602              return;
       * 603          }
       * 604          if (operation.mState == Operation.STATE_WAITING_FOR_COOKIE) {
       * 605              Slog.w(getTag(), "Skipping cancellation for non-started operation: " + operation);
       * 606              // We can set it to null immediately, since the HAL was never notified to start.
       * 607              mCurrentOperation = null;
       * 608              startNextOperationIfIdle();
       * 609              return;
       * 610          }
       * 611          Slog.d(getTag(), "[Cancelling] Current client: " + operation.mClientMonitor);
       * 612          final Interruptable interruptable = (Interruptable) operation.mClientMonitor;
       * 613          interruptable.cancel();
       * 614          operation.mState = Operation.STATE_STARTED_CANCELING;
       * 615
       * 616          // Add a watchdog. If the HAL does not acknowledge within the timeout, we will
       * 617          // forcibly finish this client.
       * 618          mHandler.postDelayed(new CancellationWatchdog(getTag(), operation),
       * 619                  CancellationWatchdog.DELAY_MS);
       * 620      }
       */
      //new Handler().postDelayed(new Runnable() {
      //  @Override public void run() {
          Logger.d(TAG, "auto cancel");
          cancellationSignal.cancel();
      //  }
      //}, 500);

      //new Handler().postDelayed(new Runnable() {
      //  @Override public void run() {
      //    hasFloatWindowByToken(FingerprintActivity.this);
      //  }
      //}, 1000);
    }
  }

  /**
   * 判断Activity上是否有显示dialog的方法
   * https://blog.csdn.net/chuyouyinghe/article/details/128017158
   *
   * 但是抓不到系统指纹验证页面，这是为什么？？怀疑是系统指纹验证页面是由单独进程加载的classloader去加载并实例化的WindowManagerGlobal实例持有，所以本app无法访问
   * @param activity
   */
  private void hasFloatWindowByToken(Activity activity){
    Logger.d(TAG, "hasFloatWindowByToken");
    if(activity == null)
      return;
    try {
      Class clz = Class.forName("android.view.WindowManagerImpl");
      Field field = clz.getDeclaredField("mGlobal");
      field.setAccessible(true);
      Object global = field.get(activity.getWindowManager());

      /**
       * android12源码 /frameworks/base/core/java/android/view/WindowManagerGlobal.java
       *
       * @UnsupportedAppUsage
       * 153      private final ArrayList<View> mViews = new ArrayList<View>();
       * 154      @UnsupportedAppUsage
       * 155      private final ArrayList<ViewRootImpl> mRoots = new ArrayList<ViewRootImpl>();
       * 156      @UnsupportedAppUsage
       * 157      private final ArrayList<WindowManager.LayoutParams> mParams =
       * 158              new ArrayList<WindowManager.LayoutParams>();
       */
      Class clzGlobal = Class.forName("android.view.WindowManagerGlobal");
      Field field1 = clzGlobal.getDeclaredField("mViews");
      field1.setAccessible(true);
      List<View> views = (List<View>)field1.get(global);
      Field field2 = clzGlobal.getDeclaredField("mParams");
      field2.setAccessible(true);
      List<WindowManager.LayoutParams> params = (List<WindowManager.LayoutParams>)field2.get(global);

      if (views != null) {
        int indexActivity = -1;
        for (int i = 0; i < views.size(); i++) {
          if (views.get(i) == activity.getWindow().getDecorView()) {
            indexActivity = i;
            break;
          }
        }

        if (params != null) {
          List<Integer> listIndex = new ArrayList<>();
          IBinder targetToken = activity.getWindow().getDecorView().getWindowToken();
          for (int i = 0; i < params.size(); i++) {
            WindowManager.LayoutParams params1 = params.get(i);
            if (params1 != null && params1.token == targetToken) {
              listIndex.add(i);
            }
          }
        }
      }

    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  private void testManualCancelFingerprintAuth2() {
    androidx.core.os.CancellationSignal cancelSignalCompat = new androidx.core.os.CancellationSignal();
    final FingerprintManagerCompat.CryptoObject cryptoObjectCompat = null;
    final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);
    fingerprintManagerCompat.authenticate(cryptoObjectCompat, 0, cancelSignalCompat, new FingerprintManagerCompat.AuthenticationCallback() {
      @Override public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
      }
    }, null);
    Logger.d(TAG, "testManualCancelFingerprintAuth2 auto cancel");
    cancelSignalCompat.cancel();
  }

  private static class MainThreadExecutor implements Executor {
    private Handler mHandler;

    public MainThreadExecutor() {
      mHandler = new Handler(Looper.getMainLooper());
    }

    @Override public void execute(Runnable command) {
      mHandler.post(command);
    }
  }

  /**
   * androidx
   */
  //private void hackBio(BiometricPrompt biometricPrompt) {
  //  if (biometricPrompt != null) {
  //    try {
  //      Field field = BiometricPrompt.class.getDeclaredField("mBiometricFragment");
  //      field.setAccessible(true);
  //      BiometricFragment biometricFragment = (BiometricFragment) field.get(biometricPrompt);
  //      if (biometricFragment != null) {
  //        biometricFragment.getLifecycle().addObserver(new LifecycleObserver() {
  //          @OnLifecycleEvent(Lifecycle.Event.ON_CREATE) public void onCreate() {
  //            EMTradeLogger.d(TAG, "hackBio onCreate");
  //          }
  //
  //          @OnLifecycleEvent(Lifecycle.Event.ON_START) public void onStart() {
  //            EMTradeLogger.d(TAG, "hackBio onStart");
  //          }
  //
  //          @OnLifecycleEvent(Lifecycle.Event.ON_RESUME) public void onResume() {
  //            EMTradeLogger.d(TAG, "hackBio onResume");
  //          }
  //
  //          @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE) public void onPause() {
  //            EMTradeLogger.d(TAG, "hackBio onPause");
  //          }
  //
  //          @OnLifecycleEvent(Lifecycle.Event.ON_STOP) public void onStop() {
  //            EMTradeLogger.d(TAG, "hackBio onStop");
  //          }
  //
  //          @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY) public void onDestroy() {
  //            EMTradeLogger.d(TAG, "hackBio onDestroy");
  //          }
  //        });
  //      }
  //    } catch (NoSuchFieldException e) {
  //      e.printStackTrace();
  //    } catch (IllegalAccessException e) {
  //      e.printStackTrace();
  //    }
  //  }
  //}
}
