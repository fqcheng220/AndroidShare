package com.fqcheng220.android.androidshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;


public class TransactionTooLargeExceptionActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_too_large_exception);
        findViewById(R.id.tv1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("key",new byte[1024*1024]);
                intent.setClass(TransactionTooLargeExceptionActivity.this,TransactionTooLargeExceptionActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("key",new byte[512*1024]);
                intent.setClass(TransactionTooLargeExceptionActivity.this,TransactionTooLargeExceptionActivity.class);
                startActivity(intent);
            }
        });
    }
}
