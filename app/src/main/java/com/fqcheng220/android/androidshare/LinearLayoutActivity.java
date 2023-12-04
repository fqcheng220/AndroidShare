package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;

/**
 * 测量水平方向LinearLayout的各个子view高度
 */
public class LinearLayoutActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linearlayout_horizontal_height);
    }
}
