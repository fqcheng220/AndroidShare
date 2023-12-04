package com.fqcheng220.android.androidshare;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private Button mBtn_show_dialog;
    private Button mBtn_show_dialog_view;
    private TextView mTv_test_multi_set_text1;
    private TextView mTv_test_multi_set_text2;
    private TextView mTv_test_relativeLayout;
    private TextView mTv_test_fingerprint;
    private TextView mTv_test_linearyLayout;

    private Dialog mDlg;
    private View mViewDlg;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtn_show_dialog = findViewById(R.id.btn_show_dialog);
        mBtn_show_dialog_view = findViewById(R.id.btn_show_dialog_view);
        mTv_test_multi_set_text1 = findViewById(R.id.tv_test_multi_set_text1);
        mTv_test_multi_set_text2 = findViewById(R.id.tv_test_multi_set_text2);
        mTv_test_relativeLayout = findViewById(R.id.tv_test_relativeLayout);
        mTv_test_fingerprint = findViewById(R.id.tv_test_fingerprint);
        mTv_test_linearyLayout = findViewById(R.id.tv_test_linearLayout);

        /**
         * 同一个布局文件，
         * 使用dialog#show相比直接使用DecorView#addView
         * 耗时多了约10-20ms
         *
         * 使用dialog#dismiss相比直接使用DecorView#removeView
         * 也是耗时多了约10-20ms
         */
        mBtn_show_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long curTime = System.currentTimeMillis();
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialog_progress_layout2);
                dialog.show();
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                            long curTime = System.currentTimeMillis();
                            dialogInterface.dismiss();
                            log("dismiss dialog cost time = " + (System.currentTimeMillis() - curTime));
                            return true;
                        }
                        return false;
                    }
                });
                mDlg = dialog;
                log("show dialog cost time = " + (System.currentTimeMillis() - curTime));
            }
        });

        mBtn_show_dialog_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long curTime = System.currentTimeMillis();
                final View decorView = getWindow().getDecorView();
                if (decorView instanceof ViewGroup) {
                    View content = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_progress_layout2, null);
                    ViewGroup.LayoutParams layoutParams =
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT);
                    ((ViewGroup) decorView).addView(content, layoutParams);
                    log("show dialog view cost time = " + (System.currentTimeMillis() - curTime));
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ViewGroup) decorView).removeView(content);
                        }
                    });
                    mViewDlg = content;
                }
            }
        });

        mTv_test_multi_set_text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 100; i++) {
                    mTv_test_multi_set_text1.setText("" + i);
                    log("setText " + i);
                }
            }
        });
        mTv_test_multi_set_text2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < 100; i++) {
                    final int iSeq = i;
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTv_test_multi_set_text2.setText("" + iSeq);
                            log("setText " + iSeq);
                        }
                    });
                }
            }
        });
        mTv_test_relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent().setClass(MainActivity.this, RelativeLayoutActivity.class));
            }
        });
        mTv_test_fingerprint.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                startActivity(new Intent().setClass(MainActivity.this, FingerprintActivity.class));
            }
        });
        mTv_test_linearyLayout.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                startActivity(new Intent().setClass(MainActivity.this, LinearLayoutActivity.class));
            }
        });
    }

    private void log(String msg) {
        android.util.Log.d("MainActivity", msg);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (mViewDlg != null && mViewDlg.getParent() != null && mViewDlg.getParent() instanceof ViewGroup) {
            long curTime = System.currentTimeMillis();
            ((ViewGroup) mViewDlg.getParent()).removeView(mViewDlg);
            log("dismiss dialog view cost time = " + (System.currentTimeMillis() - curTime));
            return;
        }
        super.onBackPressed();
    }

    @Override protected void onStart() {
        super.onStart();
    }

    @Override protected void onResume() {
        super.onResume();
        Rect rect1 = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect1);
        Rect rect2 = new Rect();
        if (mDlg != null) {
            mDlg.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect2);
        }
        android.util.Log.d("MainActivity","onResume");
    }
}