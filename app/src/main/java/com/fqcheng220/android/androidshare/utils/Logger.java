package com.fqcheng220.android.androidshare.utils;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2023/10/17 13:25
 */
public class Logger {
   public static void d(String tag,String msg){
      android.util.Log.d(tag,msg);
   }

   public static void e(String tag,String msg){
      android.util.Log.e(tag,msg);
   }
}
