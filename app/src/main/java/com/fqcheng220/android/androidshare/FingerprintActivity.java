package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.content.DialogInterface;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
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
    findViewById(R.id.tv_fast_auth_then_cancel).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFastManualCancelFingerprintAuth();
      }
    });
    findViewById(R.id.tv_fast_auth_then_cancel2).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFastManualCancelFingerprintAuth2();
      }
    });
    findViewById(R.id.tv_fast_auth_then_cancel_fingerprintmanager).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        testFastManualCancelFingerprintAuthFingerprintManager();
      }
    });
  }

  /**
   * 测试使用android高版本sdk api打开指纹认证页面，
   * 在指纹认证页面还没有初始化好（这里的初始化好指的是HAL还没有初始化好）时就立刻手动关闭指纹认证页面{@link CancellationSignal#cancel()}
   */
  private void testFastManualCancelFingerprintAuth() {
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
      Logger.d(TAG, "testFastManualCancelFingerprintAuth prepare to auto cancel");
      /**
       * Android12 华为p50下 立刻执行取消验证 可能不会取消失败
       * 2023-10-18 14:59:20.278 14255-14255 FingerprintActivity     com.fqcheng220.android.androidshare  D  testFastManualCancelFingerprintAuth prepare to auto cancel
       * 2023-10-18 14:59:20.278 14255-14255 FingerprintActivity     com.fqcheng220.android.androidshare  D  auto cancel
       * 2023-10-18 14:59:20.278  1797-1797  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1156
       * 2023-10-18 14:59:20.278  1797-1797  HwFingerprintService    pid-1797                             D  CODE_GET_USER_ID_FOR_CLONED_PROFILE
       * 2023-10-18 14:59:20.278  1797-1797  BiometricS...reAuthInfo pid-1797                             D  Package: com.fqcheng220.android.androidshare Sensor ID: 0 Modality: 2 Status: 1
       * 2023-10-18 14:59:20.278  1797-1797  BiometricS...reAuthInfo pid-1797                             D  getCanAuthenticateInternal Modality: 2 AuthenticatorStatus: 1
       * 2023-10-18 14:59:20.278  1797-1797  BiometricService        pid-1797                             D  handleAuthenticate: modality(2), status(0), preAuthInfo: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 14:59:20.278  1797-1797  BiometricService        pid-1797                             D  Creating authSession with authRequest: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 14:59:20.278  1797-1797  BiometricS...uthSession pid-1797                             D  Creating AuthSession with: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 14:59:20.279  1797-1797  BiometricS...uthSession pid-1797                             V  set to unknown state sensor: 0
       * 2023-10-18 14:59:20.279  1797-1797  BiometricS...uthSession pid-1797                             V  waiting for cooking for sensor: 0
       * 2023-10-18 14:59:20.279  1797-1797  BiometricS...uthSession pid-1797                             D  sensorId: 0, shouldCancel: true
       * 2023-10-18 14:59:20.279  1797-1797  FingerprintService      pid-1797                             D  cancelAuthenticationFromService, sensorId: 0
       * 2023-10-18 14:59:20.279  1797-1797  Fingerprint21           pid-1797                             D  cancelAuthentication, sensorId: 0
       * 2023-10-18 14:59:20.279  1797-1797  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1139
       * 2023-10-18 14:59:20.279  1797-3056  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1156
       * 2023-10-18 14:59:20.279  1797-3056  HwFingerprintService    pid-1797                             D  CODE_GET_USER_ID_FOR_CLONED_PROFILE
       * 行  234: 2023-10-18 14:59:20.279  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Added] {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, new queue size: 1
       * 	行  235: 2023-10-18 14:59:20.279  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Polled] {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 0
       * 	行  236: 2023-10-18 14:59:20.279  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Starting] {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 0
       * 	行  237: 2023-10-18 14:59:20.279  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Started] {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}
       * 	行  252: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Added] {[585] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=1106766532, userId=0}, new queue size: 1
       * 	行  255: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             V  Not idle, current operation: {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 2
       * 	行  259: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             D  cancelAuthenticationOrDetection, isCorrectClient: false, tokenMatches: false
       * 	行  260: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             D  Marking {[585] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=1106766532, userId=0}, State: 0 as STATE_WAITING_IN_QUEUE_CANCELING
       * 	行  262: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Finishing] {[584] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, success: true
       * 	行  264: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Polled] {[585] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=1106766532, userId=0}, State: 1
       * 	行  265: 2023-10-18 14:59:20.280  1797-3056  BiometricS...gerprint21 pid-1797                             D  [Now Cancelling] {[585] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=1106766532, userId=0}, State: 1
       * 	行  277: 2023-10-18 14:59:20.281  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Finishing] {[585] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=1106766532, userId=0}, success: true
       * 	行  281: 2023-10-18 14:59:20.281  1797-3056  BiometricS...gerprint21 pid-1797                             D  No operations, returning to idle
       */
      /**
       * Android12 华为p50下 如果有延时，但是延时不够长的话，取消系统指纹验证页面可能会报取消被忽略错 Skipping cancellation for non-started operation
       *
       * 2023-10-18 15:04:03.839 15724-15724 FingerprintActivity     com.fqcheng220.android.androidshare  D  testFastManualCancelFingerprintAuth prepare to auto cancel
       * 2023-10-18 15:04:03.839  1797-1797  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1156
       * 2023-10-18 15:04:03.839  1797-1797  HwFingerprintService    pid-1797                             D  CODE_GET_USER_ID_FOR_CLONED_PROFILE
       * 2023-10-18 15:04:03.839  1797-1797  BiometricS...reAuthInfo pid-1797                             D  Package: com.fqcheng220.android.androidshare Sensor ID: 0 Modality: 2 Status: 1
       * 2023-10-18 15:04:03.839  1797-1797  BiometricS...reAuthInfo pid-1797                             D  getCanAuthenticateInternal Modality: 2 AuthenticatorStatus: 1
       * 2023-10-18 15:04:03.839  1797-1797  BiometricService        pid-1797                             D  handleAuthenticate: modality(2), status(0), preAuthInfo: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 15:04:03.839  1797-1797  BiometricService        pid-1797                             D  Creating authSession with authRequest: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 15:04:03.839  1797-1797  BiometricS...uthSession pid-1797                             D  Creating AuthSession with: BiometricRequested: true, StrengthRequested: 255, CredentialRequested: false, Eligible:{0 }, Ineligible:{}, CredentialAvailable: true,
       * 2023-10-18 15:04:03.839  1797-1797  BiometricS...uthSession pid-1797                             V  set to unknown state sensor: 0
       * 2023-10-18 15:04:03.839  1797-1797  BiometricS...uthSession pid-1797                             V  waiting for cooking for sensor: 0
       * 2023-10-18 15:04:03.840  1797-3056  HwFingerprintService    pid-1797                             D  onHwTransact Code = 1156
       * 2023-10-18 15:04:03.840  1797-3056  HwFingerprintService    pid-1797                             D  CODE_GET_USER_ID_FOR_CLONED_PROFILE
       * 行  74: 2023-10-18 15:04:03.840  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Added] {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, new queue size: 1
       * 	行  75: 2023-10-18 15:04:03.840  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Polled] {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 0
       * 	行  76: 2023-10-18 15:04:03.840  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Starting] {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 0
       * 	行  77: 2023-10-18 15:04:03.840  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Started] {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}
       * 	行  83: 2023-10-18 15:04:03.841  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Added] {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, new queue size: 1
       * 	行  86: 2023-10-18 15:04:03.841  1797-3056  BiometricS...gerprint21 pid-1797                             V  Not idle, current operation: {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, State: 2
       * 	行  90: 2023-10-18 15:04:03.841  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Finishing] {[594] FingerprintUpdateActiveUserClient, proto=1, owner=android, cookie=0, userId=0}, success: true
       * 	行  94: 2023-10-18 15:04:03.841  1797-3056  BiometricS...gerprint21 pid-1797                             I  [Polled] {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, State: 0
       * 	行 107: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             D  Waiting for cookie before starting: {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, State: 0
       * 	行 109: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             D  cancelAuthenticationOrDetection, isCorrectClient: true, tokenMatches: true
       * 	行 110: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             D  Cancelling: {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, State: 4
       * 	行 111: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             W  Skipping cancellation for non-started operation: {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, State: 4
       * 	行 112: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             D  No operations, returning to idle
       * 	行 365: 2023-10-18 15:04:04.179  1797-3056  BiometricS...gerprint21 pid-1797                             E  Current operation is null
       *
       * 	特别注意这条日志
       * 行 111: 2023-10-18 15:04:03.842  1797-3056  BiometricS...gerprint21 pid-1797                             W  Skipping cancellation for non-started operation: {[595] FingerprintAuthenticationClient, proto=3, owner=com.fqcheng220.android.androidshare, cookie=527731634, userId=0}, State: 4
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
      new Handler().postDelayed(new Runnable() {
        @Override public void run() {
          Logger.d(TAG, "auto cancel");
          cancellationSignal.cancel();
        }
      }, 0);

      //new Handler().postDelayed(new Runnable() {
      //  @Override public void run() {
      //    hasFloatWindowByToken(FingerprintActivity.this);
      //  }
      //}, 1000);
      /**
       * 结论：
       * 如果使用{@link BiometricPrompt#authenticate(CancellationSignal, Executor, BiometricPrompt.AuthenticationCallback)}
       * 想在某个时机取消指纹验证{@link CancellationSignal#cancel()}的话，建议时间间隔>=500ms
       * 或者直接换{@link FingerprintManager#authenticate(FingerprintManager.CryptoObject, CancellationSignal, int, FingerprintManager.AuthenticationCallback, Handler)}
       * 不需要加延时，任何时机取消都会生效，不会出现系统指纹验证弹窗在取消验证的时候无法自动关闭的问题
       */
    }
  }

  /**
   * 通过{@link BiometricPrompt.Builder#setDeviceCredentialAllowed(boolean)}设置true
   * 也可以解决快速打开指纹验证后快速取消指纹验证失败的问题
   */
  private void testFastManualCancelFingerprintAuth2() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      final CancellationSignal cancellationSignal = new CancellationSignal();
      Executor executor = new MainThreadExecutor();
      final BiometricPrompt biometricPrompt;
      final BiometricPrompt.Builder builder = new BiometricPrompt.Builder(this).setTitle("title");
      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        builder.setDeviceCredentialAllowed(true);
      }
      biometricPrompt = builder.build();
      biometricPrompt.authenticate(cancellationSignal, executor, new BiometricPrompt.AuthenticationCallback() {
        @Override public void onAuthenticationError(int errorCode, CharSequence errString) {
          super.onAuthenticationError(errorCode, errString);
        }
      });
      Logger.d(TAG, "testFastManualCancelFingerprintAuth2 prepare to auto cancel");
      Logger.d(TAG, "auto cancel");
      //cancellationSignal.cancel();
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

  private static class MainThreadExecutor implements Executor {
    private Handler mHandler;

    public MainThreadExecutor() {
      mHandler = new Handler(Looper.getMainLooper());
    }

    @Override public void execute(Runnable command) {
      mHandler.post(command);
    }
  }

  private void testFastManualCancelFingerprintAuthFingerprintManager() {
    androidx.core.os.CancellationSignal cancelSignalCompat = new androidx.core.os.CancellationSignal();
    final FingerprintManagerCompat.CryptoObject cryptoObjectCompat = null;
    final FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(this);
    fingerprintManagerCompat.authenticate(cryptoObjectCompat, 0, cancelSignalCompat, new FingerprintManagerCompat.AuthenticationCallback() {
      @Override public void onAuthenticationError(int errMsgId, CharSequence errString) {
        super.onAuthenticationError(errMsgId, errString);
      }
    }, null);
    Logger.d(TAG, "testFastManualCancelFingerprintAuthFingerprintManager auto cancel");
    cancelSignalCompat.cancel();
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
