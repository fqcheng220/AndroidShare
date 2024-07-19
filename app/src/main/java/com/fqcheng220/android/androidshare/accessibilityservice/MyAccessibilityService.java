package com.fqcheng220.android.androidshare.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fqcheng220.android.androidshare.utils.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (Android抖音app版本v30.6.0)
 * @date 2024/7/18 9:13
 */
@androidx.annotation.RequiresApi(api = Build.VERSION_CODES.DONUT)
public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getCanonicalName();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    private boolean mIsRunningNotifyBuy = false;
    private boolean mIsRunningNotifyEnterLivingRoom = false;
    private volatile boolean mIsLivingRoomMonitorExpired = true;

    //一个直播间可以复用
    private AccessibilityNodeInfo mNodeInfoChatMsgList;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this,"onServiceConnected",Toast.LENGTH_LONG).show();
        showOverlayView();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
       // 事件触发时调用
       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//          performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT); // 可以进行屏幕截图的动作
//          // 查找并模拟点击
//          List<AccessibilityNodeInfo> nodeInfoList = null;
//          nodeInfoList = getRootInActiveWindow().findAccessibilityNodeInfosByText("button_text");
//          for (AccessibilityNodeInfo nodeInfo : nodeInfoList) {
//             if (nodeInfo != null && nodeInfo.isClickable()) {
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                break;
//             }
//          }
           Logger.d(TAG,"onAccessibilityEvent " + event);
           if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
               if ("com.ss.android.ugc.aweme".equals(event.getPackageName())) {
                   //抖音
//                   while (!isInLivingRoom("木火服饰")) {
//                       try {
//                           Thread.sleep(500);
//                       } catch (InterruptedException e) {
//                           throw new RuntimeException(e);
//                       }
//                   }
                   if(!isInLivingRoom("木火服饰")) {
                       Logger.e(TAG,"isInLivingRoom false");
                       mIsLivingRoomMonitorExpired = true;
                       return;
                   }
                   testDianZan();
//                   testClickOnlineGuest();
                   findChatMsgListViewV2(getRootInActiveWindow());
//                   Logger.d(TAG,"traversalAllViewAndMarkList start");
//                   traversalAllViewAndMarkList(getRootInActiveWindow(),1);
//                   Logger.d(TAG,"traversalAllViewAndMarkList end");
//                   try {
//                       Thread.sleep(30*1000);
//                   } catch (InterruptedException e) {
//                       throw new RuntimeException(e);
//                   }
                   traversalALlWindows();
//                   traversalAllViewAndMarkList(getRootInActiveWindow(),1);
//                   initLivingRoom("说点什么...");
//                   clickAndSendText("说点什么...", "发送", "5xl多大");
                   mIsLivingRoomMonitorExpired = false;
//                   if(!mIsRunningNotifyEnterLivingRoom){
//                       mIsRunningNotifyEnterLivingRoom = true;
//                       mExecutorService.submit(new Runnable() {
//                           @Override
//                           public void run() {
//                               while (!mIsLivingRoomMonitorExpired) {
//                                   CharSequence text = notifyOthersEnterLivingRoom();
//                                   if (TextUtils.isEmpty(text)) {
//                                       Logger.d(TAG, "notifyOthersEnterLivingRoom false waiting...");
//                                       try {
//                                           Thread.sleep(1);
//                                       } catch (InterruptedException e) {
//                                           throw new RuntimeException(e);
//                                       }
//                                   } else {
//                                       Logger.d(TAG, "notifyOthersEnterLivingRoom true " + text);
//                                   }
//                               }
//                               Logger.e(TAG, "thread dead:notifyOthersEnterLivingRoom");
//                           }
//                       });
//                   }
                   CharSequence text = notifyOthersEnterLivingRoom();
                   if (TextUtils.isEmpty(text)) {
                       Logger.d(TAG, "notifyOthersEnterLivingRoom false");
                   } else {
                       Logger.d(TAG, "notifyOthersEnterLivingRoom true " + text);
                   }
//                   if(!mIsRunningNotifyBuy) {
//                       mIsRunningNotifyBuy = true;
//                       mExecutorService.submit(new Runnable() {
//                           @Override
//                           public void run() {
//                               while (!mIsLivingRoomMonitorExpired) {
//                                   CharSequence text = notifyOthersBuy();
//                                   if (TextUtils.isEmpty(text)) {
//                                       Logger.d(TAG, "notifyOthersBuy false waiting...");
//                                       try {
//                                           Thread.sleep(10);
//                                       } catch (InterruptedException e) {
//                                           throw new RuntimeException(e);
//                                       }
//                                   } else {
//                                       Logger.d(TAG, "notifyOthersBuy true " + text);
//                                   }
//                               }
//                               Logger.e(TAG, "thread dead:notifyOthersBuy");
//                           }
//                       });
//                   }
                   text = notifyOthersBuy();
                   if (TextUtils.isEmpty(text)) {
                       Logger.d(TAG, "notifyOthersBuy false");
                   } else {
                       Logger.d(TAG, "notifyOthersBuy true " + text);
                   }
               } else if ("com.eastmoney.android.gubaproj".equals(event.getPackageName())) {
                   //股吧
                   clickAndSendText("请输入交易密码", "发送", "5xl多大");
               }
           }
       }
    }

    @Override
    public void onInterrupt() {
        // 服务被中断时调用
        Logger.e(TAG,"onInterrupt");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.d(TAG,"onUnbind");
        return super.onUnbind(intent);
    }

    private boolean isInLivingRoom(String livingRoomName) {
//        AccessibilityNodeInfo root = getRootInActiveWindow();
//        if (root != null) {
//            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(livingRoomName);
//            return nodeInfoList != null && !nodeInfoList.isEmpty();
//        }
//        return false;
        return true;
    }

    private void initLivingRoom(String editTextTag){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(editTextTag);
            if(nodeInfoList != null && !nodeInfoList.isEmpty()){
                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                if (nodeInfo != null) {
                    AccessibilityNodeInfo nodeInfoChatMsgList = findChatMsgListView(nodeInfo.getParent(),1);
                    if (nodeInfoChatMsgList != null) {
                        mNodeInfoChatMsgList = nodeInfoChatMsgList;
                        Logger.d(TAG, "findChatMsgListView return " + nodeInfoChatMsgList);
                        printChatMsgListView(nodeInfoChatMsgList);
                        return;
                    } else {
                        Logger.e(TAG, "findChatMsgListView return null");
                    }
                }
            }
        }
        if (mNodeInfoChatMsgList != null) {
            Logger.d(TAG, "initLivingRoom share mNodeInfoChatMsgList=" + mNodeInfoChatMsgList);
            printChatMsgListView(mNodeInfoChatMsgList);
        }
    }

    private void clickAndSendText(String editTextTag,String sendBtnTag,String content){
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(editTextTag);
            if(nodeInfoList != null && !nodeInfoList.isEmpty()){
                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                if(nodeInfo != null){
                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    AccessibilityNodeInfo nodeInfoChatMsgList = findChatMsgListView(nodeInfo.getParent(),1);
//                    if (nodeInfoChatMsgList != null) {
//                        Logger.d(TAG, "findChatMsgListView return " + nodeInfoChatMsgList);
//                        printChatMsgListView(nodeInfoChatMsgList);
//                    } else {
//                        Logger.e(TAG, "findChatMsgListView return null");
//                    }
                }
            }
        }
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(editTextTag);
            if(nodeInfoList != null && !nodeInfoList.isEmpty()){
                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                if(nodeInfo != null){
                    Bundle arguments = new Bundle();
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,content);
                    //不能使用这个
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,arguments);
                    AccessibilityNodeInfo nodeInfoSbliding = findFirstView(nodeInfo.getParent(),"android.widget.EditText",1);
                    nodeInfoSbliding.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT,arguments);
                }
            }
        }

        //点击发送按钮
