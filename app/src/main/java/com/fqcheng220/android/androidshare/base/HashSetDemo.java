package com.fqcheng220.android.androidshare.base;

import android.util.Log;
import com.fqcheng220.android.androidshare.utils.Logger;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * @author fqcheng220
 * @version V1.0
 * @Description: (用一句话描述该文件做什么)
 * @date 2024/3/28 9:19
 */
public class HashSetDemo {
  private static final String TAG = "HashSetDemo";

  public static void main(String[] args) {
    new HashSetDemo().testHashSetTraversal();
  }

  private void testHashSetTraversal() {
    Logger.d(TAG, "testHashSetTraversal");
    Logger.d(TAG,"java "+System.getProperty("java.version"));
    for (int k = 0; k < 1; k++) {
      HashSet<Integer> hashSet = new HashSet();

      for (int j = 0; j < 40; j++) {
        hashSet.add(j);
      }

      int i = 0;
      Iterator<Integer> iterator = hashSet.iterator();
      while (iterator.hasNext()) {
        int cur = iterator.next();
        if (cur != i) {
          Logger.d(TAG, k + " filter " + i + " " + cur);
        } else {
          Logger.d(TAG, k + " match " + i + " " + cur);
        }
        i++;
      }
    }
  }
}
