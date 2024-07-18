package com.fqcheng220.android.androidshare;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.fqcheng220.android.androidshare.utils.Logger;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2024/7/18 9:13
 */
@androidx.annotation.RequiresApi(api = Build.VERSION_CODES.DONUT)
public class MyAccessibilityService extends AccessibilityService {

    private static final String TAG = MyAccessibilityService.class.getCanonicalName();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    private boolean mIsRunningNotifyBuy = false;
    private boolean mIsRunningNotifyEnterLivingRoom = false;
    private volatile boolean mIsLivingRoomMonitorExpired = true;
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(this,"onServiceConnected",Toast.LENGTH_LONG).show();
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
                   initLivingRoom("说点什么...");
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
                if(nodeInfo != null){
                    AccessibilityNodeInfo nodeInfoChatMsgList = findChatMsgListView(nodeInfo.getParent(),1);
                    if (nodeInfoChatMsgList != null) {
                        Logger.d(TAG, "findChatMsgListView return " + nodeInfoChatMsgList);
                        printChatMsgListView(nodeInfoChatMsgList);
                    } else {
                        Logger.e(TAG, "findChatMsgListView return null");
                    }
                }
            }
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
            Thread.sleep(10);
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
                    AccessibilityNodeInfo nodeInfoSbliding = nodeInfo.getParent().getChild(1);
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
}