//        root = getRootInActiveWindow();
//        if (root != null) {
//            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText(sendBtnTag);
//            if(nodeInfoList != null && !nodeInfoList.isEmpty()){
//                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
//                if(nodeInfo != null){
//                    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                }
//            }
//        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    Logger.d(TAG,"traversalAllViewAndMarkList start");
                    traversalAllViewAndMarkList(getRootInActiveWindow(),1);
                    Logger.d(TAG,"traversalAllViewAndMarkList end");
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

    private AccessibilityNodeInfo findChatMsgListView(AccessibilityNodeInfo first,final int depth) {
        String tag = "";
        for (int i = 0; i < depth; i++) {
            tag += "-";
        }
        Logger.d(TAG, "findChatMsgListView" + "<" + depth + ">" + tag + first);
        if (first != null) {
            if ("androidx.recyclerview.widget.RecyclerView".equals(first.getClassName())) {
                return first;
            }
            for (int i = 0; i < first.getChildCount(); i++) {
                AccessibilityNodeInfo item = findChatMsgListView(first.getChild(i), depth + 1);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    /**
     * 直接通过id去获取聊天消息列表RecyclerView基本信息
     * @param first
     * @return
     */
    private AccessibilityNodeInfo findChatMsgListViewV2(AccessibilityNodeInfo first) {
        if (first != null) {
            List<AccessibilityNodeInfo> nodeInfoList = first.findAccessibilityNodeInfosByViewId("com.ss.android.ugc.aweme:id/oyg");
            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                Logger.d(TAG, "findChatMsgListViewV2 return " + nodeInfo);
                return nodeInfo;
            }
        }
        Logger.e(TAG, "findChatMsgListViewV2 return null");
        return null;
    }

    private void testClickOnlineGuest(){
        AccessibilityNodeInfo nodeInfo = findOnlineGuest(getRootInActiveWindow());
        if(nodeInfo != null){
            if(!nodeInfo.isClickable() && nodeInfo.getParent() != null && nodeInfo.getParent().isClickable()){
                Logger.d(TAG,"testClickOnlineGuest cannot click,use parent instead if need");
                nodeInfo = nodeInfo.getParent();
            }
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        //打印dialog window viewtree信息
    }

    private AccessibilityNodeInfo findOnlineGuest(AccessibilityNodeInfo first){
        return findByViewId(first,"com.ss.android.ugc.aweme:id/osz");
    }

    private AccessibilityNodeInfo findByViewId(AccessibilityNodeInfo first,String qualifiedId) {
        if (first != null) {
            List<AccessibilityNodeInfo> nodeInfoList = first.findAccessibilityNodeInfosByViewId(qualifiedId);
            if (nodeInfoList != null && !nodeInfoList.isEmpty()) {
                AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                Logger.d(TAG, "findByViewId return " + nodeInfo);
                return nodeInfo;
            }
        }
        Logger.e(TAG, "findByViewId return null");
        return null;
    }


    private AccessibilityNodeInfo findFirstView(AccessibilityNodeInfo first, final String viewClzName, final int depth) {
        if (TextUtils.isEmpty(viewClzName)) return null;
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            tag.append("-");
        }
        Logger.d(TAG, "findFirstView" + "<" + depth + ">" + tag + first);
        if (first != null) {
            if (viewClzName.equals(first.getClassName())) {
                return first;
            }
            for (int i = 0; i < first.getChildCount(); i++) {
                AccessibilityNodeInfo item = findFirstView(first.getChild(i), viewClzName, depth + 1);
                if (item != null) {
                    return item;
                }
            }
        }
        return null;
    }

    private void traversalAllViewAndMarkList(AccessibilityNodeInfo first,final int depth) {
        String tag = "";
        for (int i = 0; i < depth; i++) {
            tag += "--";
        }
        Logger.d(TAG, "traversalAllViewAndMarkList" + "<" + depth + ">" + tag + first);
        if (first != null) {
            if ("androidx.recyclerview.widget.RecyclerView".equals(first.getClassName())) {
                Logger.d(TAG, "traversalAllViewAndMarkList RecyclerView " + tag + "<" + depth + ">" + first);
            }
            for (int i = 0; i < first.getChildCount(); i++) {
                traversalAllViewAndMarkList(first.getChild(i), depth + 1);
            }
        }
    }

    private void traversalALlWindows(){
        List<AccessibilityWindowInfo> windowInfos = getWindows();
        if(windowInfos != null){
            for(int i=0;i<windowInfos.size();i++){
                Logger.d(TAG, "traversalALlWindows " + i + " " + windowInfos.get(i).getRoot());
            }
        }
    }

    /**
     * 测试连续快速点击点赞
     */
    private void testDianZan(){
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        for (int i = 0; i < 5; i++) {
            clickByNode(this, displayMetrics.widthPixels / 2.0f, displayMetrics.heightPixels / 2.0f);
        }
    }

    private static void clickByNode(AccessibilityService accessibilityService,float x,float y) {
        if (accessibilityService != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            GestureDescription.Builder builder = new GestureDescription.Builder();
            Path path = new Path();
            path.moveTo(x, y);
            builder.addStroke(new GestureDescription.StrokeDescription(path, 0, 1));
            boolean bRtn = accessibilityService.dispatchGesture(builder.build(), new GestureResultCallback() {
                @Override
                public void onCompleted(GestureDescription gestureDescription) {
                    Logger.d(TAG, "clickByNode onCompleted " + gestureDescription);
                }

                @Override
                public void onCancelled(GestureDescription gestureDescription) {
                    Logger.d(TAG, "clickByNode onCancelled " + gestureDescription);
                }
            }, null);
            Logger.d(TAG, "clickByNode " + bRtn);
        }
    }

    private void printChatMsgListView(AccessibilityNodeInfo node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo item = node.getChild(i);
//            Logger.d(TAG, "printChatMsgListView " + i + " " + item);
            Logger.d(TAG, "printChatMsgListView " + i + " " + (item != null ? item.getText() : ""));
        }
    }

    private CharSequence notifyOthersEnterLivingRoom() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText("加入了直播间");
            if (nodeInfoList != null && !nodeInfoList.isEmpty() && nodeInfoList.get(0) != null) {
                return nodeInfoList.get(0).getText();
            }
        }
        return "";
    }

    private CharSequence notifyOthersBuy() {
        AccessibilityNodeInfo root = getRootInActiveWindow();
        if (root != null) {
            List<AccessibilityNodeInfo> nodeInfoList = root.findAccessibilityNodeInfosByText("正在去购买");
            if (nodeInfoList != null && !nodeInfoList.isEmpty() && nodeInfoList.get(0) != null) {
                return nodeInfoList.get(0).getText();
            }
        }
        return "";
    }

    /**
     * 显示悬浮窗
     */
    private void showOverlayView() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        ViewGroup viewGroup = new FrameLayout(this);
        viewGroup.setBackgroundColor(Color.RED);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyAccessibilityService.this, "onClick", Toast.LENGTH_LONG).show();
            }
        });
        /**
         * 错误示范
         * 经常犯的错误，以为{@link WindowManager.LayoutParams(int _type, int _flags)}带两个整型参数构造函数的两个参数是宽度和高度，其实是type和flags
         * 这段代码执行完后，layoutParams.flags异常
         */
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.width = 200;
//        layoutParams.height = 200;
//        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
//        layoutParams.format = PixelFormat.TRANSLUCENT;
//        layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        /**
         * 正确示范
         */
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = 200;
        layoutParams.height = 200;
        layoutParams.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        layoutParams.format = PixelFormat.TRANSLUCENT;
        layoutParams.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        windowManager.addView(viewGroup, layoutParams);
    }
}
